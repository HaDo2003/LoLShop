/*
 * Copyright 2014 KC Ochibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
 *  and unicode 2017 that are used for separating the items in a list.
 */

package com.example.lolshop.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;


import com.example.lolshop.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class TinyDB {

    private static final String LIST_SEPARATOR = "‚‗‚"; // Defined constant for list separator
    private SharedPreferences preferences;
    private String lastImagePath = "";
    private final Gson gson;

    public TinyDB(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap getImage(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public String getSavedImagePath() {
        return lastImagePath;
    }

    public String putImage(String folder, String imageName, Bitmap bitmap) {
        if (folder == null || imageName == null || bitmap == null) return null;

        File dir = new File(Environment.getExternalStorageDirectory(), folder);
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("TinyDB", "Failed to create directory: " + dir.getAbsolutePath());
            return null;
        }

        File file = new File(dir, imageName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            lastImagePath = file.getAbsolutePath();
            return lastImagePath;
        } catch (IOException e) {
            Log.e("TinyDB", "Error saving image", e);
            return null;
        }
    }

    public boolean putImageWithFullPath(String fullPath, Bitmap bitmap) {
        if (fullPath == null || bitmap == null) return false;

        File file = new File(fullPath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            Log.e("TinyDB", "Error saving image to full path", e);
            return false;
        }
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public ArrayList<Integer> getListInt(String key) {
        String savedString = preferences.getString(key, "");
        ArrayList<String> stringList = new ArrayList<>(Arrays.asList(TextUtils.split(savedString, LIST_SEPARATOR)));
        ArrayList<Integer> intList = new ArrayList<>();
        for (String item : stringList) {
            try {
                intList.add(Integer.parseInt(item));
            } catch (NumberFormatException e) {
                Log.e("TinyDB", "Error parsing integer", e);
            }
        }
        return intList;
    }

    public void putListInt(String key, ArrayList<Integer> intList) {
        String joined = TextUtils.join(LIST_SEPARATOR, intList);
        preferences.edit().putString(key, joined).apply();
    }

    public <T> T getObject(String key, Class<T> type) {
        String json = preferences.getString(key, "");
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e("TinyDB", "Error getting object", e);
            return null;
        }
    }

    public <T> void putObject(String key, T object) {
        String json = gson.toJson(object);
        preferences.edit().putString(key, json).apply();
    }

    public <T> ArrayList<T> getListObject(String key, Class<T> type) {
        String json = preferences.getString(key, "");
        try {
            Type listType = TypeToken.getParameterized(ArrayList.class, type).getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e("TinyDB", "Error getting list object", e);
            return null;
        }
    }

    public <T> void putListObject(String key, ArrayList<T> list) {
        String json = gson.toJson(list);
        preferences.edit().putString(key, json).apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }
}