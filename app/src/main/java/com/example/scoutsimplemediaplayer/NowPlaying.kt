package com.example.simplemediaplayer

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.simplemediaplayer.R.raw
import java.io.IOException
import java.util.Stack
import kotlin.random.Random.Default.nextInt

class NowPlaying : Fragment() {
    private lateinit var titleView: TextView
    private lateinit var artistView: TextView
    private lateinit var shuffleButton: ImageView
    private lateinit var repeatAllButton: ImageView
    private lateinit var repeatOneButton: ImageView
    private lateinit var previousButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var libraryButton: ImageView
    private var shuffleOn = false
    private var repeatAllOn = false
    private var repeatOneOn = false
    private var playing = false
    private var finishedSongs = false
    private lateinit var fileNames: ArrayList<Uri>
    private lateinit var mMediaPlayer: MediaPlayer
    private var lastPlayerIdx = 0
    private lateinit var songsPlayed: HashSet<Int>
    private lateinit var prevPlayed: Stack<Int>
    private val URI_PATH = "android.resource://com.example.simplemediaplayer/raw/"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view.findViewById(R.id.current_song_title)
        artistView = view.findViewById(R.id.current_song_artist)
        shuffleButton = view.findViewById(R.id.shuffle_button)
        repeatAllButton = view.findViewById(R.id.repeat_button)
        repeatOneButton = view.findViewById(R.id.repeat_one_button)
        previousButton = view.findViewById(R.id.previous_button)
        playButton = view.findViewById(R.id.play_button)
        nextButton = view.findViewById(R.id.next_button)
        libraryButton = view.findViewById(R.id.library)
        shuffleOn = false
        repeatAllOn = false
        repeatOneOn = false
        fileNames = ArrayList()
        mMediaPlayer = MediaPlayer()
        songsPlayed = HashSet()
        prevPlayed = Stack()
        mMediaPlayer.setOnCompletionListener {
            playNextSong()
            updateCurrentSongInfo()
        }
        lastPlayerIdx = 0
        val fields = raw::class.java.getFields()
        for (field in fields) {
            val temp = URI_PATH + field.getName()
            val uri = Uri.parse(temp)
            fileNames.add(uri)
        }
        val currUri = fileNames[lastPlayerIdx]
        try {
            mMediaPlayer.setDataSource(requireContext(), currUri)
            mMediaPlayer.prepare()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        libraryButton.setOnClickListener {
            getParentFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, Library(), "LIBRARY")
                .commit()
        }
        shuffleButton.setOnClickListener {
            if (shuffleOn) {
                shuffleOn = false
                shuffleButton.setImageDrawable(resources.getDrawable(R.drawable.shuffle_24px))
            } else {
                shuffleOn = true
                shuffleButton.setImageDrawable(resources.getDrawable(R.drawable.shuffle_on_24px))
                if (repeatOneOn) {
                    repeatOneOn = false
                    repeatOneButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_one_24px))
                }
            }
        }
        repeatAllButton.setOnClickListener {
            if (repeatAllOn) {
                repeatAllButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_24px))
                repeatAllOn = false
            } else {
                repeatAllButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_on_24px))
                repeatAllOn = true
                if (repeatOneOn) {
                    repeatOneOn = false
                    repeatOneButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_one_24px))
                }
            }
        }
        repeatOneButton.setOnClickListener {
            if (repeatOneOn) {
                repeatOneButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_one_24px))
                repeatOneOn = false
                if (shuffleOn) {
                    shuffleOn = false
                    shuffleButton.setImageDrawable(resources.getDrawable(R.drawable.shuffle_24px))
                }
                songsPlayed = HashSet()
            } else {
                repeatOneButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_one_on_24px))
                repeatOneOn = true
                if (repeatAllOn) {
                    repeatAllOn = false
                    repeatAllButton.setImageDrawable(resources.getDrawable(R.drawable.repeat_24px))
                }
            }
        }
        previousButton.setOnClickListener(View.OnClickListener {
            if (!playing) {
                return@OnClickListener
            }
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            if (!prevPlayed.isEmpty()) {
                val prev = prevPlayed.pop()
                lastPlayerIdx = prev
                val currUri = fileNames[prev]
                try {
                    mMediaPlayer.setDataSource(requireContext(), currUri)
                    mMediaPlayer.prepare()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                mMediaPlayer.start()
                updateCurrentSongInfo()
                playing = true
            } else {
                titleView.text = "Nothing else on the stack!"
                artistView.text = ""
            }
        })
        playButton.setOnClickListener {
            finishedSongs = false
            if (!playing) {
                try {
                    mMediaPlayer.start()
                    playing = true
                    updateCurrentSongInfo()
                    playButton.setImageDrawable(resources.getDrawable(R.drawable.pause_24px))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                mMediaPlayer.pause()
                playButton.setImageDrawable(resources.getDrawable(R.drawable.play_arrow_24px))
                playing = false
            }
        }
        nextButton.setOnClickListener {
            if (!finishedSongs) {
                mMediaPlayer.stop()
                mMediaPlayer.reset()
                playNextSong()
                updateCurrentSongInfo()
            }
        }
    }

    private fun updateCurrentSongInfo() {
        if (!finishedSongs) {
            val uri = fileNames[lastPlayerIdx]
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            val title =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            artistView.text = artist
            titleView.text = title
        } else {
            playing = false
            songsPlayed = HashSet()
            playButton.setImageDrawable(resources.getDrawable(R.drawable.play_arrow_24px))
        }
    }

    private fun playNextSong() {
        if (!playing) {
            return
        }
        prevPlayed.add(lastPlayerIdx)
        if (repeatOneOn) {
            val currUri = fileNames[lastPlayerIdx]
            try {
                mMediaPlayer.setDataSource(requireContext(), currUri)
                mMediaPlayer.prepare()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            mMediaPlayer.start()
            playing = true
        } else {
            if (shuffleOn) {
                if (repeatAllOn) {
                    val mod = fileNames.size
                    val rand = nextInt(1, mod - 1)
                    lastPlayerIdx = (lastPlayerIdx + rand) % mod
                    val currUri = fileNames[lastPlayerIdx]
                    try {
                        mMediaPlayer.setDataSource(requireContext(), currUri)
                        mMediaPlayer.prepare()
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                    mMediaPlayer.start()
                    playing = true
                } else {
                    songsPlayed.add(lastPlayerIdx)
                    if (songsPlayed.size == fileNames.size) {
                        mMediaPlayer.stop()
                        mMediaPlayer.reset()
                        finishedSongs = true
                        artistView.text = ""
                        titleView.text = "You've listened to all of the songs!"
                    } else {
                        while (songsPlayed.contains(lastPlayerIdx)) {
                            val mod = fileNames.size
                            val rand = nextInt(1, mod - 1)
                            lastPlayerIdx = (lastPlayerIdx + rand) % mod
                        }
                        val currUri = fileNames[lastPlayerIdx]
                        try {
                            mMediaPlayer.setDataSource(requireContext(), currUri)
                            mMediaPlayer.prepare()
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                        mMediaPlayer.start()
                        playing = true
                    }
                }
            } else {
                lastPlayerIdx++
                if (lastPlayerIdx == fileNames.size) lastPlayerIdx = 0
                val currUri = fileNames[lastPlayerIdx]
                //stop playing if we have looped around the entire library
                if (!repeatAllOn && lastPlayerIdx == 0) {
                    mMediaPlayer.stop()
                    mMediaPlayer.reset()
                    finishedSongs = true
                    artistView.text = ""
                    titleView.text = "You've listened to all of the songs!"
                } else {
                    try {
                        mMediaPlayer.stop()
                        mMediaPlayer.reset()
                        mMediaPlayer.setDataSource(requireContext(), currUri)
                        mMediaPlayer.prepare()
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                    mMediaPlayer.start()
                    playing = true
                }
            }
        }
    }
}
