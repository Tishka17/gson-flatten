package org.itishka.gsonflatten;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.Annotation;
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
                JsonElement element = root;
                for (int i = 0; i < path.length - 1; i++) {
                    // If the path element looks like a number..
                    Integer index = null;
                    try {
                        index = Integer.valueOf(path[i]);
                    } catch (NumberFormatException ignored) {
                    }

                    // Get the next object in the chain if it exists already
                    JsonElement object = null;
                    if (element instanceof JsonObject) {
                        object = ((JsonObject) element).get(path[i]);
                    } else if (element instanceof JsonArray && index != null) {
                        if (index >= 0 && index < ((JsonArray)element).size()) {
                            object = ((JsonArray) element).get(index);
                        }
                    } else {
                        // Failure. We can't walk any further - we don't know
                        // how to write this path. Maybe worth throwing exception?
                        continue;
                    }

                    // Object didn't exist in the output already. Create it.
                    if (object == null) {
                        // The next element in the chain is an array
                        if (path[i + 1].matches("^\\d+$")) {
                            object = new JsonArray();
                        } else {
                            object = new JsonObject();
                        }

                        if (element instanceof JsonObject) {
                            ((JsonObject) element).add(path[i], object);
                        } else if (element instanceof JsonArray && index != null) {
                            JsonArray array = (JsonArray) element;
                            // Might need to pad the array out if we're writing an
                            // index that doesn't exist yet.
                            while (array.size() <= index) {
                                array.add(JsonNull.INSTANCE);
                            }
                            array.set(index, object);
                        }
                    }
                    element = object;
                }

                if (element instanceof JsonObject) {
                    ((JsonObject) element).add(path[path.length - 1], data);
                } else if (element instanceof JsonArray) {
                    ((JsonArray) element).set(Integer.valueOf(path[path.length - 1]), data);
                }
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
                        } else if (element.isJsonArray()) {
                            try {
                                element = element.getAsJsonArray().get(Integer.valueOf(s));
                            } catch (NumberFormatException|IndexOutOfBoundsException e) {
                                element = null;
                            }
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

    // Find annotated fields of the class and any superclasses
    private static List<Field> getAnnotatedFields(Class klass, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<>();
        while (klass != null) {
            for (Field field : klass.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    fields.add(field);
                }
            }
            // Walk up class hierarchy
            klass = klass.getSuperclass();
        }
        return fields;
    }

    private ArrayList<FlattenCacheItem> buildCache(Class<?> root, Gson gson) {
        ArrayList<FlattenCacheItem> cache = new ArrayList<>();
        final List<Field> fields = getAnnotatedFields(root, Flatten.class);
        if (fields.size() == 0) {
            return cache;
        }
        Flatten flatten;
        Type type;
        String path;
        FlattenCacheItem cacheItem;
        FieldNamingStrategy fieldNamingStrategy = gson.fieldNamingStrategy();

        for (Field field : fields) {
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

    protected static class FlattenCacheItem {

        final String[] path;
        final TypeAdapter adapter;
        final String name;

        protected FlattenCacheItem(String[] path, TypeAdapter adapter, String name) {
            this.path = path;
            this.adapter = adapter;
            this.name = name;
        }
    }
}