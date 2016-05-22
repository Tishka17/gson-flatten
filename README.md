# gson-flatten
To use library you should add it in gradle:
```gradle
    compile 'org.itishka.gson-flatten:gson-flatten:0.2'
```

Then register it in your gson builder:
```java
final Gson gson = new GsonBuilder()
          .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
          .create();
```
Then you can use `@Flatten` annotation to get data from embedded objects. For example class
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
