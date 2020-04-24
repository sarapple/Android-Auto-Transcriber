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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.midisheetmusic.*
import kotlinx.android.synthetic.main.fragment_first.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.BufferedSink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    var audioFile: File? = null;
    var snackbar: Snackbar? = null;
    private val client = OkHttpClient()
    private val ACTIVITY_REQUEST_RECORD_AUDIO = 0;
    private val ACTIVITY_CHOOSE_SONG = 1;

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
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_REQUEST_RECORD_AUDIO) {
            if (resultCode == Activity.RESULT_OK) {
                // 1MB max for now
                if (audioFile != null && audioFile!!.length() > 1024 * 1000) {
                    snackbar!!
                            .setText(R.string.msg_open_file_error_size)
                            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                            .show()
                    snackbar!!.setAction(
                            "OK"
                    ) {
                        snackbar!!.dismiss()
                    }

                    return
                }

                convertWAVToMIDI(audioFile!!);
                snackbar!!
                        .setText("Converting media to sheet music...")
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
        else if (requestCode == ACTIVITY_CHOOSE_SONG && resultCode == Activity.RESULT_OK) {
            val sourceTreeUri = data!!.data
            val byteData = contentResolver.openInputStream(sourceTreeUri!!)!!.readBytes()
            val midiFile = MidiFile(byteData, sourceTreeUri.pathSegments[sourceTreeUri.pathSegments.size-1])
            val intent = Intent(Intent.ACTION_VIEW, sourceTreeUri, this, SheetMusicActivity::class.java)
            intent.putExtra(SheetMusicActivity.MidiTitleID, midiFile.toString())

            startActivity(intent)

        }
    }

    fun recordAudio(v: View?) {
        if (audioFile != null) {
            audioFile!!.delete();
        }
        audioFile = FileHelper.getEmptyFileInFolder(this.applicationContext,"audio", "recording", ".wav")
        AndroidAudioRecorder.with(this) // Required
                .setFilePath(audioFile!!.absolutePath)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(ACTIVITY_REQUEST_RECORD_AUDIO) // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_16000)
                .setAutoStart(false)
                .setKeepDisplayOn(true) // Start recording
                .record()

    }

    fun convertWAVToMIDI (wavFile: File) {
        getConvertedSongStream(wavFile, this)
    }

    fun copyStreamToFile(inputStream: InputStream, outputStream: FileOutputStream) {
        inputStream.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    fun getConvertedSongStream(file: File, context: Context) {
        val filebytes = file.readBytes()

        val requestBody = object : RequestBody() {
            override fun contentType() = MEDIA_TYPE_AUDIO

            override fun writeTo(sink: BufferedSink) {
                sink.write(filebytes)
            }
        }

        val request = Request.Builder()
                .url("http://192.168.1.131:5000/wav-to-midi")
//                .url("http://10.0.2.2:5000/upload")
                .post(requestBody)
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException): Unit {
                snackbar!!
                        .setText(R.string.msg_http_request_error_for_wav)
                        .setDuration(BaseTransientBottomBar.LENGTH_SHORT)
                        .show()
                snackbar!!.setAction(
                        "OK"
                ) { snackbar!!.dismiss() }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val file = FileHelper.getEmptyFileInFolder(context, "test", "test", ".mid")
                val fos = FileOutputStream(file)
                var fis: InputStream? = null;
                try {
                    if (!response.isSuccessful || response.body == null) throw IOException("Unexpected code " + response);
                    fis = response.body!!.byteStream()
                    copyStreamToFile(fis, fos)

                } catch (ex: Exception) {
                    Log.e("app", ex.message)
                    Log.e("app", ex.printStackTrace().toString())
                }

                moveToSongView(context, file)
            }
        });
    }

    companion object {
        val MEDIA_TYPE_AUDIO = "audio/x-wav; charset=utf-8".toMediaType()
    }

    private fun moveToSongView (context: Context, file: File) {
        val midiFile = getMidiFileFromMidi(file, "Recorded");
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