package com.dxn.rpc.common;

import lombok.Data;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Data
@ToString
public class Request {
    private final long id;
    private final AtomicLong aid = new AtomicLong(1);
    private Object content;
    private String methodName;

    public Request() {
        id = aid.incrementAndGet();
    }
}
