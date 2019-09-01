package com.dxn.rpc.client;

import io.netty.channel.ChannelFuture;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class ChannelManager {
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();
    public static Set<String> realServerPath = new HashSet<>();


    public static void removeChannel(ChannelFuture channelFuture) {
        channelFutures.remove(channelFuture);
    }

    public static void addChannel(ChannelFuture channelFuture) {
        channelFutures.add(channelFuture);
    }

    public static void clear() {
        channelFutures.clear();
    }

    public static CopyOnWriteArrayList<ChannelFuture> getChannelFutures() {
        return channelFutures;
    }

    public static int position = 0;
    public static ChannelFuture getChannelFuture() {
        int size = channelFutures.size();
        if (position >= size) {
            position = position%size;
        }
        ChannelFuture channelFuture = channelFutures.get(position);
        position++;
        return channelFuture;
    }
}
