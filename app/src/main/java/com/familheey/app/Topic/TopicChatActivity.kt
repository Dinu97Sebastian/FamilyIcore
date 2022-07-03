package com.familheey.app.Topic

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaRecorder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.devlomi.record_view.OnRecordListener
import com.familheey.app.Adapters.ChatAdapter
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Firebase.MessagePushDelegate
import com.familheey.app.Models.ChatModel
import com.familheey.app.Models.Request.RequstComment
import com.familheey.app.Models.Response.GetCommentsResponse
import com.familheey.app.Models.Response.SocketCommentResponse
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.Post.CreatePostActivity
import com.familheey.app.R
import com.familheey.app.Utilities.*
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL
import com.familheey.app.Utilities.Constants.Bundle.DATA
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.vanniktech.emoji.EmojiPopup
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_topic_chat.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URISyntaxException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TopicChatActivity : AppCompatActivity(), MessagePushDelegate, ChatAdapter.OnChatLongClickListener {
    private val PICKIMAGE = 23
    private val RESULTLOADVIDEO = 13
    private val RESULTDOC = 36
    private val REQUESTCODEPERMISSIONS = 12
    private var UPLOADFILEPOSITION = 0
    lateinit var topicId: String
    private var mSocket: Socket? = null
    private var Filename = ""
    private var chatAdapter: ChatAdapter? = null
    private val chatModelList: ArrayList<ChatModel> = ArrayList()
    private var mSelectedUri: ArrayList<Uri>? = null
    private var chatModel: ChatModel? = null
    private val MIME_TYPE_PDF = "application/pdf"
    private val MIME_TYPE_AUDIO = "audio/*"
    private var isCardOpen = false
    private var isChated = false
    private val RECORDAUDIOREQUESTCODE = 123
    private var mediaRecorder: MediaRecorder? = null
    private var AudioSavePathInDevice: String? = null
    private var compositeDisposable = CompositeDisposable()
    private val attachments: ArrayList<SocketCommentResponse.Attachment> = ArrayList()
    private var topicdetail: TopicDetail? = null
    private val mtype = "application/json; charset=utf-8"
    private val bucketName = "file_name/"
    private val RESULT_AUDIO = 111
    private val tempDirectoryName = "temp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_chat)
        topicId = intent.getStringExtra(DATA)!!


        val popup = EmojiPopup.Builder
            .fromRootView(findViewById(R.id.root_view)).build(edtxMessage)
        val qoute_emojiPopup = EmojiPopup.Builder
            .fromRootView(findViewById(R.id.root_view)).build(edtxqouteMessage )
        getSingleTopic()
        btnRecord.setRecordView(record_view)
        record_view.cancelBounds = 8f
        initListeners()
        initSocket()
        initRecyclerView()
        fetchComments()
        inits()
        // Modified By: Dinu(22/02/2021) For update visible_status="read" in firebase
        var notificationId:String = intent.getStringExtra("NOTIFICATION_ID").toString()
        var db = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().id + "_notification")
        db.child(notificationId!!).child("visible_status").setValue("read")
