package com.dxn.rpc.common;

import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class DefaultFuture {
    public static final Map<Long, DefaultFuture> requestMap = new ConcurrentHashMap<>();
    final Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private volatile Response response;


    private Long timeout = 5 * 30 * 1000L;
    private Long startTime = System.currentTimeMillis();


    public DefaultFuture(Request request) {
        requestMap.put(request.getId(), this);
    }

    public static void recive(Response response) {
        DefaultFuture defaultChannel = requestMap.get(response.getId());
        if (defaultChannel != null) {
            defaultChannel.lock.lock();
            try {
                defaultChannel.response = response;
                defaultChannel.condition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                defaultChannel.lock.unlock();
            }
        }
    }

    public Response get(Long time) {
        lock.lock();
        try {
            while (!done()) {
                condition.await(time, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
        return response;
    }

    private boolean done() {
        if (response != null) {
            return true;
        }
        return false;
    }

    static class FurtureTimeOutTask extends Thread {

        @Override
        public void run() {
            Set<Long> ids = requestMap.keySet();
            for (Long id : ids) {
                DefaultFuture defaultFuture = requestMap.get(id);
                if (defaultFuture == null) {
                    requestMap.remove(id);
                } else {
                    // 超时处理
                    if (defaultFuture.timeout < System.currentTimeMillis() - defaultFuture.startTime) {
                        Response response = new Response();
                        response.setId(id);
                        response.setCode(10002);
                        response.setMsg("已经超时");
                        recive(response);
                    }
                }
            }
        }
    }

    static {
        FurtureTimeOutTask furtureTimeOutTask = new FurtureTimeOutTask();
        Thread thread = new Thread(furtureTimeOutTask);
        thread.setDaemon(true);
        furtureTimeOutTask.start();
    }

    static {
    }

}
