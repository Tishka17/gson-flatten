package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlattenTest {
    private class ClassOne {
        @Flatten("x::y")
        int test;
    }

    @Test
    public void test_annotation() {
        ClassOne one = new ClassOne();
        one.test = 0;
        assertEquals(one.test, 0);
    }

    @Test
    public void test_create_gson() {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
        assertNotNull(gson);
    }

    @Test
    public void test_parse_one() {
        String one = "{'x':{'y':1}}";
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
        ClassOne classOne = gson.fromJson(one, ClassOne.class);
        assertEquals(classOne.test,1);
    }
    @Test
    public void test_serialize_one() {
        ClassOne one = new ClassOne();
        one.test=13;
        final Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
        String res = gson.toJson(one);
        assertTrue(res.contains("x"));
        assertTrue(res.contains("y"));

    }
}