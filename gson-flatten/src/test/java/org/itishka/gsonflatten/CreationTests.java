package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreationTests {
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

}