package com.wjl.netty.dubborpc.publicinterface;

import java.lang.reflect.Proxy;

//这个是接口，是服务提供方和 服务消费方都需要
public interface HelloService {

    String hello(String mes);

    public static void main(String[] args) {
        HelloService helloService = (HelloService)A.getBean(HelloService.class,"你好");
        System.out.println(helloService.getClass());
        System.out.println(helloService.hello("Hello"));
    }

     class A{
        public static  Object getBean(final Class c,String name){
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{c},(proxy,method,args)->{
                System.out.println("method==>"+method.getName());
                return  name+args[0];
            });
        }

    }
}
