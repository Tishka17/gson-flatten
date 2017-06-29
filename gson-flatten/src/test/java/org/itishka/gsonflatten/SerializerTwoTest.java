package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Tishka17 on 20.05.2016.
 */
public class SerializerTwoTest {
    private class ClassFlat {
        @Flatten("x::y")
        int testY;
        @Flatten("x::z")
        int testZ;
        @SerializedName("no")
        Integer testNo;
        @Flatten("array::0::y")
        int testArrayY;
        @Flatten("deep::0::10::0::x::1::z")
        String testArrayZ;
    }

    private class ClassInner {
        int y;
        int z;
    }

    private class ClassComplex {
        Integer y;
        Integer testY;
        Integer testZ;
        Integer no;
        ClassInner x;
        List<ClassInner> array;
        JsonArray deep;
    }

    @Test
    public void test_serialize_two() {
        ClassFlat one = new ClassFlat();
        one.testY = 13;
        one.testY = 666;
        one.testNo = -1;
        one.testArrayY = 42;
        one.testArrayZ = "foo";
        final Gson gson = Helper.createFlatteningGson();
        final Gson gson_default = Helper.createDefaultGson();

        String res = gson.toJson(one);
        assertNotNull(res);
        assertNotEquals("", res);
        ClassComplex complex = gson_default.fromJson(res, ClassComplex.class);
        assertNotNull(complex.x);
        assertEquals(one.testY, complex.x.y);
        assertEquals(one.testZ, complex.x.z);
        assertEquals(one.testNo, complex.no);
        assertEquals(one.testArrayY, complex.array.get(0).y);
        assertNull(complex.testY);
        assertNull(complex.testZ);
        assertNull(complex.y);

        assertEquals(1, complex.deep.size());
        assertEquals(JsonArray.class, complex.deep.get(0).getClass());
        assertEquals("foo", complex.deep
                .get(0).getAsJsonArray()
                .get(10).getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("x").getAsJsonArray()
                .get(1).getAsJsonObject()
                .get("z").getAsString());
    }


}
