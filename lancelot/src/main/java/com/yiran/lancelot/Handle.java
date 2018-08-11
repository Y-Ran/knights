package com.yiran.lancelot;

import org.springframework.stereotype.Component;

@Component
public class Handle {

    public int add(int x, int y) {
        return x + y;
    }

    public String sayHello(String name) {
        return "hello " + name;
    }

}
