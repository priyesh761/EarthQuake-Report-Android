package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeListAdapter extends ArrayAdapter<EarthquakeListItem> {

    public EarthquakeListAdapter(@NonNull Context context, @NonNull List<EarthquakeListItem> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItem = convertView;
        if (currentItem == null) {
            currentItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView magnitude = currentItem.findViewById(R.id.magnitude);
        TextView source = currentItem.findViewById(R.id.source_city);
        TextView sourceOffset = currentItem.findViewById(R.id.source_distance);
        TextView date = currentItem.findViewById(R.id.date);
        TextView time = currentItem.findViewById(R.id.time);

        final EarthquakeListItem itm = getItem(position);
        String formattedMagnitude = formatMagnitude(itm.getMagnitude());
        magnitude.setText(formattedMagnitude);
        String mSource = itm.getSource();

        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(itm.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        String srcCity, srcOffset;
        if (mSource.contains("of")) {
            int loc = mSource.indexOf("of");
            srcOffset = mSource.substring(0, loc + 2);
            srcCity = mSource.substring(loc + 3);
        } else {
            srcOffset = "Near the";
            srcCity = mSource;
        }
        source.setText(srcCity);
        sourceOffset.setText(srcOffset);
        Long timeInMillisecond = itm.getTime();
        Date currDateTime = new Date(timeInMillisecond);
        String dateToDisplay = formatDate(currDateTime);
        date.setText(dateToDisplay);

        String timeToDisplay = formatTime(currDateTime);
        time.setText(timeToDisplay);

        return currentItem;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted Time string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    /**
     * Return the color code for magnitude
     */
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
