package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

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
        final Gson gson = Helper.createFlatteningGson();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.test, 1);
    }


    private class ClassTwo {
        @Flatten("x::y")
        int testY;
        @Flatten("x::z")
        int testZ;
        @SerializedName("no")
        int testNo;
    }

    @Test
    public void test_parse_two() {
        String one = "{'x':{'y':1, 'z':2}, 'no':-1}";
        final Gson gson = Helper.createFlatteningGson();
        ClassTwo classTwo = gson.fromJson(one, ClassTwo.class);
        assertEquals(1, classTwo.testY);
        assertEquals(2, classTwo.testZ);
        assertEquals(-1, classTwo.testNo);
    }
}
