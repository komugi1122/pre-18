package com.example.memorycollection.savon;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_PAGE_DATA_LIST = "PageDataList";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // データを保存
    public void savePageDataList(List<PageData> pageDataList) {
        String json = gson.toJson(pageDataList);
        sharedPreferences.edit().putString(KEY_PAGE_DATA_LIST, json).apply();
    }

    // データを読み込む
    public List<PageData> loadPageDataList() {
        String json = sharedPreferences.getString(KEY_PAGE_DATA_LIST, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<PageData>>() {}.getType();
        return gson.fromJson(json, type);
    }
}