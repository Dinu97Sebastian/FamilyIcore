package com.familheey.app.Topic

import Members
import MembershipDashboard
import MembershipType
import MembershipTypePeriod
import MyFamilySelectionWraper
import WraperMembershipType
import com.familheey.app.Discover.model.DiscoverWrapper
import com.familheey.app.Models.FamilySuggestionWrapper
import com.familheey.app.Models.ProfileWraper
import com.familheey.app.Models.Request.AddMember
import com.familheey.app.Models.Request.RequstComment
import com.familheey.app.Models.Response.MyFamilyWraper
import com.familheey.app.Models.Response.NotificationSeetings
import com.familheey.app.Models.Response.PostResponse
import com.familheey.app.Models.Response.Profile
import com.familheey.app.PaymentHistory.PaymentWrapper
import com.familheey.app.membership.Membership
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RestApiService {

    @POST("reject_user_topic")
    fun rejectUserTopic(@Body req: RequestBody): Observable<Response<JsonObject>>

    @POST("accept_user_topic")
    fun acceptUserTopic(@Body req: RequestBody): Observable<Response<JsonObject>>

    @POST("update_topic")
    fun updateTopic(@Body req: TopicCreateUpdateRequest): Observable<Response<TopicsCreateResponse>>

    @POST("topic_create")
    fun createTopic(@Body req: TopicCreateRequest): Observable<Response<TopicsCreateResponse>>

    @POST("posts/getCommentsByPost")
    fun getComments(@Body req: RequestBody): Observable<Response<JsonObject>>

    @POST("topic_detail")
    fun getSingleTopic(@Body req: RequestBody): Observable<Response<TopicsBaseDetailModel>>

    @POST("topic_list")
    fun getTopics(@Body req: RequestBody): Observable<Response<TopicsBaseModel>>


    @POST("commonTopicListByUsers")
    fun getCommonTopicListByUsers(@Body req: RequestBody): Observable<Response<TopicsBaseModel>>

    @POST("topic_users_list")
    fun getUsersForTopics(@Body req: RequestBody): Observable<Response<UserWrapper>>

    @POST("add_users_to_topic")
    fun addUsersToTopic(@Body req: RequestBody): Observable<Response<Void>>


    @POST("topic_comment")
    fun topicComment(@Body req: RequstComment): Observable<Response<Void>>

    @POST("activateTopic")
    fun activateTopic(@Body req: RequestBody): Observable<Response<Void>>

    @POST("deactivateTopic")
    fun deactivateTopic(@Body req: RequestBody): Observable<Response<Void>>

    @POST("posts/delete_comment")
    fun topicDelete(@Body req: RequestBody): Observable<Response<Void>>


    @POST("getUserAdminGroups")
    fun getUserAdminGroups(@Body req: RequestBody): Observable<Response<MyFamilySelectionWraper>>

    @POST("get_suggested_group_newUser")
    fun getNewUserFamilySuggestions(@Body requestBody: RequestBody?): Observable<Response<FamilySuggestionWrapper>>

    @POST("joinFamily")
    fun joinFamily(@Body requestBody: RequestBody?): Observable<Response<JsonObject>>

    @POST("add_membership_lookup")
    fun addMembershipLookup(@Body req: Membership): Observable<Response<Void>>

    @POST("update_membership_lookup")
    fun updateMembershipLookup(@Body req: Membership): Observable<Response<Void>>

    @POST("list_membership_lookup")
    fun listMembershipLookup(@Body req: RequestBody): Observable<Response<ArrayList<MembershipType>>>

    @POST("membership_dashboard")
    fun getMembershipDashboard(@Body req: RequestBody): Observable<Response<MembershipDashboard>>

    @POST("viewMembers")
    fun getMembers(@Body req: RequestBody): Observable<Response<Members>>

    @POST("user_membership_update")
    fun userMembershipUpdate(@Body req: RequestBody): Observable<Response<Void>>

    @POST("getMembershiptypeById")
    fun getMembershiptypeById(@Body req: RequestBody): Observable<Response<WraperMembershipType>>

    @POST("get_membership_lookup_periods")
    fun getMembershipLookupPeriods(): Observable<Response<ArrayList<MembershipTypePeriod>>>

    @POST("Membership_reminder")
    fun getMembershipReminder(@Body req: RequestBody): Observable<Response<Void>>

    @POST("payment/stripeCreatePaymentIntent")
    fun getstripeCreatePaymentIntent(@Body req: RequestBody): Observable<Response<JsonObject>>

    @POST("paymentHistoryByUserid")
    fun getPaymentHistory(@Body req: RequestBody): Observable<Response<PaymentWrapper>>

    @POST("paymentHistoryByGroupid")
    fun paymentHistoryByGroup(@Body req: RequestBody): Observable<Response<PaymentWrapper>>

    @POST("globalsearch")
    fun searchData(@Body requestBody: RequestBody?): Observable<Response<DiscoverWrapper>>

    @POST("get_user_commented_post")
    fun getUserCommentedPost(@Body requestBody: RequestBody?): Observable<Response<ArrayList<PostMessage>>>

    @POST("view_profile")
    fun viewUserProfile(@Body requestBody: RequestBody?): Observable<Response<ProfileWraper>>

    @POST("addToGroup")
    fun addToFamily(@Body requestBody: AddMember): Observable<Response<Void>>

    @POST("edit_profile")
    fun updateSettings(@Body requestBody: RequestBody): Observable<Response<Profile>>

    @POST("get_notification_settings")
    fun getNotificationSettings(@Body requestBody: RequestBody): Observable<Response<ArrayList<NotificationSeetings>>>


    @POST("viewFamily")
    fun getMyFamilies(@Body requestBody: RequestBody?): Observable<Response<MyFamilyWraper>>

    @POST("posts/announcement_list")
    fun getPost(@Body requestBody: RequestBody): Observable<Response<PostResponse>>
}