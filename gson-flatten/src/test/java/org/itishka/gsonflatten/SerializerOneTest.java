package org.itishka.gsonflatten;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Tishka17 on 20.05.2016.
 */
public class SerializerOneTest {
    private class ClassFlat {
        @Flatten("x::y")
        int test;
    }

    private class ClassInner {
        int y;
    }

    private class ClassComplex {
        Integer y;
        Integer test;
        ClassInner x;
    }

    @Test
    public void test_serialize_one() {
        ClassFlat one = new ClassFlat();
        one.test = 13;

        final Gson gson = Helper.createFlatteningGson();
        final Gson gson_default = Helper.createDefaultGson();

        String res = gson.toJson(one);
        assertNotNull(res);
        assertNotEquals("", res);
        ClassComplex complex = gson_default.fromJson(res, ClassComplex.class);
        assertNotNull(complex.x);
        assertEquals(complex.x.y, one.test);
        assertNull(complex.test);
        assertNull(complex.y);
    }
}