//Dinu(09-04-2021)
        btn_emoji.setOnClickListener(OnClickListener { popup.toggle() })
        btn_qouteEmoji.setOnClickListener(OnClickListener { qoute_emojiPopup.toggle() })
    }

    //CHECKSTYLE:OFF
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICKIMAGE && resultCode == Activity.RESULT_OK && null != data) {
            mSelectedUri = Matisse.obtainResult(data) as ArrayList<Uri>?
            try {
                progress.visibility = VISIBLE
                ImageCompressionAsyncTask(this).execute(readContentToFile(mSelectedUri!![0]))
            } catch (e: java.lang.Exception) {
                /*
                need to handle
                 */
            }
        }
        if (requestCode == RESULT_AUDIO && resultCode == Activity.RESULT_OK && null != data) {
            mSelectedUri = Matisse.obtainResult(data) as ArrayList<Uri>?
            try {
                showHideCard()
                imageAttach.visibility = VISIBLE
                edtxMessage.visibility = VISIBLE
                record_view.visibility = INVISIBLE
                val uri = data.getData()!!;
                mSelectedUri = ArrayList()
                mSelectedUri?.add(uri)
                uploadImagesToServer(MIME_TYPE_AUDIO)


            } catch (e: java.lang.Exception) {
                /*
                need to handle
                 */
            }
        }

        if (requestCode == RESULTLOADVIDEO && resultCode == Activity.RESULT_OK && null != data) {

            try {
                mSelectedUri = Matisse.obtainResult(data) as ArrayList<Uri>?
                val file = readContentToFile(mSelectedUri!!.get(0))
                val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss")
                val timeStamp = dateFormat.format(Date())
                val thumb = ThumbnailUtils.createVideoThumbnail(file!!.path, MediaStore.Video.Thumbnails.MINI_KIND)
                val thumbFile = File(cacheDir, timeStamp + "_thumb.jpg")
                val ss = thumbFile.createNewFile()
                val os: OutputStream = BufferedOutputStream(FileOutputStream(thumbFile))
                thumb?.compress(Bitmap.CompressFormat.JPEG, 70, os)
                os.close()
                val attachment = SocketCommentResponse.Attachment()
                attachment.filename = file.name.replace(" ".toRegex(), "")
                attachment.type = "video/mp4"
                attachment.video_thumb = "video_thumb/" + thumbFile.name
                attachments.add(attachment)
                uploadWithTransferUtility(thumbFile, "video_thumb/" + thumbFile.name, "SINGLE")
                uploadWithTransferUtility(file, bucketName + file.name.replace(" ".toRegex(), ""), "VIDEO")
            } catch (e: java.lang.Exception) {
                /*
                need to handle
                 */
            }

        }

        if (requestCode == RESULTDOC) {
            if (data == null) return
            mSelectedUri = ArrayList()

            val uri = data.data
            mSelectedUri = java.util.ArrayList()
            mSelectedUri!!.add(uri!!)

            //  mSelectedUri!!.addAll(data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)!!)
            if (isReadStoragePermissionGranted()) {
                uploadImagesToServer(MIME_TYPE_PDF)
                showHideCard()
            }

        }
    }

    //CHECKSTYLE:ON
    override fun onResume() {
        super.onResume()
        FamilheeyApplication.messagePushDelegate = this
        activatePost()
    }


    override fun onPause() {
        super.onPause()
        FamilheeyApplication.messagePushDelegate = null
        chatAdapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSocket != null && mSocket!!.connected()) {
            mSocket!!.disconnect()
        }
    }

    override fun getPush(type: String?) {
/*
Don't need this
*
* */
    }

    override fun onChatLongClicked(position: Int) {
        if (chatModelList.size > position) {
            val chat = chatModelList[position]
            if (chat.type.contains("audio")) {
                if (chat.isOwner) confirmationDalog(chat.comment_id)
                else showChatOption(chat)
            } else if (chat.filename.toLowerCase(Locale.ROOT).contains("mp4") || chat.filename.toLowerCase(Locale.ROOT).contains("mov") || chat.filename.toLowerCase(Locale.ROOT).contains("wmv") || chat.filename.toLowerCase(Locale.ROOT).contains("webm") || chat.filename.toLowerCase(Locale.ROOT).contains("mkv") || chat.filename.toLowerCase(Locale.ROOT).contains("flv") || chat.filename.toLowerCase(Locale.ROOT).contains("avi")) {
                if (chat.isOwner) confirmationDalog(chat.comment_id)
            } else showChatOption(chat)
        }
    }


    private val onNewMessage = Emitter.Listener { args: Array<Any> ->
        this@TopicChatActivity.runOnUiThread(Runnable {

            try {
                val json = JSONTokener(args[0].toString()).nextValue()
                if (json is JSONObject) {
                    acceptview.visibility = GONE
                    recyclerView.visibility = VISIBLE
                    normalchatview.visibility = VISIBLE
                } else if (json is JSONArray) {
                    val data = args[0] as JSONArray
                    val listType = object : TypeToken<List<SocketCommentResponse?>?>() {}.type
                    val socketCommentResponseList = Gson().fromJson<ArrayList<SocketCommentResponse>>(data.toString(), listType)
                    for (i in socketCommentResponseList.indices) {
                        var imagetype: String? = ""
                        var image: String? = ""
                        if (socketCommentResponseList[0].type == "delete_comment") {
                            for (j in chatModelList.indices) {
                                if (chatModelList[j].comment_id == socketCommentResponseList[0].delete_id[0]) {
                                    chatModelList.removeAt(j)
                                    break
                                }
                            }
                            chatAdapter!!.notifyDataSetChanged()
                        } else {
                            if (socketCommentResponseList[0].attachmentList.size > 0) {
                                image = socketCommentResponseList[0].file_name
                                imagetype = socketCommentResponseList[0].file_type
                            }
                            val chatModel: ChatModel = if (socketCommentResponseList[i].commented_by == SharedPref.getUserRegistration().id) {
                                ChatModel(true, socketCommentResponseList[i].comment, socketCommentResponseList[i].full_name, image, "", socketCommentResponseList[i].createdAt, imagetype, socketCommentResponseList[i].comment_id, socketCommentResponseList[i].commented_by.toInt(), socketCommentResponseList[i].quoted_date, socketCommentResponseList[i].quoted_id, socketCommentResponseList[i].quoted_item, socketCommentResponseList[i].quoted_user, socketCommentResponseList[i].attachmentList)
                            } else {
                                ChatModel(false, socketCommentResponseList[i].comment, socketCommentResponseList[i].full_name, image, ApiPaths.IMAGE_BASE_URL + "propic/" + socketCommentResponseList[i].propic, socketCommentResponseList[i].createdAt, imagetype, socketCommentResponseList[i].comment_id, socketCommentResponseList[i].commented_by.toInt(), socketCommentResponseList[i].quoted_date, socketCommentResponseList[i].quoted_id, socketCommentResponseList[i].quoted_item, socketCommentResponseList[i].quoted_user, socketCommentResponseList[i].attachmentList)
                            }
                            chatModelList.add(chatModel)
                            chatAdapter!!.notifyDataSetChanged()
                            if (recyclerView != null) {
                                recyclerView.scrollToPosition(chatModelList.size - 1)
                            }
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                /*
                need to handle
                 */
            }

        })
    }

    private fun initSocket() {
        try {

            mSocket = IO.socket(ApiPaths.SOCKET_URL)
            mSocket?.on(Socket.EVENT_CONNECT) {
            }
                ?.on(Socket.EVENT_ERROR) {
                }
            mSocket?.on("topic_channel_$topicId", onNewMessage)
            mSocket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun initRecyclerView() {
        chatAdapter = ChatAdapter(chatModelList, this@TopicChatActivity, this)
        recyclerView.layoutManager = LinearLayoutManager(this@TopicChatActivity)
        recyclerView.adapter = chatAdapter
    }

    private fun check(): Boolean {

        return TedPermission.isGranted(applicationContext, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun getPermissionToRecordAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORDAUDIOREQUESTCODE)
        }
    }

    private fun muteBackgroundAudio() {
        val am = this@TopicChatActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamMute(AudioManager.STREAM_MUSIC, true)
    }

    private fun unMuteBackgroundAudio() {
        val am = this@TopicChatActivity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamMute(AudioManager.STREAM_MUSIC, false)
    }
//Dinu(20/08/2021) to get temp folder
    fun getTempFolder(context: Context): String? {
        val tempDirectory =
                File(context.getExternalFilesDir(null).toString() + File.separator + tempDirectoryName)
        if (!tempDirectory.exists()) {
            println("creating directory: temp")
            tempDirectory.mkdir()
        }
        return tempDirectory.absolutePath
    }
    private fun mediaRecorderReady() {
        //Dinu(10/08/2021)
        AudioSavePathInDevice = getTempFolder(applicationContext)+ "/" +
                System.currentTimeMillis() + "AudioRecording.wav"
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder?.setOutputFile(AudioSavePathInDevice)
    }

    private fun showProgress() {
        if (progressBar != null) {
            progressBar.visibility = VISIBLE
        }
    }

    private fun hideProgress() {
        if (progressBar != null) {
            progressBar.visibility = GONE
        }
    }

    //CHECKSTYLE:OFF
    private fun initListeners() {


        addUser.setOnClickListener {
            val intent = Intent(applicationContext, TopicUsersActivity::class.java)
            intent.putExtra(DATA, topicId)
            if (topicdetail != null && topicdetail?.isAccept!!) {
                intent.putExtra(Constants.Bundle.VIEWING_ONLY, false)
            } else {
                intent.putExtra(Constants.Bundle.VIEWING_ONLY, true)
            }
            startActivity(intent)
        }
        goBack.setOnClickListener { onBackPressed() }
        imageAttach.setOnClickListener { showHideCard() }
        txtCancel.setOnClickListener { showHideCard() }
        btnSend.setOnClickListener {
            if (validatedChat()) {
                addComment(edtxMessage.text.toString(), false)
                edtxMessage.setText("")
            }
        }

        btnqouteSend.setOnClickListener {
            if (validatedQouteChat()) {
                addComment(edtxqouteMessage.text.toString(), true)
                chatview.visibility = VISIBLE
                qoutechat.visibility = GONE
                chatModel = null
                edtxMessage.setText("")
                edtxqouteMessage.setText("")
            }
        }
        edtxMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /* Don't need this
                **/
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                /*
                *Don't need this
                  */
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    btnSend.visibility = VISIBLE
                    btnRecord.visibility = GONE
                } else {
                    btnRecord.visibility = VISIBLE
                    btnSend.visibility = GONE
                }
            }
        })

        record_view.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (check()) {
                            recordStart()
                        } else {
                            showPermission(4)
                        }
                    } else {
                        recordStart()
                    }
                } catch (e: Exception) {
                    /*
                need to handle
                 */
                }
            }

            override fun onCancel() {
                //On Swipe To Cancel
                if (mediaRecorder != null) {
                    unMuteBackgroundAudio()
                    mediaRecorder!!.stop()
                    mediaRecorder!!.release()
                }
            }

            override fun onFinish(recordTime: Long) {
                if (mediaRecorder != null) {
                    try {
                        unMuteBackgroundAudio()
                        mediaRecorder!!.stop()
                        mediaRecorder!!.release()
                        val uri = Uri.fromFile(File(AudioSavePathInDevice!!))
                        mSelectedUri = ArrayList()
                        mSelectedUri?.add(uri)
                        uploadImagesToServer(MIME_TYPE_AUDIO)
                        imageAttach.visibility = VISIBLE
                        edtxMessage.visibility = VISIBLE
                        btn_emoji.visibility = VISIBLE
                        record_view.visibility = INVISIBLE
                    } catch (e: Exception) {
                        /*
                need to handle
                 */
                    }
                }
            }

            override fun onLessThanSecond() {
                imageAttach.visibility = VISIBLE
                edtxMessage.visibility = VISIBLE
                btn_emoji.visibility = VISIBLE
                record_view.visibility = INVISIBLE
            }
        })

        record_view.setOnBasketAnimationEndListener {
            imageAttach.visibility = VISIBLE
            edtxMessage.visibility = VISIBLE
            btn_emoji.visibility = VISIBLE
            record_view.visibility = INVISIBLE
        }

        txtVideo.setOnClickListener {

            showHideCard()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) goToVideoGalleryIntent()
                else showPermission(1)

            } else {
                goToVideoGalleryIntent()
            }
        }

        txtImage.setOnClickListener {
            showHideCard()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()) goToImageGalleryintent()
                else showPermission(3)
            } else {
                goToImageGalleryintent()
            }
        }

        txtDoc.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted()){
                    //FileUtils.pickDocument(this, RESULTDOC)
                    val intent = Intent()
                    intent.action = Intent.ACTION_OPEN_DOCUMENT
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "*/*"
                    val mimeTypes = arrayOf(FileUtils.MimeTypes.DOC, FileUtils.MimeTypes.DOCX, FileUtils.MimeTypes.PDF, FileUtils.MimeTypes.PPT,
                        FileUtils.MimeTypes.PPTX, FileUtils.MimeTypes.XLS, FileUtils.MimeTypes.XLSX, FileUtils.MimeTypes.XLA,
                        FileUtils.MimeTypes.zip1, FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.rar1, FileUtils.MimeTypes.rar2, FileUtils.MimeTypes.text1)
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    startActivityForResult(Intent.createChooser(intent, "Select Document "), RESULTDOC)
                }
                else showPermission(0)
            } else {
                //FileUtils.pickDocument(this, RESULTDOC)
                val intent = Intent()
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                val mimeTypes = arrayOf(FileUtils.MimeTypes.DOC, FileUtils.MimeTypes.DOCX, FileUtils.MimeTypes.PDF, FileUtils.MimeTypes.PPT,
                    FileUtils.MimeTypes.PPTX, FileUtils.MimeTypes.XLS, FileUtils.MimeTypes.XLSX, FileUtils.MimeTypes.XLA,
                    FileUtils.MimeTypes.zip1, FileUtils.MimeTypes.zip2,FileUtils.MimeTypes.rar1, FileUtils.MimeTypes.rar2, FileUtils.MimeTypes.text1)

                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                startActivityForResult(Intent.createChooser(intent, "Select Document "), RESULTDOC)
            }

        }
        txtAudio.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadWritePermissionGranted())
                {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "audio/mpeg"
                    startActivityForResult(Intent.createChooser(intent, "File Browser"), RESULT_AUDIO)
                }
                else {
                    showPermission(0)
                }
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "audio/mpeg"
                startActivityForResult(Intent.createChooser(intent, "File Browser"), RESULT_AUDIO)
            }
        }
        btn_accept.setOnClickListener {
            acceptUserTopic()
        }
        btn_reject.setOnClickListener {
            rejectUserTopic()
        }
        btn_qoute_view_close.setOnClickListener {
            chatview.visibility = VISIBLE
            qoutechat.visibility = GONE
            edtxMessage.text = edtxqouteMessage.text
            edtxqouteMessage.setText("")
        }
    }

    //CHECKSTYLE:ON
    private fun showHideCard() {
        if (isCardOpen) {
            bottom_sheet.visibility = GONE
        } else {
            bottom_sheet.visibility = VISIBLE
        }
        isCardOpen = !isCardOpen
    }

    private fun validatedChat(): Boolean {
        if (edtxMessage.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this@TopicChatActivity, "Enter some text.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validatedQouteChat(): Boolean {
        if (edtxqouteMessage.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Enter some text.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun recordStart() = try {
        muteBackgroundAudio()
        edtxMessage.visibility = INVISIBLE
        record_view.visibility = VISIBLE
        imageAttach.visibility = INVISIBLE
        btn_emoji.visibility = INVISIBLE
        mediaRecorderReady()
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
    } catch (e: IllegalStateException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    private fun confirmationDalog(id: String) {
        val builder = AlertDialog.Builder(this@TopicChatActivity)
        builder.setTitle("Do you really want to delete this conversation?")
        builder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            deleteComment(id)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.cancel() }
        builder.setCancelable(false)
        val alertDialog = builder.show()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
    }

    private fun showChatOption(chat: ChatModel) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_chat_option)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val delete = dialog.findViewById<TextView>(R.id.delete)
        val forward = dialog.findViewById<TextView>(R.id.forward)
        val copy = dialog.findViewById<TextView>(R.id.copy)
        val qoute = dialog.findViewById<TextView>(R.id.qoute)

        val download = dialog.findViewById<TextView>(R.id.download)
        if (chat.type.contains("audio")||chat.type.contains("mp3")) {
            copy.visibility = GONE
            qoute.visibility = GONE
            forward.visibility = GONE
            delete.visibility = GONE
            download.visibility = VISIBLE
        } else {
            if (chat.chatText.isEmpty()) {
                copy.visibility = GONE
                qoute.visibility = GONE
            } else {
                copy.visibility = VISIBLE
                qoute.visibility = VISIBLE
            }
            if (chat.isOwner) {
                delete.visibility = VISIBLE
            }
        }
        copy.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("chat_text", chat.chatText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        delete.setOnClickListener {
            dialog.dismiss()
            confirmationDalog(chat.comment_id)
        }
        qoute.setOnClickListener {
            dialog.dismiss()
            chatview.visibility = GONE
            qoutechat.visibility = VISIBLE
            txt_qoute.text = chat.chatText
            txt_qoute_user_name.text = chat.senderName + ", "
            txt_qoute_date.text = getFormattedDate(chat.createdAt)
            edtxqouteMessage.text = edtxMessage.text
            chatModel = chat
        }
        forward.setOnClickListener {
            startActivity(Intent(this@TopicChatActivity, CreatePostActivity::class.java).putExtra("FROM", "CHAT").putExtra(DATA, Gson().toJson(chat)))
            dialog.dismiss()
        }
        download.setOnClickListener {
            dialog.dismiss()
            Filename = chat.filename
            audioDownload(Filename)
        }

        dialog.show()
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        return if (TedPermission.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) true else {
            requestPermission()
            false
        }
    }

    private fun requestPermission() {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    FileUtils.pickDocument(this@TopicChatActivity, RESULTDOC)
                }

                //CHECKSTYLE:OFF
                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    /*
                    Not Needed
                     */
                }
                //CHECKSTYLE:ON
            })
            .setDeniedMessage("If you reject permission,you can not upload images\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    private fun goToVideoGalleryIntent() {
        Matisse.from(this)
            .choose(MimeType.ofVideo())
            .showSingleMediaType(true)
            .countable(true)
            .maxSelectable(1)
            .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(true)
            .forResult(RESULTLOADVIDEO)
    }

    private fun goToImageGalleryintent() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .showSingleMediaType(true)
            .countable(false)
            .maxSelectable(10)
            .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(true)
            .forResult(PICKIMAGE)
    }


    private fun askForPermission(i: Int) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) +
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Snackbar.make(findViewById(android.R.id.content),
                    "Please grant permissions to write data in sdcard",
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE"
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUESTCODEPERMISSIONS)
                }.show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUESTCODEPERMISSIONS)
            }
        } else {
            when (i) {
                0 -> {
                    FileUtils.pickDocument(this, RESULTDOC)
                }
                1 -> {
                    goToVideoGalleryIntent()
                }
                else -> {
                    goToImageGalleryintent()
                }
            }
        }
    }

    fun getfileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


    private fun uploadImagesToServer(type: String) {
        showProgress()
        val service = RetrofitBase.createRxResource(this, ApiServices::class.java)
        val parts: MutableList<MultipartBody.Part> = ArrayList()
        if (type != MIME_TYPE_PDF) {
            if (mSelectedUri != null) {
                for (i in mSelectedUri!!.indices) {

                    parts.add(FileUtil.prepareFilePart(this@TopicChatActivity, "file_name", mSelectedUri!![i], type))
                }
            }
        } else {
            if (mSelectedUri != null) {
                for (i in mSelectedUri!!.indices) {
                    try {
                        val extension = "application/" + getfileExtension(mSelectedUri!![i])
                        parts.add(FileUtil.prepareFilePartPdf(this@TopicChatActivity, "file_name", mSelectedUri!![i], extension))
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        hideProgress()
                        Toast.makeText(this, "Oops something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val description: RequestBody = "post".toRequestBody("MIME_TYPE_TEXT".toMediaTypeOrNull())
        val postId: RequestBody = topicId.toRequestBody("MIME_TYPE_TEXT".toMediaTypeOrNull())
        val commentedBy: RequestBody = SharedPref.getUserRegistration().id.toRequestBody("MIME_TYPE_TEXT".toMediaTypeOrNull())
        val comment: RequestBody = "".toRequestBody("MIME_TYPE_TEXT".toMediaTypeOrNull())
        if (parts.size == 0) {
            return
        }
        val call = service.uploadMultipleChatAttachment(ApiPaths.SOCKET_COMMENT_URL + "topic_comment", description, postId, comment, commentedBy, parts)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                hideProgress()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                hideProgress()
                Snackbar.make(findViewById(android.R.id.content),
                    "Upload failed!", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun getSingleTopic() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResource(this, RestApiService::class.java).getSingleTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    topicdetail = response.body()?.data?.get(0)
                    toolBarTitle.text = response.body()?.data?.get(0)?.title
                    if (SharedPref.getUserRegistration().id == response.body()?.data?.get(0)?.createdBy.toString()) {
                        if (checkAnyOneAcceptTopic(response.body()?.data?.get(0)?.toUsers!!)) {
                            acceptview.visibility = GONE
                            recyclerView.visibility = VISIBLE
                            normalchatview.visibility = VISIBLE
                        } else {
                            txt_mesg.text = "Once user accepts your request, you can start the conversation"
                            this.acceptview.visibility = VISIBLE
                            accept_reject.visibility = GONE
                            recyclerView.visibility = GONE
                            normalchatview.visibility = GONE
                        }
                    } else {
                        if (response.body()?.data?.get(0)?.isAccept!!) {
                            acceptview.visibility = GONE
                            recyclerView.visibility = VISIBLE
                            normalchatview.visibility = VISIBLE
                        } else {
                            txt_mesg.text = response.body()?.data?.get(0)?.createdUser + " want to start a conversation with you "
                            acceptview.visibility = VISIBLE
                            recyclerView.visibility = GONE
                            normalchatview.visibility = GONE
                        }
                    }
                }, {
                })
        )
    }

    private fun checkAnyOneAcceptTopic(users: ArrayList<User>): Boolean {
        for (user in users) {
            if (user.isAccept!!) {
                return true
            }
        }
        return false
    }

    //CHECKSTYLE:OFF
    private fun fetchComments() {
        showProgress()
        chatModelList.clear()
        val jsonObject = JsonObject()
        jsonObject.addProperty("topic_id", topicId)
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResource(this, RestApiService::class.java).getComments(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    val getCommentsResponse = Gson().fromJson(response.body().toString(), GetCommentsResponse::class.java)
                    val data = getCommentsResponse.data
                    for (i in data.indices) {
                        var imageUrl: String? = ""
                        var type: String? = ""
                        var profPic = ""
                        if (data[i].propic != null) {
                            profPic = ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + data[i].propic
                        }
                        if (data[i].attachment.size != 0) {
                            imageUrl = data[i].attachment[0].filename
                            type = data[i].attachment[0].type
                        }
                        var chatModel: ChatModel
                        chatModel = if (data[i].userId.toString() == SharedPref.getUserRegistration().id) {
                            ChatModel(true, data[i].comment, data[i].fullName, imageUrl, profPic, data[i].createdAt, type, data[i].commentId.toString() + "", data[i].userId, data[i].quoted_date, data[i].quoted_id, data[i].quoted_item, data[i].quoted_user, data[i].attachment)
                        } else {
                            ChatModel(false, data[i].comment, data[i].fullName, imageUrl, profPic, data[i].createdAt, type, data[i].commentId.toString() + "", data[i].userId, data[i].quoted_date, data[i].quoted_id, data[i].quoted_item, data[i].quoted_user, data[i].attachment)
                        }
                        chatModelList.add(chatModel)
                    }
                    if (data.size > 0) {
                        chatAdapter!!.notifyDataSetChanged()
                    }
                    hideProgress()
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(chatModelList.size - 1)
                    }
                }, {
                    hideProgress()
                })
        )
    }
    //CHECKSTYLE:ON

    fun addComment(comment: String?, isQoute: Boolean) {
        showProgress()
        val requstComment = RequstComment()
        requstComment.setTopic_id(topicId)
        requstComment.setComment(comment)
        requstComment.setCommented_by(SharedPref.getUserRegistration().id)
        if (attachments.size > 0) {
            requstComment.setAttachment(attachments)
            requstComment.setFile_name(attachments[0].filename)
            requstComment.setFile_type(attachments[0].type)
        }
        if (isQoute && chatModel != null) {
            requstComment.setQuoted_item(chatModel!!.chatText)
            requstComment.setQuoted_id(chatModel!!.comment_id)
            requstComment.setQuoted_user(chatModel!!.senderName)
            requstComment.setQuoted_date(chatModel!!.createdAt)
        }

        compositeDisposable.add(RetrofitUtil.createRxResourceSoket(this, RestApiService::class.java).topicComment(requstComment).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                isChated = true
                this.attachments.clear()
                hideProgress()
            }, {
                hideProgress()
            }))

    }


    private fun deleteComment(commentId: String) {
        showProgress()
        val jsonObject = JsonObject()
        val jsonElements = JsonArray()
        jsonElements.add(commentId)
        jsonObject.add("comment_id", jsonElements)
        jsonObject.addProperty("topic_id", topicId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResourceSoket(this, RestApiService::class.java).topicDelete(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    isChated = true
                    hideProgress()
                }, {
                    hideProgress()
                }))
    }


    private fun rejectUserTopic() {
        showProgress()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResourceSoket(this, RestApiService::class.java).rejectUserTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    hideProgress()
                    onBackPressed()
                }, {
                    hideProgress()
                }))
    }

    private fun acceptUserTopic() {
        showProgress()
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("topic_id", topicId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResourceSoket(this, RestApiService::class.java).acceptUserTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    hideProgress()
                    acceptview.visibility = GONE
                    recyclerView.visibility = VISIBLE
                    normalchatview.visibility = VISIBLE
                }, {
                    hideProgress()
                }))
    }


    private fun activatePost() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("topic_id", topicId.toInt())
        jsonObject.addProperty("device_type", "android")
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(mtype.toMediaTypeOrNull())
        compositeDisposable.add(
            RetrofitUtil.createRxResourceSoket(this, RestApiService::class.java).activateTopic(requestBody).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                }, {
                }))
    }

    private fun getFormattedDate(createdAt: String): String? {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val tzInAmerica = TimeZone.getTimeZone("IST")
        dateFormatter.timeZone = tzInAmerica
        try {
            val date = dateFormatter.parse(createdAt)
            val p = PrettyTime()
            return p.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "Just now"
    }

    override fun onBackPressed() {
        if (intent != null && intent.hasExtra(Constants.Bundle.TYPE)) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    @Throws(IOException::class)
    private fun readContentToFile(uri: Uri): File? {
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH)
        val timeStamp = dateFormat.format(Date())
        val file = File(cacheDir, timeStamp + getDisplayName(uri))
        contentResolver.openInputStream(uri).use { obj ->
            FileOutputStream(file, false).use { out ->
                val buffer = ByteArray(1024)
                var len: Int
                while (obj!!.read(buffer).also { len = it } != -1) {
                    out.write(buffer, 0, len)
                }
                return file
            }
        }
    }

    private fun getDisplayName(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null).use { cursor ->

            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        }
        return uri.path!!.replace(" ".toRegex(), "")

    }

    private fun inits() {
        applicationContext.startService(Intent(applicationContext, TransferService::class.java))
        AWSMobileClient.getInstance().initialize(this, object : com.amazonaws.mobile.client.Callback<UserStateDetails?> {
            override fun onResult(result: UserStateDetails?) {
                /*
                Don't need this
               */
            }

            override fun onError(e: java.lang.Exception) {
                /*
                Don't need this
               */
            }
        })
    }


    private fun uploadWithTransferUtility(file: File?, key: String?, type: String) {
        progress.visibility = VISIBLE
        val configuration = ClientConfiguration()
            .withMaxErrorRetry(2)
            .withConnectionTimeout(1200000)
            .withSocketTimeout(1200000)
        val transferUtility = TransferUtility.builder()
            .context(this)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(Regions.US_EAST_1), configuration))
            .build()
        val uploadObserver = transferUtility.upload(key, file)

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    if (type == "VIDEO") {
                        progress.visibility = GONE
                        addComment("", false)
                    } else if (type == "IMAGE") {
                        compressNextFile()
                    }
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                if (type == "VIDEO" || type == "IMAGE") {
                    progress.progress = percentDone
                }
            }

            override fun onError(id: Int, ex: java.lang.Exception) {
                /* Don't need the response */
            }
        })
    }


    inner class ImageCompressionAsyncTask(var mContext: Context) : AsyncTask<File, Int, File>() {

        override fun doInBackground(vararg params: File): File {
            return try {
                val compressedFile = Compressor(mContext)
                    .setQuality(60)
                    .compressToFile(params[0])
                for (i in 0..85) {
                    try {
                        Thread.sleep(10)
                    } catch (e: java.lang.Exception) {
                        /*
                        Need to handle
                         */
                    }
                    publishProgress(i)
                }
                compressedFile
            } catch (e: java.lang.Exception) {
                params[0]
            }

        }

        // Override the onProgressUpdate method to post the update on main thread
        override fun onProgressUpdate(vararg values: Int?) {

            progress.progress = values[0]!!

        }


        // Update the final status by overriding the OnPostExecute method.
        override fun onPostExecute(s: File) {
            uploadWithTransferUtility(s, bucketName + s.name.replace(" ".toRegex(), ""), "IMAGE")
            val attachment = SocketCommentResponse.Attachment()
            attachment.filename = s.name.replace(" ".toRegex(), "")
            attachment.type = "image/png"
            attachments.add(attachment)
        }

    }

    private fun compressNextFile() {
        if (mSelectedUri!!.size > UPLOADFILEPOSITION + 1) {
            UPLOADFILEPOSITION += 1
            try {
                ImageCompressionAsyncTask(this).execute(readContentToFile(mSelectedUri!![UPLOADFILEPOSITION]))
            } catch (ignored: java.lang.Exception) {
                /* Don't need the response */
            }
        } else {
            progress.visibility = GONE
            addComment("", false)
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun showPermission(type: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_permission)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        if (type == 4) {
            dialog.findViewById<View>(R.id.txt_plus).visibility = VISIBLE
            dialog.findViewById<View>(R.id.img_mic).visibility = VISIBLE
            val txtDecs = dialog.findViewById<TextView>(R.id.txt_decs)
            txtDecs.text = "To record a Voice Message, allow Familheey access to your microphone and your device's photos,media, and files"
        }
        dialog.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.btn_continue).setOnClickListener {
            dialog.dismiss()
            if (type == 0) {
                askForPermission(0)
            } else if (type == 1) {
                askForPermission(1)
            } else if (type == 3) {
                askForPermission(3)
            } else if (type == 4) {
                getPermissionToRecordAudio()
            } else askForPermission(type)
        }
        dialog.show()
    }

    private fun isReadWritePermissionGranted(): Boolean {
        return TedPermission.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun audioDownload(fname: String) {
        if (isReadStoragePermissionGranted1()) {
            val url = ApiPaths.IMAGE_BASE_URL + bucketName + fname
            if (url != "") {
                Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show()
                Utilities.downloadDocuments(this, url, fname)
            } else {
                Toast.makeText(this, "Unable to download ", Toast.LENGTH_SHORT).show()
            }
        } else showPermission()
    }

    private fun isReadStoragePermissionGranted1(): Boolean {
        return TedPermission.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestPermission1() {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    audioDownload(Filename)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    /* Don't need the response */
                }
            })
            .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    private fun showPermission() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_permission)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        val txtDecs = dialog.findViewById<TextView>(R.id.txt_decs)
        txtDecs.text = "To view images, allow Familheey access to your device's photos,media, and files."
        dialog.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.btn_continue).setOnClickListener {
            dialog.dismiss()
            requestPermission1()
        }
        dialog.show()
    }
}

