package com.dxn.rpc.component;

import com.dxn.rpc.common.Const;
import com.dxn.rpc.server.SimpleHandler;
import com.dxn.rpc.util.ZkFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Component
public class InitNetty implements ApplicationListener<ContextRefreshedEvent> {

    protected static void bindConnectionOptions(ServerBootstrap bootstrap) {

        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.childOption(ChannelOption.SO_LINGER, 0);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true); //调试用
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //心跳机制暂时使用TCP选项，之后再自己实现

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel)
                            throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("delimiter", new DelimiterBasedFrameDecoder(65535, Unpooled.wrappedBuffer(new byte[]{'\r', '\n'})));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast("idle", new IdleStateHandler(20, 8, 5, TimeUnit.SECONDS));
                        pipeline.addLast("simpleHander", new SimpleHandler());
                        System.out.println(" initChannel pipeline ");
                    }
                });


        bindConnectionOptions(bootstrap);

        int port = 8083;
        try {
            ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    System.out.println("绑定成功 ");
                }
            });
            InetAddress localHost = InetAddress.getLocalHost();
            CuratorFramework zk = ZkFactory.getInstance();
//            zk.create().withMode(CreateMode.EPHEMERAL).forPath(Const.SERVER_PATH);
            zk.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Const.SERVER_PATH + "/" + localHost.getHostAddress() + "#" + port + "#");
            channelFuture.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("channel 退出");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
