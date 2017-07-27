package com.tagadvance.aqi;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * The configuration screen for the {@link AirQualityIndex AirQualityIndex} AppWidget.
 * @author Tag <tagadvance@gmail.com>
 */
public class AirQualityIndexConfigureActivity extends Activity {

    private static final String TAG = AirQualityIndexConfigureActivity.class.getName();

    static final String PREFS_NAME = "AirQualityIndex";
    static final String KEY_USE_GPS = "gps";
    static final String KEY_POSTAL_CODE = "postal_code";


    int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    CheckBox useGps;
    EditText postalCode;

    public AirQualityIndexConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.air_quality_index_configure);
        useGps = (CheckBox) findViewById(R.id.use_gps);
        postalCode = (EditText) findViewById(R.id.postal_Code);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        WidgetPreferences preferences = WidgetPreferences.create(this, PREFS_NAME, widgetId);
        boolean isChecked = preferences.getBoolean(KEY_USE_GPS, true);
        useGps.setChecked(isChecked);
        String text = preferences.getString(KEY_POSTAL_CODE, "");
        postalCode.setText(text);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = AirQualityIndexConfigureActivity.this;
            final WidgetPreferences preferences = WidgetPreferences.create(context, PREFS_NAME, widgetId);

            // When the button is clicked, store the string locally
            boolean isChecked = useGps.isSelected();
            preferences.setBoolean(KEY_USE_GPS, isChecked);
            String text = postalCode.getText().toString();
            preferences.setString(KEY_POSTAL_CODE, text);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AirQualityIndex.updateAppWidget(context, appWidgetManager, widgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

}

