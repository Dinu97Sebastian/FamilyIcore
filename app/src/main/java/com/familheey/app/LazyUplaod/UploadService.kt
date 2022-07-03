package com.familheey.app.LazyUplaod

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.familheey.app.Models.Request.HistoryImages
import com.familheey.app.R
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

const val START_UPLOAD = "START_UPLOAD"
const val RESUME_UPLOAD = "RESUME_UPLOAD"
const val CANCEL_UPLOAD = "CANCEL_UPLOAD"
const val FILE_LIST = "FILE_LIST"

class UploadService : Service() {

    val TAG: String = UploadService::class.java.simpleName
    private val CHANNEL_ID = "FILE_UPLOAD"
    private val NOTIFICATION_ID = 1

    val fileQueue: BlockingQueue<FileUploadModel> = ArrayBlockingQueue(20)
    private val mFileUploadHandler = Handler()
    private var mFileUploadTask: FileUploadTask? = null
    private var isFileUploadRunning = false

    private var modules  = ArrayList<FileUploadModel>()
    private var files :FileUploadModel? = null
    private val MIME_TYPE_IMAGE = "image/png"
    private var fileCount = 0
    private lateinit var notificationBuilder:NotificationCompat.Builder
    fun startUpload(context: Context, fileList: ArrayList<String?>?) {
        val intent = Intent(context, UploadService::class.java)
        intent.action = START_UPLOAD
        intent.putStringArrayListExtra(FILE_LIST, fileList)
        context.startService(intent)
    }

    fun cancelUpload(context: Context) {
        val intent = Intent(context, UploadService::class.java)
        intent.action = CANCEL_UPLOAD
        context.startService(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {Log.e("UploadService"," onStartCommand called")
        val action = intent!!.action
        if (!TextUtils.isEmpty(action)) {
            when (action) {
                START_UPLOAD -> {
                    val fileList = intent.getParcelableExtra<FileUploadModel>(FILE_LIST)
                    fileList?.let { startFileUpload(it) }
                }
                CANCEL_UPLOAD -> cancelFileUpload()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun cancelFileUpload() {
        fileQueue.clear()
        if (mFileUploadTask != null) {
            mFileUploadHandler.removeCallbacks(mFileUploadTask!!)
            isFileUploadRunning = false
            mFileUploadTask = null
            hideNotification()
        }
    }

    private fun startFileUpload(fileList: FileUploadModel) {Log.e("UploadService"," startFileUpload called")
        //for (filePath in fileList) {
            if (!fileQueue.contains(fileList)) {Log.e("UploadService"," fileQueue called")
                fileQueue.add(fileList)
            }
        //}
        startUploadingFile()
    }

    private fun startUploadingFile() {
        if (mFileUploadTask == null) {
            mFileUploadTask = FileUploadTask()
        }
        if (!isFileUploadRunning) {
            isFileUploadRunning = true
            mFileUploadHandler.post(mFileUploadTask!!)
        }
    }

    inner class FileUploadTask : Runnable {
        override fun run() {
            if (fileQueue.isEmpty()) {Log.e("UploadService"," fileQueue.isEmpty() called")
                isFileUploadRunning = false
                Log.e(TAG, "File Upload Complete.")
                hideNotification()
                return
            }
            try {
                files = fileQueue.take()
                showUploadNotification()
                callUploadFileApi(files!!.data[0])
                } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun callUploadFileApi(file: FileData) {uploadWithTransferUtility(file.file,file.mUrl,0,0)}

    private fun hideNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        stopForeground(true)
    }

    private fun showUploadNotification() {
        val notificationManager = NotificationManagerCompat.from(this)
        var messageText = ""
        if (fileQueue.size > 0) {
            messageText = """$messageText
            ${fileQueue.size} is remaining."""
        }
        var channelId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "familheey_upload_service"
            val chan = NotificationChannel(channelId, "Familheey Background Service", NotificationManager.IMPORTANCE_DEFAULT)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
        }
        //val resultIntent = Intent(this, CreatePostActivity::class.java)
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        //val stackBuilder = TaskStackBuilder.create(this)
        //stackBuilder.addNextIntentWithParentStack(resultIntent)
        //val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Uploading post")
                .setContentText(messageText)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                //.setContentIntent(resultPendingIntent) //intent
                .setProgress(100, 0, false)
                .build()
        notificationManager.notify(1, notificationBuilder.build())
        startForeground(1, notification)
    }


    fun uploadWithTransferUtility(file: File?, key: String?, position: Int,count :Int ) {
        var counts = count
        notificationBuilder.setContentTitle("Uploading media in "+files?.type)
        notificationBuilder.setContentText(counts.toString()+"/"+files?.files?.size)
        notificationBuilder.setProgress(100,0,false)
        NotificationManagerCompat.from(this).notify(1,notificationBuilder.build())
        if(files?.data!![position].id.equals("")){
        notificationBuilder.setProgress(100, 100, false)
        NotificationManagerCompat.from(this).notify(1, notificationBuilder.build())
        if (files?.data!!.size > position + 1)
        uploadWithTransferUtility(files?.data!!.get(position + 1).file, files?.data!!.get(position + 1).mUrl, position + 1,++counts)
        else mFileUploadHandler.post(mFileUploadTask!!)
        }else {
            val configuration = ClientConfiguration()
                    .withMaxErrorRetry(2)
                    .withConnectionTimeout(1200000)
                    .withSocketTimeout(1200000)
            val transferUtility = TransferUtility.builder()
                    .context(this)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .s3Client(AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(Regions.US_EAST_1), configuration))
                    .build()
            val uploadObserver = transferUtility.upload(key
                    , file)
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    try {
                        if (TransferState.COMPLETED == state) {
                            var pos = 0
                            for ((i, data) in files?.files!!.withIndex())
                                if (data.filename.equals(files?.data!![position].id)) pos = i;
                            if (files?.data!![position].fileType.equals("MIME_TYPE_VIDEO")) {
                                ++files?.files!![pos].is_ready
                                if (files?.files!![pos].is_ready == 2) {
                                    var file = ArrayList<HistoryImages>()
                                    file.add(files?.files!![pos])
                                    FileUploadObject.updatePost(this@UploadService, UpdateRequest(files?.id!!.toString(), file, true))
                                    ++counts
                                }
                            } else {
                                var file = ArrayList<HistoryImages>()
                                file.add(files?.files!![pos])
                                FileUploadObject.updatePost(this@UploadService, UpdateRequest(files?.id!!.toString(), file, true))
                                ++counts
                            }
                            if (files?.data!!.size > position + 1) {
                                //albumDocuments.get(position - 1).setIsuploading(false)
                                uploadWithTransferUtility(files?.data!!.get(position + 1).file, files?.data!!.get(position + 1).mUrl, position + 1, counts)
                            } else mFileUploadHandler.post(mFileUploadTask!!)
                        }
                    } catch (e: Exception) {

                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    if (position > 0 && files?.data!!.size > position - 1) {
                        files?.data!!.get(position - 1).progress = (percentDone)
                    }
                    notificationBuilder.setProgress(100, percentDone, false)
                    NotificationManagerCompat.from(this@UploadService).notify(1, notificationBuilder.build())
                }

                override fun onError(id: Int, ex: Exception) {
                    if (position > 0) {
                        files?.data!!.removeAt(position - 1)
                    }
                }
            })
        }
    }
}