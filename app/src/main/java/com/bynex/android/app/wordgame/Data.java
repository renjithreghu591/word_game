package com.bynex.android.app.wordgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data {

    private Context context;
    Toast toast;

    public Data(Context context) {
        this.context = context;
    }

    public boolean createCategory(String str) {
        if (str.equals(Values.WRONG_WORDS)) {
            showToast("Wrong words is default category name");
            return false;
        }
        SharedPreferences categories = context.getSharedPreferences(Values.CATEGORIES, Context.MODE_PRIVATE);

        if (categories.getString(str, null) == null) {
            categories.edit().putString(str, "true").commit();
            showToast("Category created");
            return true;
        }

        showToast("Category name already existed");
        return false;
    }

    public List<String> getList(String which) {
        SharedPreferences categories = context.getSharedPreferences(which, Context.MODE_PRIVATE);

        Map<String, ?> map = categories.getAll();
        Set<String> set = map.keySet();

        Iterator iterator = set.iterator();
        List<String> list = new ArrayList<>();

        while (iterator.hasNext()) {
            list.add(iterator.next().toString());
        }

        return list;
    }

    public String getValue(String sp, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public int getSize(String sp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sp, Context.MODE_PRIVATE);

        Map<String, ?> map = sharedPreferences.getAll();
        Set<String> set = map.keySet();

        return set.size();
    }

    public boolean add(String key, String word, String meaning) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(word, null) == null) {
            sharedPreferences.edit().putString(word, meaning).commit();
            return true;
        }

        showToast("Word already existed");
        return false;
    }

    public void renameCategory(String key, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.CATEGORIES, Context.MODE_PRIVATE);

        sharedPreferences.edit().remove(key).commit();
        sharedPreferences.edit().putString(name, "true").commit();

        SharedPreferences category = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Map<String, ?> map = category.getAll();
        Set<String> set = map.keySet();

        Iterator iterator = set.iterator();

        SharedPreferences newCategory = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        while (iterator.hasNext()) {
            String str = iterator.next().toString();
            newCategory.edit().putString(str, category.getString(str, null)).commit();
        }

        category.edit().clear().commit();
    }

    public void deleteCategory(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Values.CATEGORIES, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sharedPreferences1.edit().clear().commit();
        sharedPreferences.edit().remove(key).commit();
    }

    public void deleteWordAndMeaning(String category, String word) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(word).commit();
    }

    public void renameWord(String category, String word, String newWord) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(category, Context.MODE_PRIVATE);

        String meaning = sharedPreferences.getString(word, null);

        sharedPreferences.edit().remove(word).commit();
        sharedPreferences.edit().putString(newWord, meaning).commit();
    }

    public void renameMeaning(String category, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(category, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void showToast(String str) {
        try {
            toast.getView().isShown();
            toast.setText(str);
            toast.setDuration(Toast.LENGTH_SHORT);
        } catch (Exception e) {
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}
