package com.wjl.spring.X45_SpringProxyFeatures;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class T1 {
    protected T2 t2;

    protected boolean initialized;

    @Autowired
    public void setT2(T2 t2) {
        log.info("setT2(T2 t2)");
        this.t2 = t2;
    }

    @PostConstruct
    public void init() {
        log.info("init");
        initialized = true;
    }

    public T2 getT2() {
        log.info("getT2()");
        return t2;
    }

    public boolean isInitialized() {
        log.info("isInitialized()");
        return initialized;
    }

    public void m1() {
        System.out.println("m1() 成员方法");
    }

    final public void m2() {
        System.out.println("m2() final 方法");
    }

    static public void m3() {
        System.out.println("m3() static 方法");
    }

    private void m4() {
        System.out.println("m4() private 方法");
    }

}
