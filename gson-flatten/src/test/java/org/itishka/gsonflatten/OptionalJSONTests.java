package org.itishka.gsonflatten;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Created by Tishka17 on 20.05.2016.
 */
public class OptionalJSONTests {
    private class Flat {
        @Flatten("root::key1")
        String key1;
        @Flatten("root::key2")
        String key2;
        @Flatten("root::key3")
        String key3;
    }

    @Test
    public void testParse() {
        String json = "{'root':{'key1':'string'}}";
        final Gson gson = Helper.createFlatteningGson();
        Flat flat = gson.fromJson(json, Flat.class);
        assertNull(flat.key2);
        assertNull(flat.key3);
    }
}
