package com.example.simplemediaplayer

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.simplemediaplayer.R.raw
import java.time.Duration

class Library : Fragment() {
    private val uriPath = "android.resource://com.example.simplemediaplayer/raw/"
    var linearLayout: LinearLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        linearLayout = view.findViewById(R.id.track_list_layout)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateTrackInfo()
        val nowPlaying = view.findViewById<ImageView>(R.id.now_playing)
        nowPlaying.setOnClickListener {
            val fragment = getParentFragmentManager().findFragmentByTag("LIBRARY")
            if (fragment != null) getParentFragmentManager().beginTransaction().remove(fragment)
                .commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun populateTrackInfo() {
        val fields = raw::class.java.getFields()
        for (x in fields.indices) {
            val temp = uriPath + fields[x].getName()
            val uri = Uri.parse(temp)
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val title =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val duration =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            addTrackInfoToLibrary(title, artist, duration)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun addTrackInfoToLibrary(title: String?, artist: String?, duration: String?) {
        val relativeLayout = RelativeLayout(context)
        val params = RelativeLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 10, 0, 10)
        relativeLayout.setLayoutParams(params)
        val titleView = TextView(context)
        val titleParams = RelativeLayout.LayoutParams(
            400,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        titleParams.setMargins(15, 0, 0, 0)
        titleView.setMaxLines(1)
        titleView.ellipsize = TextUtils.TruncateAt.END
        titleView.setLayoutParams(titleParams)
        titleView.text = title
        relativeLayout.addView(titleView)
        val artistView = TextView(context)
        val artistParams = RelativeLayout.LayoutParams(
            300,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        artistParams.setMargins(450, 0, 0, 0)
        artistView.setLayoutParams(artistParams)
        artistView.setMaxLines(1)
        artistView.ellipsize = TextUtils.TruncateAt.END
        artistView.text = artist
        relativeLayout.addView(artistView)
        val durationView = TextView(context)
        val durationParams = RelativeLayout.LayoutParams(
            300,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        durationParams.setMargins(800, 0, 0, 0)
        durationView.setLayoutParams(durationParams)
        val time = Duration.ofMillis(duration!!.toLong())
        val min = time.toMinutesPart()
        val seconds = time.toSecondsPart()
        var secondStr = "" + seconds
        if (secondStr.length == 1) secondStr = "0$secondStr"
        if (secondStr.isEmpty()) secondStr = "00"
        durationView.text = "$min:$secondStr"
        relativeLayout.addView(durationView)
        linearLayout!!.addView(relativeLayout)
    }
}