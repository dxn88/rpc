package com.dxn.rpc.client;

import com.alibaba.fastjson.JSONObject;
import com.dxn.rpc.common.Const;
import com.dxn.rpc.common.DefaultFuture;
import com.dxn.rpc.common.Request;
import com.dxn.rpc.common.Response;
import com.dxn.rpc.util.ZkFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Hello world!
 */
public class Client {
    public static final int clientNum = Integer.parseInt(System.getProperty("size", "1"));
    public static final int frequency = 100;  //ms
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8083"));
//    public static ChannelFuture cf = null;

    public static  Bootstrap b;
    public static  EventLoopGroup group ;
    static {
        try {
            beginPressTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void beginPressTest() throws InterruptedException {
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("delimiter", new DelimiterBasedFrameDecoder(65535, Unpooled.wrappedBuffer(new byte[]{'\r', '\n'})));
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast("simpleHander", new ClientHandler());
                        System.out.println(" client initChannel pipeline ");
                    }
                });


        try {
            // Start the client.
            for (int i = 1; i <= clientNum; i++) {
                startConnection(b, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


    }

    private static void startConnection(Bootstrap b, int index) throws InterruptedException {
        // 获取服务器地址
        CuratorFramework client = ZkFactory.getInstance();
        String host = "localhost";
        int port = 8083;
        try {
            List<String> servers = client.getChildren().forPath(Const.SERVER_PATH);
            // 加上监听
            CuratorWatcher watcher = new ServerWatcher();
            client.getData().usingWatcher(watcher).forPath(Const.SERVER_PATH);
            for (String server : servers) {
                String[] split = server.split("#");
                ChannelManager.realServerPath.add(split[0] + "#" + split[1]);
//                String hostAndPort = ChannelManager.realServerPath.toArray()[0].toString();
//                String[] split = hostAndPort.split("#");
                host = split[0];
                port = Integer.parseInt(split[1]);
                ChannelFuture channelFuture = Client.b.connect(host, port);
// 管理 channel
                ChannelManager.addChannel(channelFuture);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("bbbbbbbbbbbbbbbbbbb = exit ");
    }

    public static Response send(Request request) {
        ChannelFuture cf = ChannelManager.getChannelFuture();

        cf.channel().writeAndFlush(JSONObject.toJSONString(request));
        cf.channel().writeAndFlush("\r\n");
        DefaultFuture defaultChannel = new DefaultFuture(request);
        Response response = defaultChannel.get(30000L);
        return response;
    }


//    public static void close() {
//        cf.channel().close();
//    }

}
