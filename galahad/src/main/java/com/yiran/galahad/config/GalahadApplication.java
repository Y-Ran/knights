package com.yiran.galahad.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration("galahad-spring-config")
@ComponentScan(basePackages = {"com.yiran.galahad"})
public class GalahadApplication {
}
