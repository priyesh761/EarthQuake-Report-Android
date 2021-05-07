package com.example.android.quakereport

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.text.DecimalFormat
import java.util.*


class EarthquakeRListAdapter(private val context: Context, private val earthquakeList: ArrayList<EarthquakeListItem>) : RecyclerView.Adapter<EarthquakeRListAdapter.EarthquakeRViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeRViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        return EarthquakeRViewHolder(view)
    }

    override fun onBindViewHolder(holder: EarthquakeRViewHolder, position: Int) {

        val (magnitude, mSource, timeInMillisecond, url) = earthquakeList[position]
        val formattedMagnitude = formatMagnitude(magnitude)
        holder.magnitude.text = formattedMagnitude

        val magnitudeColor = getMagnitudeColor(magnitude)
        //DrawableCompat suppports backward compatibility
        DrawableCompat.setTint(holder.magnitude.background, magnitudeColor)

        val srcCity: String
        val srcOffset: String
        if (mSource.contains("of")) {
            val loc = mSource.indexOf("of")
            srcOffset = mSource.substring(0, loc + 2)
            srcCity = mSource.substring(loc + 3)
        } else {
            srcOffset = "Near the"
            srcCity = mSource
        }
        holder.source.text = srcCity
        holder.sourceOffset.text = srcOffset
        val currDateTime = Date(timeInMillisecond)
        val dateToDisplay = formatDate(currDateTime)
        holder.date.text = dateToDisplay

        val timeToDisplay = formatTime(currDateTime)
        holder.time.text = timeToDisplay

        holder.itemView.setOnClickListener {

            val intent = Intent(context, Web::class.java)
            if (BuildConfig.DEBUG && it == null) {
                error("Assertion failed")
            }
            intent.putExtra("url", url)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return earthquakeList.size
    }


    class EarthquakeRViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val magnitude: TextView = itemView.findViewById(R.id.magnitude)
        val source: TextView = itemView.findViewById(R.id.source_city)
        val sourceOffset: TextView = itemView.findViewById(R.id.source_distance)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)

    }

    fun clear() {
        earthquakeList.clear()
    }

    fun addAll(newEarthquakeList: ArrayList<EarthquakeListItem>) {
        earthquakeList.addAll(newEarthquakeList)
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private fun formatDate(dateObject: Date): String? {

        val dateFormat = getDateInstance()
        return dateFormat.format(dateObject)
    }

    /**
     * Return the formatted Time string (i.e. "4:30 PM") from a Date object.
     */
    private fun formatTime(dateObject: Date): String? {
        val timeFormat = getTimeInstance()
        return timeFormat.format(dateObject)
    }

    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    private fun formatMagnitude(magnitude: Double): String? {
        val magnitudeFormat = DecimalFormat("0.0")
        return magnitudeFormat.format(magnitude)
    }

    /**
     * Return the color code for magnitude
     */
    private fun getMagnitudeColor(magnitude: Double): Int {
        val magnitudeColorResourceId: Int
        val magnitudeFloor = Math.floor(magnitude).toInt()
        magnitudeColorResourceId = when (magnitudeFloor) {
            0, 1 -> R.color.magnitude1
            2 -> R.color.magnitude2
            3 -> R.color.magnitude3
            4 -> R.color.magnitude4
            5 -> R.color.magnitude5
            6 -> R.color.magnitude6
            7 -> R.color.magnitude7
            8 -> R.color.magnitude8
            9 -> R.color.magnitude9
            else -> R.color.magnitude10plus
        }
        return ContextCompat.getColor(context, magnitudeColorResourceId)
    }


}