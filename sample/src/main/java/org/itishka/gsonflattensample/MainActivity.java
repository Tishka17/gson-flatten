package org.itishka.gsonflattensample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.itishka.gsonflatten.Flatten;
import org.itishka.gsonflatten.FlattenTypeAdapterFactory;

public class MainActivity extends AppCompatActivity {

    private final String json = "" +
            "{" +
            "   'title': {" +
            "     'text':'Hello everybody!'" +
            "   }" +
            "}";

    public static final class MyClass {
        @Flatten("title::text")
        String hello;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new FlattenTypeAdapterFactory())
                .create();

        ((TextView) findViewById(R.id.text)).setText(gson.fromJson(json, MyClass.class).hello);

    }
}
