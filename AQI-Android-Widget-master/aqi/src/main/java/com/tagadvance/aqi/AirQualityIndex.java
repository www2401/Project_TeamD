package com.tagadvance.aqi;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AirQualityIndexConfigureActivity AirQualityIndexConfigureActivity}
 */
public class AirQualityIndex extends AppWidgetProvider {

    private static final String TAG = AirQualityIndex.class.getName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            WidgetPreferences preferences = WidgetPreferences.create(context, AirQualityIndexConfigureActivity.PREFS_NAME, appWidgetIds[i]);
            preferences.remove(AirQualityIndexConfigureActivity.KEY_USE_GPS);
            preferences.remove(AirQualityIndexConfigureActivity.KEY_POSTAL_CODE);
        }
    }

    @Override
    public void onEnabled(final Context context) {
        Log.d(AirQualityIndex.class.getName(), "enabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(final Context context, final AppWidgetManager widgetManager, final int widgetId) {
        Log.d(AirQualityIndex.class.getName(), "updating...");

        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.air_quality_index);

        WidgetPreferences preferences = WidgetPreferences.create(context, AirQualityIndexConfigureActivity.PREFS_NAME, widgetId);
        final boolean isChecked = preferences.getBoolean(AirQualityIndexConfigureActivity.KEY_USE_GPS, true);
        final String postalCode = preferences.getString(AirQualityIndexConfigureActivity.KEY_POSTAL_CODE, "");

        final String API_KEY = "";
        final AirNowAPI api = new AirNowAPI(API_KEY);
        new AsyncTask<Void, Void, Integer>() {

            protected Integer doInBackground(Void... voided) {
                int index = 0;
                if (isChecked) {
                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        index = api.getObservationByLocation(latitude, longitude);
                    }
                }
                if (index == 0) {
                    index = api.getObservationByZipCode(postalCode);
                }
                return index;
            }

            protected void onPostExecute(Integer index) {
                AQI aqi = AQI.getAQI(index);
                views.setInt(R.id.aqi_layout, "setBackgroundColor", aqi.getBackgroundColor());
                views.setTextColor(R.id.widget_text, aqi.getTextColor());
                String description = context.getString(aqi.getStringId());
                String text = String.format("%d - %s", index, description);
                views.setTextViewText(R.id.widget_text, text);

                // Instruct the widget manager to update the widget
                widgetManager.updateAppWidget(widgetId, views);
            }

        }.execute();
    }

}

