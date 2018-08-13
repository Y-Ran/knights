package com.yiran.lancelot.example;

import com.yiran.galahad.utils.ClassKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LancelotMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(LancelotMain.class);

    public static void main(String[] args) throws IOException {

        ClassKit classKit = new ClassKit();

        classKit.getAllClasses("com.yiran").forEach(classInfo -> {
            LOGGER.info("className : {}", classInfo.getClassName());
        });

    }

}
