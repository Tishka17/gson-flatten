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
        @Flatten("arr::0")
        int testArray1;
        @Flatten("arr::1::x")
        int testArray2;
        @Flatten("arr::0::x")
        int testArray3;
        @SerializedName("no")
        int testNo;
        @Flatten("arr::nan")
        int testBarParse1;
        @Flatten("arr::4")
        int testBarParse2;
    }

    @Test
    public void test_parse_two() {
        String one = "{'x':{'y':1, 'z':2}, 'no':-1, 'arr':['3', {'x': 4}]}";
        final Gson gson = Helper.createFlatteningGson();
        ClassTwo classTwo = gson.fromJson(one, ClassTwo.class);
        assertEquals(1, classTwo.testY);
        assertEquals(2, classTwo.testZ);
        assertEquals(-1, classTwo.testNo);
        assertEquals(3, classTwo.testArray1);
        assertEquals(4, classTwo.testArray2);
        assertEquals(0, classTwo.testArray3);
        assertEquals(0, classTwo.testBarParse1);
        assertEquals(0, classTwo.testBarParse2);
    }

}
