package org.itishka.gsonflatten;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tishka17 on 20.05.2016.
 */
public class AutoNamingTests {
    private class Flat {
        @Flatten("x::")
        int y;
    }

    private class Inner {
        int y;
    }

    private class Complex {
        Inner x;
    }

    @Test
    public void testParse() {
        String json = "{'x':{'y':18}}";
        final Gson gson = Helper.createFlatteningGson();
        Flat flat = gson.fromJson(json, Flat.class);
        assertEquals(18, flat.y);
    }

    @Test
    public void testSerialize() {
        final Gson gson = Helper.createFlatteningGson();
        final Gson default_gson = Helper.createDefaultGson();

        Flat flat = new Flat();
        flat.y = 5;
        Complex complex = default_gson.fromJson(gson.toJson(flat), Complex.class);
        assertEquals(flat.y, complex.x.y);
    }
}
