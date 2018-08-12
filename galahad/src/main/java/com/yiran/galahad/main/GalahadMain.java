package com.yiran.galahad.main;



import com.yiran.galahad.Galahad;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;



public class GalahadMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Galahad.of()
                .setScanPackages("com.yiran")
                .init();
    }


}
