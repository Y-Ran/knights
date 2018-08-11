package com.yiran.lancelot;

import com.yiran.lancelot.config.LancelotApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class LancelotMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(LancelotMain.class);

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(LancelotApplication.class);
        Handle handle = (Handle) applicationContext.getBean("handle");
        LOGGER.info("日志测试输出");
        LOGGER.info("1 + 2 = {}", handle.add(1, 2));
        LOGGER.info("say hello : {}", handle.sayHello("tom"));
    }

}
