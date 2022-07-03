package com.familheey.app.Post

data class PostResponse(
    val `data`: List<Data>,
    val message: String
)

data class Data(
    val category_id: Int,
    val conversation_enabled: Boolean,
    val createdAt: String,
    val created_by: Int,
    val elasticsearch_id: Any,
    val file_type: Any,
    val firebase_link: Any,
    val from_id: Any,
    val id: Int,
    val is_active: Boolean,
    val is_approved: Int,
    val is_shareable: Boolean,
    val orgin_id: Any,
    val post_attachment: List<Any>,
    val post_info: PostInfo,
    val post_ref_id: String,
    val post_type: String,
    val privacy_type: String,
    val publish_id: Any,
    val publish_mention_items: Any,
    val publish_mention_users: Any,
    val publish_type: Any,
    val shared_user_id: Any,
    val snap_description: String,
    val sort_date: String,
    val title: String,
    val to_group_id: Int,
    val to_user_id: Any,
    val type: String,
    val updatedAt: String,
    val url_metadata: Any,
    val valid_urls: Any
)

class PostInfo(
)