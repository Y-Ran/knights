package com.yiran.galahad.utils;

import com.yiran.galahad.annotation.Component;
import com.yiran.galahad.bean.ClassInfo;
import com.yiran.test.Child;
import com.yiran.test.Person;
import com.yiran.test.Thing;
import com.yiran.test.Grand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassKitTest {

    ClassKit classKit;

    @Before
    public void initTest() {
        this.classKit = new ClassKit();
    }

    @Test
    public void getClassInfoTest() throws IOException {
        Set<ClassInfo> result = this.classKit.getClassInfo("com.yiran.test", null, Component.class, true);
        Set<ClassInfo> expect = new HashSet<>(Arrays.asList(new ClassInfo(Person.class), new ClassInfo(Grand.class)));
        Assert.assertEquals(expect, result);

        result = this.classKit.getClassInfo("com.yiran.test", null, null, true);
        expect = new HashSet<>(Arrays.asList(new ClassInfo(Person.class), new ClassInfo(Thing.class), new ClassInfo(Grand.class), new ClassInfo(Child.class)));
        Assert.assertEquals(expect, result);

        result = this.classKit.getClassInfo("com.yiran.test", Person.class, null, true);
        expect = new HashSet<>(Collections.singletonList(new ClassInfo(Child.class)));
        Assert.assertEquals(expect, result);

        result = this.classKit.getClassInfo("com.yiran.test", Person.class, Component.class, true);
        expect = new HashSet<>();
        Assert.assertEquals(expect, result);
    }

}
