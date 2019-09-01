package com.dxn.rpc.proxy;

import com.dxn.rpc.annotation.Invoke;
import com.dxn.rpc.client.Client;
import com.dxn.rpc.common.Request;
import com.dxn.rpc.common.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class InvokeProxy implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Invoke.class)) {
                field.setAccessible(true);

                final Map<Method, Class> methodClassMap = new HashMap<>();
                putMethodClassMap(methodClassMap, field);
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new  Class[]{field.getType()});
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        // 调用服务器
                        Request request = new Request();
                        request.setContent(args[0]);
                        request.setMethodName(methodClassMap.get(method).getName() + "." + method.getName());
                        Response result = Client.send(request);
                        return result.getContent();
                    }
                });
                try {
                    field.set(bean, enhancer.create());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private void putMethodClassMap(Map<Method, Class> methodClassMap, Field field) {
        Method[] declaredMethods = field.getType().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            methodClassMap.put(declaredMethod, field.getType());
        }

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return null;
    }
}
