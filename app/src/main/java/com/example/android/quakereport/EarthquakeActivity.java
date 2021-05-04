/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthquakeListItem>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final String TAG = "Debug";
    private static String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=1&limit=50";
    private static EarthquakeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        earthquakeListView.setEmptyView(findViewById(R.id.empty_screen));
        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthquakeListAdapter(this, new ArrayList<EarthquakeListItem>());

        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                EarthquakeListItem er = mAdapter.getItem(i);
                // Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse(er.getURL()));
                Intent intent = new Intent(EarthquakeActivity.this, Web.class);
                assert er != null;
                intent.putExtra("url", er.getURL());
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, EarthquakeActivity.this).forceLoad();
    }

    @Override
    public Loader<List<EarthquakeListItem>> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader: ");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("eventtype", "earthquake");
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("limit", "50");
        USGS_REQUEST_URL = uriBuilder.toString();
        Log.d(TAG, "onCreateLoader: " + USGS_REQUEST_URL);
        return new EarthquakeAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<EarthquakeListItem>> loader, List<EarthquakeListItem> data) {
        Log.d(TAG, "onLoadFinished: ");
        mAdapter.clear();
        if (data != null && !data.isEmpty())
            mAdapter.addAll(data);
        TextView empty = (TextView) findViewById(R.id.empty_screen);
        findViewById(R.id.spinner).setVisibility(View.GONE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) empty.setText(R.string.no_earthquake_found);
        else empty.setText(R.string.check_internet_msg);
    }

    @Override
    public void onLoaderReset(Loader<List<EarthquakeListItem>> loader) {
        mAdapter.clear();
        mAdapter.addAll(new ArrayList<EarthquakeListItem>());
        Log.d(TAG, "onLoaderReset: ");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<List<EarthquakeListItem>> {
        public EarthquakeAsyncTaskLoader(Context context) {
            super(context);
            Log.d(TAG, "EarthquakeAsyncTaskLoader: constructor");
        }

        @Override
        public List<EarthquakeListItem> loadInBackground() {
            List<EarthquakeListItem> result = QueryUtils.fetchEarthquakeData(USGS_REQUEST_URL);
            Log.d(TAG, "loadInBackground: ");
            return result;
        }

    }

}
