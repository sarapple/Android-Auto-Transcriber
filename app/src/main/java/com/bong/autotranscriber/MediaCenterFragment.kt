package com.bong.autotranscriber

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.bong.autotranscriber.FileHelper.Companion.getEmptyFileInFolder
import com.midisheetmusic.*
import com.midisheetmusic.sheets.ClefSymbol
import kotlinx.android.synthetic.main.fragment_first.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MediaCenterFragment : Fragment() {
    private var sheet /* The sheet music */: SheetMusic? = null
    private var layout /* The layout */: LinearLayout? = null
    private var options /* The options for sheet music and sound */: MidiOptions? = null
    private var mediaStateFields: MediaStateFields = MediaStateFields()
    private var recorder: RecorderAdapter = RecorderAdapter()
    private var midifile: MidiFile? = null;
    private var currMusicFile: File? = null;

    enum class MediaState {
        IDLE, RECORDING, PRINTING, IDLE_WITH_RECORDING_DATA
    }

    class MediaStateFields () {
        var buttonRecordText: Int = R.string.record
        var currentMediaState: MediaState = MediaState.IDLE
        var isChooseSongButtonEnabled: Boolean = true
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.v("app", "hello")
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // sets up default state
        val mediaStateFields = getStateByMediaState(MediaState.IDLE)
        setState(mediaStateFields)
        setupRecordButton()
        setupChooseSongButton()
    }

    fun convertWAVToMIDI (wavFile: File) {
//        val wavFile = FileHelper.assetFileCopy(context!!, "wavable_wavable.wav")
        val wavBytes: ByteArray = FileUtils.readFileToByteArray(wavFile);
        val url = "http://192.168.1.131:5000/music"
//        val url = "http://10.0.2.2:5000/music"
        val queue = Volley.newRequestQueue(this.context)

        queue.add(makeHttpRequest(url, wavBytes, this.context!!))
    }

    fun makeHttpRequest(url: String?, byteArray: ByteArray, context: Context): ByteArrayRequest {
        return object : ByteArrayRequest(
                url,
                createMyReqSuccessListener(context),
                createMyReqErrorListener()
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

    private fun createMyReqSuccessListener(context: Context): Response.Listener<ByteArray> {
        val responseListener = object : Response.Listener<ByteArray> {
            override fun onResponse(response: ByteArray) {
                val file = FileHelper.getEmptyFileInFolder(context, "midi", "mid", ".mid")
                file.writeBytes(response)
                Toast.makeText(context, "Acquired midi data from wav file", Toast.LENGTH_SHORT)
                        .show()
                moveToSongView(context, file);

            }
        }
        return responseListener
    }


    private fun createMyReqErrorListener(): Response.ErrorListener {
        val responseListener = object : Response.ErrorListener {
            override fun onErrorResponse(response: VolleyError) {
                Toast.makeText(context, "Error: Failed to convert wav to midi", Toast.LENGTH_SHORT)
                        .show()
            }
        }
        return responseListener
    }

    private fun getMidiFileFromMidi(file: File, title: String): MidiFile {
        val uriFromFile = Uri.fromFile(file)

        try {
            val fileUri = FileUri(uriFromFile, title)

            val data = fileUri.getData(activity)

            return MidiFile(data, title)
        } catch (e: MidiFileException) {
            Log.e("app", "Midi file could not be read");
            throw Exception("Midi file cannot be read");
        }
    }

    private fun setupViews(midiFile: MidiFile) {
        val activity = activity!!

        // Keep screen on
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ClefSymbol.LoadImages(context)
        TimeSigSymbol.LoadImages(context)

        options = MidiOptions(midiFile)

        layout = activity.findViewById<LinearLayout>(R.id.linearLayout2)
        createSheetMusic(options!!, midiFile)
    }

    /** Create the SheetMusic view with the given options  */
    private fun createSheetMusic(options: MidiOptions, midiFile: MidiFile) {
        if (sheet != null) {
            layout!!.removeView(sheet)
        }
        sheet = SheetMusic(context)
        sheet!!.init(midiFile, options)
        layout!!.addView(sheet)
        layout!!.requestLayout()
        sheet!!.draw()
    }

    /* Save the current sheet music as PNG images. */
    private fun saveAsImages(sheetView: SheetMusic) {
        if (!options!!.scrollVert) {
            options!!.scrollVert = true
            createSheetMusic(options!!, midifile!!)
        }
        try {
            val numpages = sheetView.GetTotalPages()
            for (page in 1..numpages) {
                val image = Bitmap.createBitmap(
                        SheetMusic.PageWidth + 40,
                        // TODO: Ensure all pages print (wait for first print completion?)
//                    SheetMusic.PageHeight + 40,
                        220 + 40,
                        Bitmap.Config.ARGB_8888
                )
                val imageCanvas = Canvas(image)
                sheetView.DrawPage(imageCanvas, page)
                val tempFile = getEmptyFileInFolder(
                        this.context!!,
                        "sheet_music",
                        "testing_scale" + "_" + page + "_",
                        ".png"
                )
                tempFile.mkdirs()
                val stream: OutputStream = FileOutputStream(tempFile)
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                // Inform the media scanner about the file
                MediaScannerConnection.scanFile(
                        this.context!!,
                        arrayOf(tempFile.toString()),
                        null,
                        null
                )
                val imageOrig = BitmapFactory.decodeFile(tempFile.toString())
//                Brother().sendFileToQL820NWB(imageOrig, this.context!!)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: Failed to print", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun setState (fields: MediaStateFields) {
        button_record.setText(fields.buttonRecordText)
        button_choose_song.isEnabled = fields.isChooseSongButtonEnabled

        mediaStateFields = fields
    }

    private fun getStateByMediaState (mediaState: MediaState): MediaStateFields {
        when (mediaState) {
            MediaState.IDLE -> MediaStateFields().apply {
                buttonRecordText = R.string.record
                currentMediaState = mediaState
                isChooseSongButtonEnabled = true
            }
            MediaState.RECORDING -> return MediaStateFields().apply {
                buttonRecordText = R.string.stop
                currentMediaState = mediaState
                isChooseSongButtonEnabled = false
            }
            MediaState.IDLE_WITH_RECORDING_DATA -> return MediaStateFields().apply {
                buttonRecordText = R.string.record
                currentMediaState = mediaState
                isChooseSongButtonEnabled = true
            }
        }

        return MediaStateFields()
    }

    private fun setupRecordButton() {
        button_record.setOnClickListener {
            if (mediaStateFields.currentMediaState == MediaState.RECORDING) {
                recorder.stopRecorder()

                Toast.makeText(context, "Stop recording file: " + currMusicFile.toString(), Toast.LENGTH_SHORT).show();
                val mediaStateFields = getStateByMediaState(MediaState.IDLE_WITH_RECORDING_DATA)
                setState(mediaStateFields)

                convertWAVToMIDI(currMusicFile!!)
            } else {
                val musicFile = getMusicFile(context!!)
                currMusicFile = musicFile
                recorder.setupRecorder(musicFile)
                recorder.startRecorder()
                val mediaStateFields = getStateByMediaState(MediaState.RECORDING)
                setState(mediaStateFields)
            }
        }
    }

    fun getMusicFile(context: Context): File {
        val musicFile = FileHelper.getEmptyFileInFolder(context, "music", "recorded_song", ".wav");

        return musicFile
    }

    fun setupChooseSongButton() {
        button_choose_song.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, null, this.context, ChooseSongActivity::class.java)
            startActivity(intent)

            if (mediaStateFields.currentMediaState == MediaState.RECORDING) {
                // button should be disabled
            } else {
                Toast.makeText(context, "Error: Please stop your recording first.", Toast.LENGTH_SHORT)
                        .show()
                // Notify that recording needs to happen first.
            }
        }
    }
}
