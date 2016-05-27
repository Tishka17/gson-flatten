[ ![gson-flatten](https://api.bintray.com/packages/tishka17/maven/gson-flatten/images/download.svg) ](https://bintray.com/tishka17/maven/gson-flatten/_latestVersion)


# gson-flatten
### Library to collapse inner objects when parsing json

1. To use library you should add it in gradle:
    ```gradle
    repositories {
        maven {
            url "https://dl.bintray.com/tishka17/maven"
        }
    }
    ```
and later:
    ```gradle
        compile 'org.itishka.gson-flatten:gson-flatten:0.5'
    ```

2. Then register it in your gson builder:
    ```java
    final Gson gson = new GsonBuilder()
              .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
              .create();
    ```
3. Define your class and use `@Flatten` annotation to get data from embedded objects. For example class
    ``` java
    class Weather {
      @Flatten("temperature::min")
      int min_temperture;
      @Flatten("temperature::max")
      int max_temperture;
    }
    ```
    will be filled with data from json
    ``` json
    {
      "temperature": {
         "min": -273,
         "max": 1000
      }
    }
    ```

4. Then just parse or serialize json as your usually do:
    ```java
      String json = gson.toJson(weather);
      Weather weather2 = gson.fromJson(json, Weather.class);
    ```

### Additional features
* You can skip field name if it equlas in inner object and outer one. E.g.:
```java
class Weather {
    @Flatten("temperature::")
    int min;
    @Flatten("temperature::")
    int max;
}
```

* If you are using Gson with some FieldNamingStrategies, do not forget to provide them to `FlattenTypeAdapterFactory`:
```java
final Gson gson = new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
          .registerTypeAdapterFactory(new FlattenTypeAdapterFactory(FieldNamingPolicy.UPPER_CAMEL_CASE))
          .create();
```