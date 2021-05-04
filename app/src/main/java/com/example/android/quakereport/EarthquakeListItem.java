package com.example.android.quakereport;

public class EarthquakeListItem {
    private final double mMagnitude;
    private final String mSource;
    private final Long mTime;
    private final String mURL;

    public EarthquakeListItem(double mag, String src, Long dt, String URL) {
        mMagnitude = mag;
        mSource = src;
        mTime = dt;
        mURL = URL;
    }

    double getMagnitude() {
        return mMagnitude;
    }

    String getSource() {
        return mSource;
    }

    Long getTime() {
        return mTime;
    }

    String getURL() {
        return mURL;
    }
}
