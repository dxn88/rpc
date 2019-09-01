package com.dxn.rpc.util;

import com.dxn.rpc.common.Response;

public class ResponseUtil {

    public static Response createSuccessResponse() {
        return new Response(0, "成功");
    }
    public static Response createErrorResponse() {
        return new Response(10001, "没有此方法");
    }

    public static Response createSuccessResponse(Object content) {
        return new Response(0, "成功", content);
    }

    public static Response createResponse(Integer code, String msg) {
        return new Response(code, msg);
    }

    public static Response createResponse(Integer code, String msg, Object content) {
        return new Response(code, msg, content);
    }
}
