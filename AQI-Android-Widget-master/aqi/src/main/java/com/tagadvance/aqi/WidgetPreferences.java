package com.tagadvance.aqi;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Preconditions;

/**
 * @author Tag <tagadvance@gmail.com>
 */
public class WidgetPreferences {

    public static WidgetPreferences create(Context context, String name, int widgetId) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return new WidgetPreferences(preferences, widgetId);
    }

    private final SharedPreferences preferences;
    private final int widgetId;

    public WidgetPreferences(SharedPreferences preferences, int widgetId) {
        this.preferences = Preconditions.checkNotNull(preferences, "preferences must not be null");
        this.widgetId = widgetId;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key + widgetId, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key + widgetId, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key + widgetId, defaultValue);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key + widgetId, value);
        editor.commit();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key + widgetId);
        editor.commit();
    }

}
