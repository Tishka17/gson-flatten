package org.itishka.gsonflatten;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tishka17 on 18.05.2016.
 */
public class FlattenTypeAdapterFactory implements TypeAdapterFactory {

    public FlattenTypeAdapterFactory() {
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> defaultAdapter = gson.getAdapter(JsonElement.class);
        final List<FlattenCacheItem> cache = buildCache(type.getRawType(), gson);

        TypeAdapter<T> result = new TypeAdapter<T>() {
            private void setElement(JsonObject root, String[] path, JsonElement data) {
                JsonObject element = root;
                for (int i = 0; i < path.length - 1; i++) {
                    JsonObject object = element.getAsJsonObject(path[i]);
                    if (object == null) {
                        object = new JsonObject();
                        element.add(path[i], object);
                    }
                    element = object;
                }
                element.add(path[path.length - 1], data);
            }

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                JsonElement res = delegateAdapter.toJsonTree(value);
                if (res.isJsonObject()) {
                    JsonObject object = res.getAsJsonObject();
                    for (FlattenCacheItem cacheItem : cache) {
                        JsonElement data = object.get(cacheItem.name);
                        object.remove(cacheItem.name);
                        setElement(object, cacheItem.path, data);
                    }
                    res = object;
                }
                gson.toJson(res, out);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (cache.isEmpty())
                    return delegateAdapter.read(in);
                JsonElement rootElement = defaultAdapter.read(in);
                if (!rootElement.isJsonObject())
                    return delegateAdapter.fromJsonTree(rootElement);
                JsonObject root = rootElement.getAsJsonObject();
                for (FlattenCacheItem cacheElement : cache) {
                    JsonElement element = root;
                    for (String s : cacheElement.path) {
                        if (element.isJsonObject()) {
                            element = element.getAsJsonObject().get(s);
                        } else {
                            element = null;
                            break;
                        }
                    }
                    rootElement.getAsJsonObject().add(cacheElement.name, element);// FIXME: 19.05.2016 serializedName
                }
                T data = delegateAdapter.fromJsonTree(rootElement);
                return data;
            }
        }.nullSafe();

        return result;
    }


    private ArrayList<FlattenCacheItem> buildCache(Class<?> root, Gson gson) {
        ArrayList<FlattenCacheItem> cache = new ArrayList<>();
        final Field[] fields = root.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return cache;
        }
        Flatten flatten;
        Type type;
        String path;
        FlattenCacheItem cacheItem;
        FieldNamingStrategy fieldNamingStrategy = gson.fieldNamingStrategy();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Flatten.class)) {
                continue;
            }
            flatten = field.getAnnotation(Flatten.class);
            path = flatten.value();
            type = field.getGenericType();
            String name = fieldNamingStrategy.translateName(field);
            cacheItem = new FlattenCacheItem(path.split("::", -1), gson.getAdapter(type.getClass()), name);
            //check path
            for (int i = 0; i < cacheItem.path.length - 1; i++) {
                if (cacheItem.path[i] == null || cacheItem.path[i].length() == 0) {
                    throw new RuntimeException("Intermediate path items cannot be empty, found " + path);
                }
            }
            int i = cacheItem.path.length - 1;
            if (cacheItem.path[i] == null || cacheItem.path[i].length() == 0) {
                cacheItem.path[i] =  cacheItem.name;
            }
            cache.add(cacheItem);
        }

        return cache;
    }

    private static class FlattenCacheItem {

        final String[] path;
        final TypeAdapter adapter;
        final String name;

        private FlattenCacheItem(String[] path, TypeAdapter adapter, String name) {
            this.path = path;
            this.adapter = adapter;
            this.name = name;
        }
    }
}