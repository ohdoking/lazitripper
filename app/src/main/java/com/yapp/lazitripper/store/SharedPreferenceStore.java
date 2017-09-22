package com.yapp.lazitripper.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by ohdok on 2017-02-25.
 *
 * sharePreference를 다양한 오브젝트로 저장하고 가져올 수 있게 만들어 놓은 클래스
 */

public class SharedPreferenceStore<T> {

    Context context;
    String storeName;
    public SharedPreferenceStore(Context context, String storeName){
        this.context = context;
        this.storeName = storeName;
    }

    public T getPreferences(String key, Class<T> clazz){
        SharedPreferences pref = context.getSharedPreferences(storeName, context.MODE_PRIVATE);
        String json = pref.getString(key, "");
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public void savePreferences(String key, T value){
        SharedPreferences pref = context.getSharedPreferences(storeName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.commit();
    }
}
