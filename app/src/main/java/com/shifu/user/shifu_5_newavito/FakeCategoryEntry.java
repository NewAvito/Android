package com.shifu.user.shifu_5_newavito;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A product entry in the list of products.
 */
public class FakeCategoryEntry {
    private static final String TAG = FakeCategoryEntry.class.getSimpleName();

    public final String title;
    public final Uri dynamicUrl;
    public final String url;
    public final String cost;
    public final String location;
    public String date;
    public Date date2;

    public FakeCategoryEntry(
            String title, String dynamicUrl, String url, String cost, String location) {
        this.title = title;
        this.dynamicUrl = Uri.parse(dynamicUrl);
        this.url = url;
        this.cost = cost;
        this.location = location;
    }

    private static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    /**
     * Loads a raw JSON at R.raw.products and converts it into a list of ItemEntry objects
     */
    public static List<FakeCategoryEntry> initProductEntryList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.products);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int pointer;
            while ((pointer = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, pointer);
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error writing/reading from the JSON file.", exception);
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                Log.e(TAG, "Error closing the input stream.", exception);
            }
        }
        String jsonProductsString = writer.toString();
        Gson gson = new Gson();
        Type productListType = new TypeToken<ArrayList<FakeCategoryEntry>>() {
        }.getType();
        return gson.fromJson(jsonProductsString, productListType);
    }

    public static List<FakeCategoryEntry> DatedList(Resources resources) {
        List<FakeCategoryEntry> list = initProductEntryList(resources);
        for (FakeCategoryEntry item: list) {
            int year = randBetween(2016, 2018);
            int month = randBetween(0,11);
            int day = randBetween(0,28);
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, 0, 0);
            item.date2 = c.getTime();
        }
        return list;
    }
}