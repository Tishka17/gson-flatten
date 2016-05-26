package org.itishka.gsonflatten;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tishka17 on 26.05.2016.
 */
public class NamingTest {

    private class ClassOne {
        @Flatten("x::y")
        int testY;
    }

    @Test
    public void test_parse_one() {
        String one = "{'x':{'y':1}}";
        final Gson gson = Helper.createFlatteningGson();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.testY, 1);
    }

    @Test
    public void test_parse_lower_dashes() {
        String one = "{'x':{'y':1}}";
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory(FieldNamingPolicy.LOWER_CASE_WITH_DASHES))
                .create();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.testY, 1);
    }

    @Test
    public void test_parse_upper_camel() {
        String one = "{'x':{'y':1}}";
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory(FieldNamingPolicy.UPPER_CAMEL_CASE))
                .create();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.testY, 1);
    }

}
