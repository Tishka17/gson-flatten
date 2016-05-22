package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Tishka17 on 22.05.2016.
 */
public class Helper {
    public static Gson createFlatteningGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();
    }

    public static Gson createDefaultGson() {
        return new Gson();
    }
}
