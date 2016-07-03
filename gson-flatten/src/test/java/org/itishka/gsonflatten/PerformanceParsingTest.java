package org.itishka.gsonflatten;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.awt.SystemTray;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 03.07.2016.
 */
public class PerformanceParsingTest {

    private class ClassFlat {
        @Flatten("x::y")
        int testY;
        @Flatten("x::z")
        int testZ;
    }

    private class ClassComplex {
        Integer testY;
        Integer testZ;
    }

    private String json;
    private int count = 1000;

    @Before
    public void prepare() {
        final Gson gson = Helper.createFlatteningGson();
        ClassFlat []array = new ClassFlat[1000000];
        for (int i =0; i<array.length; i++) {
            ClassFlat a = new ClassFlat();
            a.testY = 1;
            a.testZ = 2;
        }
        json = gson.toJson(array);
    }


    @Test
    public void flattenBenchmark() {
        Long a = System.currentTimeMillis();
        final Gson gson = Helper.createFlatteningGson();
        ArrayList<ClassComplex> la = new ArrayList<>();
        for (int i=0;i<count;i++) {
            List<ClassComplex> list = gson.fromJson(json, la.getClass());
        }
        System.out.println("Duration flattenBenchmark: "+(System.currentTimeMillis()-a));
    }

    @Test
    public void defaultBenchmark() {
        Long a = System.currentTimeMillis();
        ArrayList<ClassComplex> la = new ArrayList<>();
        final Gson gson = Helper.createDefaultGson();
        for (int i=0;i<count;i++) {
            List<ClassComplex> list = gson.fromJson(json, la.getClass());
        }
        System.out.println("Duration defaultBenchmark: "+(System.currentTimeMillis()-a));
    }

}
