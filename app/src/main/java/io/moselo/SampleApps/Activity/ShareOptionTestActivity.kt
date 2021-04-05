package io.moselo.SampleApps.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Listener.TapCoreSendMessageListener
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Model.TAPImageURL
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalkSample.R
import kotlinx.android.synthetic.main.activity_share_option_test.*
import kotlinx.android.synthetic.main.activity_share_option_test.popup_loading
import kotlinx.android.synthetic.main.tap_fragment_login_verification.*
import java.io.File

class ShareOptionTestActivity : AppCompatActivity() {

    private var roomModel: TAPRoomModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_option_test)

        roomModel = TAPRoomModel.Builder("36226-36275",
                "test", 1, TAPImageURL(), "#FFFFFF")


        when(intent.action) {
            Intent.ACTION_SEND -> {
                when {
                    intent.type == "text/plain" -> {
                        btn_send_message.text = "Send Text Message"
                        btn_send_message.setOnClickListener {
                            handleTextType(intent)
                        }
                    }
                    intent.type?.startsWith("image/") == true -> {
                        btn_send_message.text = "Send Image Message"
                        btn_send_message.setOnClickListener {
                            handleImageType(intent)
                        }
                    }
                    intent.type?.startsWith("video/") == true -> {
                        btn_send_message.text = "Send Video Message"
                        btn_send_message.setOnClickListener {
                            handleVideoType(intent)
                        }
                    }
                    else -> {
                        btn_send_message.text = "Send File Message"
                        btn_send_message.setOnClickListener {
                            handleFileType(intent)
                        }
                    }
                }
            }
        }


    }

    private fun handleFileType(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val file = File(TAPFileUtils.getInstance().getFilePath(this, it))
            TapCoreMessageManager.getInstance().sendFileMessage(file, roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                        deleteCache()
                    }
                }
            })
        }
    }

    private fun handleVideoType(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            TapCoreMessageManager.getInstance().sendVideoMessage(it, "share video", roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                    }
                }
            })
        }
    }

    private fun handleImageType(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            TapCoreMessageManager.getInstance().sendImageMessage(it, "share image", roomModel, object : TapCoreSendMessageListener() {
                override fun onStart(message: TAPMessageModel?) {
                    super.onStart(message)
                    runOnUiThread {
                        showPopupLoading(getString(R.string.tap_loading))
                    }
                }

                override fun onSuccess(message: TAPMessageModel?) {
                    super.onSuccess(message)
                    runOnUiThread {
                        hidePopupLoading()
                    }
                }
            })
        }
    }

    private fun handleTextType(intent: Intent) {
        if (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) == null) {
            //text type with string data
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                TapCoreMessageManager.getInstance().sendTextMessage(it, roomModel, object : TapCoreSendMessageListener() {
                    override fun onSuccess(message: TAPMessageModel?) {
                        super.onSuccess(message)
                        Toast.makeText(this@ShareOptionTestActivity, "SUCCES SEND MESSAGE", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            // text type with file data
            handleFileType(intent)
        }

    }

    private fun showPopupLoading(message: String) {
        popup_loading.findViewById<ImageView>(R.id.iv_loading_image).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white))
        if (null == popup_loading.findViewById<ImageView>(R.id.iv_loading_image).animation)
            TAPUtils.rotateAnimateInfinitely(this, popup_loading.findViewById<ImageView>(R.id.iv_loading_image))
        popup_loading.findViewById<TextView>(R.id.tv_loading_text)?.text = message
        popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.VISIBLE
    }

    private fun hidePopupLoading() {
        popup_loading.findViewById<FrameLayout>(R.id.popup_loading).visibility = View.GONE
    }

    fun deleteCache() {
        try {
            val dir: File = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?) {
        if (dir != null && dir.isDirectory) {
            val children = dir.listFiles()
            for (i in children.indices) {
                if (children[i].name.equals("share")) {
                    children[i].deleteRecursively()
                }
            }
        }
    }
}