package com.dxn.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private long id;

    private Object content;

    private String msg;

    private Integer code;

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(Integer code, String msg, Object content) {
        this(code, msg);
        this.content = content;
    }
}
