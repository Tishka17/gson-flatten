package org.itishka.gsonflatten;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tishka17 on 18.05.2016.
 */
public class FlattenTypeAdapterFactory implements TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> defaultAdapter = gson.getAdapter(JsonElement.class);
        final Map<Field, FlattenCacheItem> cache = buildCache(type.getRawType(), gson);

        TypeAdapter<T> result = new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (cache.isEmpty())
                    return delegateAdapter.read(in);
                JsonElement rootElement = defaultAdapter.read(in);
                if (!rootElement.isJsonObject())
                    return delegateAdapter.fromJsonTree(rootElement);
                JsonObject root = rootElement.getAsJsonObject();
                for (Field f : cache.keySet()) {
                    JsonElement element = root;
                    FlattenCacheItem cacheElement = cache.get(f);
                    for (String s : cacheElement.path) {
                        if (element.isJsonObject()) {
                            element = element.getAsJsonObject().get(s);
                        } else {
                            element = null;
                            break;
                        }
                    }
                    rootElement.getAsJsonObject().add(f.getName(), element);// FIXME: 19.05.2016 serializedName
                }
                T data = delegateAdapter.fromJsonTree(rootElement);
                return data;
            }
        }.nullSafe();

        return result;
    }


    private static Map<Field, FlattenCacheItem> buildCache(Class<?> root, Gson gson) {
        Map<Field, FlattenCacheItem> cache = new HashMap<>();
        final Field[] fields = root.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return cache;
        }
        Flatten flatten;
        Type type;
        String path;
        FlattenCacheItem cacheItem;
        List<FlattenCacheItem> list;

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Flatten.class)) {
                continue;
            }
            flatten = field.getAnnotation(Flatten.class);
            path = flatten.value();
            type = field.getGenericType();
            cacheItem = new FlattenCacheItem(path.split("::"), type, gson.getAdapter(type.getClass()));
            cache.put(field, cacheItem);
        }
        return cache;
    }

    private static class FlattenCacheItem {

        final String[] path;
        final TypeAdapter adapter;

        private FlattenCacheItem(String[] path, Type type, TypeAdapter adapter) {
            this.path = path;
            this.adapter = adapter;
        }
    }
}