package com.dxn.rpc.server;

import com.alibaba.fastjson.JSONObject;
import com.dxn.rpc.common.Request;
import com.dxn.rpc.common.Response;
import com.dxn.rpc.component.Medium;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SimpleHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client come !");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg = " + msg);
        if (msg.toString().equals("pong")) {
            return;
        }
        Request request = JSONObject.parseObject(msg.toString(), Request.class);
        Response response = Medium.process(request);
        System.out.println("response = " + response);
        ctx.channel().writeAndFlush(JSONObject.toJSONString(response));
        ctx.channel().writeAndFlush("\r\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause = " + cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲");
                ctx.channel().close();
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                System.out.println("写空闲");
            } else if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                ctx.writeAndFlush("ping\r\n");
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
