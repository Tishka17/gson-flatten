package org.itishka.gsonflatten;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 03.07.2016.
 */
public class PerformanceSerializingTest {

    private class ClassFlat {
        @Flatten("x::y")
        int testY;
        @Flatten("x::z")
        int testZ;
    }

    ClassFlat []array = new ClassFlat[1000000];
    private int count = 1000;

    @Before
    public void prepare() {
        for (int i =0; i<array.length; i++) {
            ClassFlat a = new ClassFlat();
            a.testY = 1;
            a.testZ = 2;
        }
    }


    @Test
    public void flattenBenchmark() {
        Long a = System.currentTimeMillis();
        final Gson gson = Helper.createFlatteningGson();
        for (int i=0;i<count;i++) {
            String s = gson.toJson(array);
        }
        System.out.println("Duration flattenBenchmark: "+(System.currentTimeMillis()-a));
    }

    @Test
    public void defaultBenchmark() {
        Long a = System.currentTimeMillis();
        final Gson gson = Helper.createDefaultGson();
        for (int i=0;i<count;i++) {
            String s = gson.toJson(array);
        }
        System.out.println("Duration defaultBenchmark: "+(System.currentTimeMillis()-a));
    }
}
