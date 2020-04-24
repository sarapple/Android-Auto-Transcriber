package com.bong.autotranscriber

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.midisheetmusic.*
import kotlinx.android.synthetic.main.fragment_first.*
import org.apache.commons.io.FileUtils
import java.io.File

class MainActivity : AppCompatActivity() {
    var audioFile: File? = null;
    var snackbar: Snackbar? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) {
            supportActionBar!!.setBackgroundDrawable(
                    ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark)))
        }
        setupChooseSongButton()
        snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val requestRecordAudio = 0;
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestRecordAudio) {
            if (resultCode == Activity.RESULT_OK) {
                convertWAVToMIDI(audioFile!!);
                snackbar!!
                        .setText("Converting media to sheet music...")
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "Audio was not recorded.", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val sourceTreeUri = data!!.data
            val byteData = contentResolver.openInputStream(sourceTreeUri!!)!!.readBytes()
            val midiFile = MidiFile(byteData, sourceTreeUri.pathSegments[sourceTreeUri.pathSegments.size-1])
            val intent = Intent(Intent.ACTION_VIEW, sourceTreeUri, this, SheetMusicActivity::class.java)
            intent.putExtra(SheetMusicActivity.MidiTitleID, midiFile.toString())

            startActivity(intent)

        }
    }

    fun recordAudio(v: View?) {
        audioFile = FileHelper.getEmptyFileInFolder(this.applicationContext,"audio", "recording", ".wav")
        val requestRecordAudio = 0;
        AndroidAudioRecorder.with(this) // Required
                .setFilePath(audioFile!!.absolutePath)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(requestRecordAudio) // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_16000)
                .setAutoStart(false)
                .setKeepDisplayOn(true) // Start recording
                .record()
    }

    fun convertWAVToMIDI (wavFile: File) {
        val wavBytes: ByteArray = FileUtils.readFileToByteArray(wavFile);
        val url = "http://192.168.1.131:5000/wav-to-midi"
//        val url = "http://10.0.2.2:5000/wav-to-midi"

        val queue = Volley.newRequestQueue(this.applicationContext)

        queue.add(makeHttpRequest(url, wavBytes, this.applicationContext!!))
    }

    fun makeHttpRequest(url: String?, byteArray: ByteArray, context: Context): ByteArrayRequest {
        return object : ByteArrayRequest(
                url,
                createMyReqSuccessListener(context),
                createMyReqErrorListener(context)
        ) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return byteArray
            }
        }
    }

    private fun moveToSongView (context: Context, file: File) {
        val midiFile = getMidiFileFromMidi(file, "Auto");
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromFile(file), context, SheetMusicActivity::class.java)
        intent.putExtra(SheetMusicActivity.MidiTitleID, midiFile.toString())
        startActivity(intent)
    }

    fun selectMidiFile() {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("audio/x-mid");
        // Provide read access to files and sub-directories in the user-selected
        // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 1)
    }

    private fun createMyReqSuccessListener(context: Context): Response.Listener<ByteArray> {
        val responseListener = object : Response.Listener<ByteArray> {
            override fun onResponse(response: ByteArray) {
                val file = FileHelper.getEmptyFileInFolder(context, "midi", "mid", ".mid")
                file.writeBytes(response)
//                Toast.makeText(context, "Acquired midi data from wav file", Toast.LENGTH_SHORT)
//                        .show()
                moveToSongView(context, file);

            }
        }
        return responseListener
    }


    private fun createMyReqErrorListener(context: Context): Response.ErrorListener {
        val responseListener = object : Response.ErrorListener {
            override fun onErrorResponse(response: VolleyError) {
                snackbar!!
                        .setText(R.string.msg_http_request_error_for_wav)
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show()
                snackbar!!.setAction(
                        "OK"
                ) { snackbar!!.dismiss() }
            }
        }
        return responseListener
    }

    private fun getMidiFileFromMidi(file: File, title: String): MidiFile? {
        val uriFromFile = Uri.fromFile(file)

        try {
            val fileUri = FileUri(uriFromFile, title)

            val data = fileUri.getData(this)

            return MidiFile(data, title)
        } catch (e: MidiFileException) {
            snackbar!!
                    .setText("Midi file could not be read. Please try a shorter recording.")
                    .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                    .show()
            snackbar!!.setAction(
                    "OK"
            ) { snackbar!!.dismiss() }
            return null
//            throw Exception("Midi file cannot be read");
        }

    }

    fun setupChooseSongButton() {
        button_choose_song.setOnClickListener {
            Log.v("app", "here");
            selectMidiFile()
        }
    }
}