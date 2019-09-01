package com.dxn.rpc.component;

import com.alibaba.fastjson.JSONObject;
import com.dxn.rpc.common.Request;
import com.dxn.rpc.common.Response;
import com.dxn.rpc.util.ResponseUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Medium {
    public static Map<String, BeanMethod> name2Method = new HashMap();


    public static Response process(Request request) {
        Response response = null;
        try {
            String methodName = request.getMethodName();
            BeanMethod beanMethod = name2Method.get(methodName);
            if (beanMethod == null) {
                Response errorResponse = ResponseUtil.createErrorResponse();
                errorResponse.setId(request.getId());
                return errorResponse;
            }
            Object bean = beanMethod.getBean();
            Method method = beanMethod.getMethod();
            Class<?> parameterType = method.getParameterTypes()[0];
            Object content = request.getContent();
            Object param = JSONObject.parseObject(JSONObject.toJSONString(content), parameterType);
            Object result = method.invoke(bean, param);
            response = ResponseUtil.createSuccessResponse(result);
            response.setId(request.getId());
        } catch (Exception e) {
            response.setId(request.getId());
            response.setCode(100001);
            response.setMsg(e.getMessage());
        }

        return response;
    }
}
