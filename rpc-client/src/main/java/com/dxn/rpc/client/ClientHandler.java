package com.dxn.rpc.client;

import com.alibaba.fastjson.JSONObject;
import com.dxn.rpc.common.DefaultFuture;
import com.dxn.rpc.common.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("ClientHandler.channelActive");
//        ctx.writeAndFlush("hello \r\n \r\n \r\n");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg.toString().equals("ping")) {
            System.out.println("msg = " + msg.toString());
            ctx.writeAndFlush("pong\r\n");
            return;
        }
        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        DefaultFuture.recive(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
