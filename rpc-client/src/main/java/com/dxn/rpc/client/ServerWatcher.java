package com.dxn.rpc.client;

import com.dxn.rpc.common.Const;
import com.dxn.rpc.util.ZkFactory;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;

public class ServerWatcher implements CuratorWatcher {

    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        String path = watchedEvent.getPath();
        CuratorFramework client = ZkFactory.getInstance();
        client.getData().usingWatcher(this).forPath(Const.SERVER_PATH);
        List<String> serverPaths = client.getChildren().forPath(path);
        ChannelManager.realServerPath.clear();
        ChannelManager.clear();
        for (String serverPath : serverPaths) {
            String[] split = serverPath.split("#");
            String host = split[0];
            Integer port = Integer.parseInt(split[1]);
            ChannelManager.realServerPath.add(host + port);

            ChannelFuture channelFuture = Client.b.connect(host, port);
            ChannelManager.addChannel(channelFuture);
        }
    }
}
