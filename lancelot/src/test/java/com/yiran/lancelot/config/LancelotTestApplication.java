package com.yiran.lancelot.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration("lancelot-test-spring-config")
@ComponentScan(basePackages = {"com.yiran.lancelot"})
public class LancelotTestApplication {
}
