package com.bong.autotranscriber

import android.content.Context
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import omrecorder.*
import omrecorder.PullTransport.OnAudioChunkPulledListener
import java.io.File


class RecorderAdapter() {
    private var recordTask: Recorder? = null;

    private fun mic(): PullableSource? {
        return PullableSource.Default(
            AudioRecordConfig.Default(
                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100
            )
        )
    }
    fun setupRecorder(musicFile: File) {
        recordTask = OmRecorder.wav(
            PullTransport.Default(mic(),
                OnAudioChunkPulledListener { audioChunk -> Log.i("app", audioChunk.maxAmplitude().toString()) }),
            musicFile
        )

        recordTask!!.startRecording();
        recordTask!!.pauseRecording();
    }


    fun startRecorder() {
        recordTask!!.resumeRecording();
    }

    fun stopRecorder() {
        recordTask!!.pauseRecording();
        recordTask!!.stopRecording();
    }
}