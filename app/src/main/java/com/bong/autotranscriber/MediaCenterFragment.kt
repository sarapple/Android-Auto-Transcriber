package com.bong.autotranscriber

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.*
import java.net.URLEncoder


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
        var buttonPrintIsClickable: Boolean = false
        var buttonRecordIsClickable: Boolean = false
        var currentMediaState: MediaState = MediaState.IDLE
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
//        val mediaStateFields = getStateByMediaState(MediaState.IDLE)
//        setState(mediaStateFields)
//        setupRecordButton()
//        setupPrintButton()
    }

//    fun convertWAVToMIDI (wavFile: File) {
////        val wavFile = FileHelper.assetFileCopy(context!!, "wavable_wavable.wav")
//        val wavBytes: ByteArray = FileUtils.readFileToByteArray(wavFile);
//        val url = "http://192.168.1.131:5000/music"
////        val url = "http://10.0.2.2:5000/music"
//        val queue = Volley.newRequestQueue(this.context)
//
//        queue.add(makeHttpRequest(url, wavBytes, this.context!!))
//    }
//
//    fun makeHttpRequest(url: String?, byteArray: ByteArray, context: Context): ByteArrayRequest {
//        return object : ByteArrayRequest(
//                url,
//                createMyReqSuccessListener(context),
//                createMyReqErrorListener()
//        ) {
//            @Throws(AuthFailureError::class)
//            override fun getBody(): ByteArray {
//                return byteArray
//            }
//        }
//    }
//
//    private fun createMyReqSuccessListener(context: Context): Response.Listener<ByteArray> {
//        val responseListener = object : Response.Listener<ByteArray> {
//            override fun onResponse(response: ByteArray) {
//                val file = FileHelper.getEmptyFileInFolder(context, "midi", "mid", ".mid")
//                file.writeBytes(response)
//
//                Toast.makeText(context, "Acquired midi data from wav file", Toast.LENGTH_SHORT)
//                        .show()
//                val midiFile = getMidiFileFromMidi(file, "BESTMIDI");
//                midifile = midiFile // keep for later
//
//                setupViews(midiFile);
//            }
//        }
//        return responseListener
//    }
//
//
//    private fun createMyReqErrorListener(): Response.ErrorListener {
//        val responseListener = object : Response.ErrorListener {
//            override fun onErrorResponse(response: VolleyError) {
//                Toast.makeText(context, "Error: Failed to convert wav to midi", Toast.LENGTH_SHORT)
//                        .show()
//            }
//        }
//        return responseListener
//    }
//
//    private fun getMidiFileFromMidi(file: File, title: String): MidiFile {
//        val uriFromFile = Uri.fromFile(file)
//
//        try {
//            val fileUri = FileUri(uriFromFile, title)
//
//            val data = fileUri.getData(activity)
//
//            return MidiFile(data, title)
//        } catch (e: MidiFileException) {
//            Log.e("app", "Midi file could not be read");
//            throw Exception("Midi file cannot be read");
//        }
//    }
//
//    private fun setupViews(midiFile: MidiFile) {
//        val activity = activity!!
//
//        // Keep screen on
//        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//        ClefSymbol.LoadImages(context)
//        TimeSigSymbol.LoadImages(context)
//
//        options = MidiOptions(midiFile)
//
//        layout = activity.findViewById<LinearLayout>(R.id.linearLayout2)
//        createSheetMusic(options!!, midiFile)
//    }
//
//    /** Create the SheetMusic view with the given options  */
//    private fun createSheetMusic(options: MidiOptions, midiFile: MidiFile) {
//        if (sheet != null) {
//            layout!!.removeView(sheet)
//        }
//        sheet = SheetMusic(context)
//        sheet!!.init(midiFile, options)
//        layout!!.addView(sheet)
//        layout!!.requestLayout()
//        sheet!!.draw()
//    }
//
//    /* Save the current sheet music as PNG images. */
//    private fun saveAsImages(sheetView: SheetMusic) {
//        if (!options!!.scrollVert) {
//            options!!.scrollVert = true
//            createSheetMusic(options!!, midifile!!)
//        }
//        try {
//            val numpages = sheetView.GetTotalPages()
//            for (page in 1..numpages) {
//                val image = Bitmap.createBitmap(
//                        SheetMusic.PageWidth + 40,
//                        // TODO: Ensure all pages print (wait for first print completion?)
////                    SheetMusic.PageHeight + 40,
//                        220 + 40,
//                        Bitmap.Config.ARGB_8888
//                )
//                val imageCanvas = Canvas(image)
//                sheetView.DrawPage(imageCanvas, page)
//                val tempFile = getEmptyFileInFolder(
//                        this.context!!,
//                        "sheet_music",
//                        "testing_scale" + "_" + page + "_",
//                        ".png"
//                )
//                tempFile.mkdirs()
//                val stream: OutputStream = FileOutputStream(tempFile)
//                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                stream.close()
//
//                // Inform the media scanner about the file
//                MediaScannerConnection.scanFile(
//                        this.context!!,
//                        arrayOf(tempFile.toString()),
//                        null,
//                        null
//                )
//                val imageOrig = BitmapFactory.decodeFile(tempFile.toString())
//                Brother().sendFileToQL820NWB(imageOrig, this.context!!)
//            }
//        } catch (e: Exception) {
//            Toast.makeText(context, "Error: Failed to print", Toast.LENGTH_SHORT)
//                    .show()
//        }
//    }
//
//    private fun setState (fields: MediaStateFields) {
//        button_record.setText(fields.buttonRecordText)
//        button_print.isClickable = fields.buttonPrintIsClickable
//        button_print.isEnabled = fields.buttonPrintIsClickable
//
//        mediaStateFields = fields
//    }
//
//    private fun getStateByMediaState (mediaState: MediaState): MediaStateFields {
//        when (mediaState) {
//            MediaState.IDLE -> MediaStateFields().apply {
//                buttonRecordText = R.string.record
//                buttonPrintIsClickable = false
//                buttonRecordIsClickable = true
//                currentMediaState = mediaState
//            }
//            MediaState.RECORDING -> return MediaStateFields().apply {
//                buttonRecordText = R.string.stop
//                buttonPrintIsClickable = false
//                buttonRecordIsClickable = true
//                currentMediaState = mediaState
//            }
//            MediaState.PRINTING -> return MediaStateFields().apply {
//                buttonRecordText = R.string.stop
//                buttonPrintIsClickable = false
//                buttonRecordIsClickable = true
//                currentMediaState = mediaState
//            }
//            MediaState.IDLE_WITH_RECORDING_DATA -> return MediaStateFields().apply {
//                buttonRecordText = R.string.record
//                buttonPrintIsClickable = true
//                buttonRecordIsClickable = true
//                currentMediaState = mediaState
//            }
//        }
//
//        return MediaStateFields()
//    }
//
//    private fun setupRecordButton() {
//        button_record.setOnClickListener {
//            if (mediaStateFields.currentMediaState == MediaState.RECORDING) {
//                recorder.stopRecorder()
//
//                Toast.makeText(context, "Stop recording file: " + currMusicFile.toString(), Toast.LENGTH_SHORT).show();
//                val mediaStateFields = getStateByMediaState(MediaState.IDLE_WITH_RECORDING_DATA)
//                setState(mediaStateFields)
//                convertWAVToMIDI(currMusicFile!!)
//
//            } else {
//                val musicFile = getMusicFile(context!!)
//                currMusicFile = musicFile
//                recorder.setupRecorder(musicFile)
//                recorder.startRecorder()
//                val mediaStateFields = getStateByMediaState(MediaState.RECORDING)
//                setState(mediaStateFields)
//            }
//        }
//    }
//
//    fun getMusicFile(context: Context): File {
//        val musicFile = FileHelper.getEmptyFileInFolder(context, "music", "recorded_song", ".wav");
//
//        return musicFile
//    }
//
//    fun setupPrintButton() {
//        button_print.setOnClickListener {
//            if (mediaStateFields.currentMediaState == MediaState.IDLE_WITH_RECORDING_DATA) {
//                setState(mediaStateFields)
//                saveAsImages(sheet!!);
//            } else {
//                Toast.makeText(context, "Error: Please record first", Toast.LENGTH_SHORT)
//                        .show()
//                // do not allow print action. Notify that recording needs to happen first.
//            }
//        }
//    }
}
