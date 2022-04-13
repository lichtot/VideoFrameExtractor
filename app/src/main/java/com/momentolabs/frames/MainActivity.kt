package com.momentolabs.frames

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.crazylegend.videopicker.pickers.SingleVideoPicker
import com.crazylegend.videopicker.videos.VideoModel
import com.momentolabs.frameslib.data.model.FrameRetrieveRequest
import com.momentolabs.frameslib.data.model.Status
import com.momentolabs.frameslib.ui.view.VideoFramesLayout
import com.momentolabs.frameslib.Frames
import com.momentolabs.frameslib.data.metadataprovider.ProviderType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val askForStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showSingleImageBottomSheetPicker()
        }
    @SuppressLint("MissingPermission")
    private fun showSingleImageBottomSheetPicker(){
        SingleVideoPicker.showPicker(context = this, onPickedVideo = ::loadVideo)
    }
    private fun loadVideo(videoModel: VideoModel) {
//        Glide.with(this)
//            .load(videoModel.contentUri)
//            .into(binding.video)
        val path = getPath(this, videoModel.contentUri)
        Log.e("VIDEO_PICKED", videoModel.toString())
        Log.e("VIDEO_PICKED", path!!)
        loadFrame(path, videoModel.width!!, videoModel.height!!)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_select.setOnClickListener {
            askForStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }
    fun loadFrame(path:String, w: Int=400, h: Int=300){
        val framesLayout = findViewById<VideoFramesLayout>(R.id.layoutFramesLayout)

        val frameRetrieveRequest = FrameRetrieveRequest.MultipleFrameRequest(
            videoPath = path,
            frameWidth = 400,
            frameHeight = 300,
            durationPerFrame = 1000
        )

        Frames
            .load(frameRetrieveRequest)
            .setProviderType(ProviderType.FFMPEG)
            .into { framesResource ->
                when (framesResource.status) {
                    Status.EMPTY_FRAMES -> Log.v("TEST", "emptyframes: ${framesResource.frames.size} ${System.currentTimeMillis()}")
                    Status.LOADING -> Log.v("TEST", "loading: ${framesResource.frames.size}")
                    Status.COMPLETED -> Log.v("TEST", "Completed: ${framesResource.frames.size} ${System.currentTimeMillis()}")
                }
            }

        Frames.load(frameRetrieveRequest).into(videoFramesLayout = framesLayout, orientation = LinearLayout.HORIZONTAL)
    }
}
