package com.example.android.quakereport;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS API.
 * This class is only meant to hold static variables and methods, which can be accessed
 * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
 */
public final class QueryUtils {

    private QueryUtils() {

    }

    /**
     * Return a list of {@link EarthquakeListItem} objects that has been built up from
     * parsing a JSON  response.
     */
    public static List<EarthquakeListItem> extractEarthquakes(String data) {

        List<EarthquakeListItem> earthquakes = new ArrayList<>();

        try {

            // build up a list of Earthquake objects with the corresponding data.
            JSONObject response = new JSONObject(data);
            JSONArray earthquakeList = response.getJSONArray("features");

            for (int i = 0; i < earthquakeList.length(); i++) {
                JSONObject current = earthquakeList.getJSONObject(i);
                JSONObject currentProperties = current.getJSONObject("properties");
                double magnitude = currentProperties.getDouble("mag");
                String place = currentProperties.getString("place");
                Long timeInMillisecond = currentProperties.getLong("time");
                String url = currentProperties.getString("url");
                earthquakes.add(new EarthquakeListItem(magnitude, place, timeInMillisecond, url));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }

    /**
     * Return a list of {@link EarthquakeListItem} objects that has been created
     * from data fetched from API and parsing a JSON  response.
     */

    public static List<EarthquakeListItem> fetchEarthquakeData(String queryLink) {
        Log.d("Debug", "fetchEarthquakeData: ");

        URL url = createUrl(queryLink);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("exception", "fetchEarthquakeData: ", e);
        }
        return extractEarthquakes(jsonResponse);
    }

    /**
     * Return URL object for give URL in form of string.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Debug", "createUrl: ", e);
        }
        return url;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                response.append(line);
                line = reader.readLine();
            }
        }
        return response.toString();
    }

    /**
     *
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else
                Log.d("debug", "Error Response Code: " + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.e("debug", "makeHttpRequest: ", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }
}