package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tishka17 on 20.05.2016.
 */
public class ParserTests {
    private class ClassOne {
        @Flatten("x::y")
        int test;
    }

    @Test
    public void test_parse_one() {
        String one = "{'x':{'y':1}}";
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.test, 1);
    }


    private class ClassTwo {
        @Flatten("x::y")
        int testY;
        @Flatten("x::z")
        int testZ;
    }

    @Test
    public void test_parse_two() {
        String one = "{'x':{'y':1, 'z':2}}";
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
        ClassTwo classTwo = gson.fromJson(one, ClassTwo.class);
        assertEquals(classTwo.testY, 1);
        assertEquals(classTwo.testZ, 2);
    }
}