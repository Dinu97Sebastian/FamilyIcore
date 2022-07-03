package com.familheey.app.LazyUplaod

import android.os.Parcelable
import com.familheey.app.Models.Request.HistoryImages
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class FileUploadModel (
        var name: String,
        var type: String,
        var id: Int,
        var status: String,
        var Progress: Int,
        var count:Int,
        var data : ArrayList<FileData>,
        var files : ArrayList<HistoryImages>
) : Parcelable

@Parcelize
data class FileData (
        var file: File?,
        var mUrl: String,
        var isuploading: Boolean,
        var fileType: String,
        var progress: Int?,
        var id: String?
) : Parcelable