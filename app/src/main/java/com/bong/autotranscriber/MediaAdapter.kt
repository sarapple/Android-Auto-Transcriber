package com.bong.autotranscriber

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class MediaAdapter () {
    companion object {
        fun getMusicFile(context: Context): File {
            val musicFile = FileHelper.getEmptyFileInFolder(context, "music", "recorded_song", ".wav");

            return musicFile
        }

        fun getSongPlayer(): MediaPlayer {
            return MediaPlayer()
        }

        fun startPlaying(player: MediaPlayer, context: Context, musicFile: File, onComplete: () -> Unit) {
            player.reset()
            player.setDataSource(context, musicFile.toUri())
            player.prepareAsync()
            player.setOnPreparedListener {
                player.start()
            }
            player.setOnCompletionListener {
                onComplete()
            }
        }

        fun stopPlaying(player: MediaPlayer) {
            player.pause()
//            player.release() <-- do this on save
        }
    }
}
