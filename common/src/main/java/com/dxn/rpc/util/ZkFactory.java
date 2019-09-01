package com.dxn.rpc.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkFactory {

    private static CuratorFramework curatorFramework = createZk();

    private static CuratorFramework createZk() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework framework = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        framework.start();

        return framework;

    }

    public static CuratorFramework getInstance() {

        return curatorFramework;
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework instance = ZkFactory.getInstance();
        instance.create().forPath("/hello", "world".getBytes());
    }


}
