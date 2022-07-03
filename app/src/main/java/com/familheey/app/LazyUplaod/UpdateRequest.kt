package com.familheey.app.LazyUplaod

import com.familheey.app.Models.Request.HistoryImages

data class UpdateRequest(
        var id: String,
        var post_attachment: ArrayList<HistoryImages>,
        var is_active: Boolean
)

data class Attachment(
        var type: String,
        var filename: String
)