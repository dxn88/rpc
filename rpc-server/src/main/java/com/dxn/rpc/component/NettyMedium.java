package com.dxn.rpc.component;

import com.dxn.rpc.annotation.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 用于把netty数据传到业务controller
 */
@Component
public class NettyMedium implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Remote.class)) {
            System.out.println("bean = " + bean);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                String name = bean.getClass().getInterfaces()[0].getName() + "." + method.getName();
                Medium.name2Method.put(name, new BeanMethod(bean, method));
            }
        }
        return null;
    }
}
