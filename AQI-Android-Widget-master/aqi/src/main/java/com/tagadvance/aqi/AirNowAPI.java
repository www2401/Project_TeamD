package com.tagadvance.aqi;

import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Tag <tagadvance@gmail.com>
 */
public class AirNowAPI {

    public static final String API_URL = "http://www.airnowapi.org";
    public static final String FORMAT_CSV = "text/csv";
    public static final String FORMAT_JSON = "application/json";
    public static final String FORMAT_XML = "application/xml";
    public static final String AQI_TAG_NAME = "AQI";

    private static final String TAG = AirNowAPI.class.getName();

    private final String apiKey;

    public AirNowAPI(String apiKey) {
        this.apiKey = Preconditions.checkNotNull(apiKey, "apiKey must not be null");
    }

    /**
     *
     * @param zipCode
     * @return
     * @see http://airnowapi.org/CurrentObservationsByZip/docs
     */
    public int getObservationByZipCode(String zipCode) {
        String uri = buildObservationByZipCodeUrl(zipCode);
        return fetchAirQualityIndex(uri);
    }

    private String buildObservationByZipCodeUrl(String zipCode) {
        String path = "/aq/observation/zipCode/current/";
        StringBuilder sb = new StringBuilder(API_URL).append(path).append('?');
        Map<String, String> parameters = new HashMap<>();
        parameters.put("zipCode", zipCode);
        parameters.put("format", FORMAT_XML);
        parameters.put("api_key", this.apiKey);
        Joiner.on('&').withKeyValueSeparator("=").appendTo(sb, parameters);
        return sb.toString();
    }

    /**
     *
     * @param zipCode
     * @return
     * @see http://airnowapi.org/CurrentObservationsByLatLon/docs
     */
    public int getObservationByLocation(Object latitude, Object longitude) {
        String uri = buildObservationByLocationCodeUrl(latitude.toString(), longitude.toString());
        return fetchAirQualityIndex(uri);
    }

    private String buildObservationByLocationCodeUrl(String latitude, String longitude) {
        String path = "/aq/observation/latLong/current/";
        StringBuilder sb = new StringBuilder(API_URL).append(path).append('?');
        Map<String, String> parameters = new HashMap<>();
        parameters.put("latitude", latitude);
        parameters.put("longitude", longitude);
        parameters.put("format", FORMAT_XML);
        parameters.put("api_key", this.apiKey);
        Joiner.on('&').withKeyValueSeparator("=").appendTo(sb, parameters);
        return sb.toString();
    }

    private int fetchAirQualityIndex(String uri) {
        Log.d(TAG, "connecting to " + uri);
        URL url;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            return 0;
        }

        // The java.lang.AutoCloseable interface wasn't added to Android until API 19. =(
        // http://stackoverflow.com/a/27069995/625688
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);
            NodeList nodeList = doc.getElementsByTagName(AQI_TAG_NAME);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element node = (Element) nodeList.item(i);
                String aqi = node.getTextContent();
                try {
                    return Integer.parseInt(aqi);
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (SAXException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return 0;
    }
}
