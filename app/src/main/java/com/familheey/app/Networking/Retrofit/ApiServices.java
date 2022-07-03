package com.familheey.app.Networking.Retrofit;

import com.familheey.app.Discover.model.ElasticSearch;
import com.familheey.app.Fragments.Posts.PostAdapterInFamilyFeed;
import com.familheey.app.Fragments.Posts.PostData;
import com.familheey.app.Fragments.Posts.StickyPost;
import com.familheey.app.LazyUplaod.UpdateRequest;
import com.familheey.app.Models.Request.AlbumRequest;
import com.familheey.app.Models.Request.CreateEventRequest;
import com.familheey.app.Models.Request.Device;
import com.familheey.app.Models.Request.FamilyHistoryUpdateRequest;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Models.Request.PostShareRequest;
import com.familheey.app.Models.Request.RequstComment;
import com.familheey.app.Models.Response.AnnouncementCount;
import com.familheey.app.Models.Response.Banner;
import com.familheey.app.Models.Response.CancelRequestResponse;
import com.familheey.app.Models.Response.ContributeItemAmountSplit;
import com.familheey.app.Models.Response.ConversationMute;
import com.familheey.app.Models.Response.EventsharelistResponse;
import com.familheey.app.Models.Response.GetEventByIdResponse;
import com.familheey.app.Models.Response.Keys;
import com.familheey.app.Models.Response.PostResponse;
import com.familheey.app.Models.Response.ShareUser;
import com.familheey.app.Models.Response.UrlParse;
import com.familheey.app.Need.Contributor;
import com.familheey.app.Need.ContributorsWrapper;
import com.familheey.app.Need.ItemCreateResponse;
import com.familheey.app.Need.Need;
import com.familheey.app.Need.NeedDetailWrapper;
import com.familheey.app.Need.NeedRequestWrapper;
import com.familheey.app.Notification.AdminAcceptResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiServices {

    @POST("GetBanners")
    Observable<Response<ArrayList<Banner>>> GetBanners(@Body RequestBody requestBody);

    @POST("cognito_userpool_rgstr")
    Call<ResponseBody> registerUser(@Body RequestBody requestBody);

    @POST("validatePhoneNumber")
    Call<ResponseBody> validatePhoneNumber(@Body RequestBody requestBody);

    @POST("cognito_userpool_rgstr")
    Call<ResponseBody> registerUser(@Body MultipartBody multipartBody);

    @POST("confirmotp")
    Call<ResponseBody> confirmOTP(@Body RequestBody requestBody);

    @POST("cognito_userpool_rsndOTP")
    Call<ResponseBody> resendOTP(@Body RequestBody requestBody);

    @POST("edit_profile")
    Call<ResponseBody> completeUserRegistration(@Body MultipartBody multipartBody);


    @POST("create_family")
    Call<ResponseBody> createFamily(@Body RequestBody requestBody);

    @POST("update_family")
    Call<ResponseBody> updateFamily(@Body RequestBody requestBody);

    @POST("upload_file_tos3")
    Call<ResponseBody> updateFamilyHistoryImage(@Body MultipartBody multipartBody);

    @POST("update_family")
    Call<ResponseBody> updateFamily(@Body MultipartBody multipartBody);

    @POST("fetch_link_family")
    Call<ResponseBody> fetchFamiliesForLinking(@Body RequestBody requestBody);

    @POST("fetch_group")
    Call<ResponseBody> fetchSimilarFamilies(@Body RequestBody requestBody);

    @POST("joinFamily")
    Call<ResponseBody> joinFamily(@Body RequestBody requestBody);

    @POST("request_link_family")
    Call<ResponseBody> requestFamiliesForLinking(@Body RequestBody requestBody);

    @POST("viewFamily")
    Call<ResponseBody> listAllfamily(@Body RequestBody requestBody);

    @POST("posts/getFamilyListForPost")
    Call<ResponseBody> listAllfamilyPost(@Body RequestBody requestBody);

    @POST("listAllMembers")
    Call<ResponseBody> listMember(@Body RequestBody requestBody);

    @POST("globalsearch")
    Call<ResponseBody> searchData(@Body RequestBody requestBody);

    @POST("getFamilyById")
    Call<ResponseBody> getFamilyDetailsByID(@Body RequestBody requestBody);

    /**ticket 693**/
    @POST("loginWithOtp")
    Call<ResponseBody> getMobileDetailsFromUserId(@Body RequestBody requestBody);
    @POST("posts/create")
    Call<ResponseBody> createPostRequestInBackground(@Body RequestBody requestBody);

    @POST("addToGroup")
    Call<ResponseBody> addToFamily(@Body RequestBody requestBody);

    @POST("viewMembers")
    Call<ResponseBody> viewFamilyMembers(@Body RequestBody requestBody);

    @POST("admin_view_requests")
    Call<ResponseBody> viewMemberRequest(@Body RequestBody requestBody);

    @POST("admin_request_action")
    Call<ResponseBody> memberRequestAction(@Body RequestBody requestBody);

    @POST("listGroupFolders")
    Call<ResponseBody> listGroupFolders(@Body RequestBody requestBody);

    @POST("createFolder")
    Call<ResponseBody> createFolder(@Body RequestBody requestBody);

    @POST("updateFolder")
    Call<ResponseBody> editFolder(@Body RequestBody requestBody);

    @POST("getallRelations")
    Call<ResponseBody> getallRelations(@Body RequestBody requestBody);

    @POST("addRelation")
    Call<ResponseBody> addRelation(@Body RequestBody requestBody);

    @POST("updateRelation")
    Call<ResponseBody> updateRelation(@Body RequestBody requestBody);

    @POST("update_groupmaps")
    Call<ResponseBody> updateUserRestrictionStatus(@Body RequestBody requestBody);

    @Multipart
    @POST("uploadFile")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part image, @Part("folder_id") RequestBody folder_id, @Part("user_id") RequestBody user_id, @Part("group_id") RequestBody group_id, @Part("is_sharable") RequestBody is_sharable);

    @POST("uploadFile")
    Call<ResponseBody> uploadFile(@Body MultipartBody body);

    @Multipart
    @POST("update_agenda")
    Call<ResponseBody> updateAgendaMultipart(@Part MultipartBody.Part body, @Part("user_id") RequestBody user_id, @Part("event_id") RequestBody event_id, @Part("title") RequestBody title, @Part("description") RequestBody description, @Part("start_date") RequestBody start_date, @Part("end_date") RequestBody end_date, @Part("agenda_id") RequestBody agenda_id);

    @Multipart
    @POST("create_agenda")
    Call<ResponseBody> createAgendaMultipart(@Part MultipartBody.Part body, @Part("user_id") RequestBody user_id, @Part("event_id") RequestBody event_id, @Part("title") RequestBody title, @Part("description") RequestBody description, @Part("start_date") RequestBody start_date, @Part("end_date") RequestBody end_date);


    @Multipart
    @POST("uploadFile")
    Call<ResponseBody> uploadAlbumFile(@Part List<MultipartBody.Part> images, @Part("folder_id") RequestBody folder_id, @Part("user_id") RequestBody user_id, @Part("is_sharable") RequestBody is_sharable, @Part("folder_type") RequestBody folderType);

    @POST("viewContents")
    Call<ResponseBody> viewContents(@Body RequestBody requestBody);

    @POST("view_profile")
    Call<ResponseBody> viewUserProfile(@Body RequestBody requestBody);

    @POST("unFollow")
    Call<ResponseBody> unFollowtheFamily(@Body RequestBody requestBody);

    @POST("Follow")
    Call<ResponseBody> FollowtheFamily(@Body RequestBody requestBody);

    @POST("create_events")
    Call<ResponseBody> createEvents(@Body RequestBody requestBody);

    @POST("update_events")
    Call<ResponseBody> updateEvents(@Body RequestBody requestBody);

    @POST("fetch_category")
    Call<ResponseBody> fetchEventcategory(@Body RequestBody requestBody);

    @POST("request_link_family")
    Call<ResponseBody> requestLinkFamily(@Body RequestBody requestBody);

    @POST("inviteViaSms")
    Call<ResponseBody> inviteViaSms(@Body RequestBody requestBody);

    @POST("listallInvitation")
    Call<ResponseBody> fetchUserInvitations(@Body RequestBody requestBody);

    @POST("invitationResp")
    Call<ResponseBody> respondToFamilyInvitation(@Body RequestBody requestBody);

    @POST("explore_events2")
    Call<ResponseBody> exploreEvents(@Body RequestBody requestBody);

    @POST("create_agenda")
    Call<ResponseBody> createAgenda(@Body RequestBody requestBody);

    @POST("update_agenda")
    Call<ResponseBody> updateAgenda(@Body RequestBody requestBody);

    @POST("list_agenda")
    Call<ResponseBody> listAgenda(@Body RequestBody requestBody);

    @POST("created_by_me2")
    Call<ResponseBody> createdByMe(@Body RequestBody requestBody);

    @POST("get_event_byId")
    Call<ResponseBody> getEventById(@Body RequestBody requestBody);

    @POST("respondToEvents")
    Call<ResponseBody> respondToRSVP(@Body RequestBody requestBody);

    @POST("event_invitations2")
    Call<ResponseBody> eventInvitation(@Body RequestBody requestBody);

    @POST("getGroupsToAddMember")
    Call<ResponseBody> getAllGroupsBasedOnUserId(@Body RequestBody requestBody);

    @POST("get_eventitems")
    Call<ResponseBody> getEventItems(@Body RequestBody requestBody);

    @POST("add_event_signupitems")
    Call<ResponseBody> addEventSignup(@Body RequestBody requestBody);

    @POST("update_event_signupitems")
    Call<ResponseBody> callUpdateEventSignupApi(@Body RequestBody requestBody);

    @POST("listEventFolders")
    Call<ResponseBody> listEventFolders(@Body RequestBody requestBody);

    @POST("listUserFolders")
    Call<ResponseBody> listUserFolders(@Body RequestBody requestBody);


    @POST("make_pic_cover")
    Call<ResponseBody> makePicCover(@Body RequestBody requestBody);

    @POST("delete_file")
    Call<ResponseBody> deleteFile(@Body RequestBody requestBody);


    @POST("addUserHistory")
    Call<ResponseBody> addHistory(@Body RequestBody requestBody);

    @POST("group_events")
    Call<ResponseBody> fetchGroupEvents(@Body RequestBody requestBody);

    @POST("group_events_listing2")
    Call<ResponseBody> fetchFamilyGroupEvents(@Body RequestBody requestBody);

    @POST("list_linked_family")
    Call<ResponseBody> fetchLinkedFamilies(@Body RequestBody requestBody);

    @POST("fetch_calender")
    Call<ResponseBody> fetchCalendar(@Body RequestBody requestBody);

    @POST("blocked_users")
    Call<ResponseBody> getBlockedUsers(@Body RequestBody requestBody);

    @POST("get_guests_count")
    Call<ResponseBody> getGuestCount(@Body RequestBody requestBody);

    @POST("get_going_guest_details")
    Call<ResponseBody> goingGuestDetails(@Body RequestBody requestBody);

    @POST("getRSVP_guest_details")
    Call<ResponseBody> getRsvpGuestList(@Body RequestBody requestBody);

    @POST("event_invited_byUser")
    Call<ResponseBody> getInvitedGuestList(@Body RequestBody requestBody);

    @POST("event_shareor_invite")
    Call<ResponseBody> eventGroupInvite(@Body RequestBody requestBody);

    @POST("leave_family")
    Call<ResponseBody> leaveFamily(@Body RequestBody requestBody);

    @POST("user_group_members")
    Call<ResponseBody> viewPeopleforShare(@Body RequestBody requestBody);

    @POST("action_linked_family")
    Call<ResponseBody> requestUnlinkFamily(@Body RequestBody requestBody);

    @POST("add_eventitems_contribution")
    Call<ResponseBody> addSignUpDetails(@Body RequestBody requestBody);

    @POST("update_eventitems_contribution")
    Call<ResponseBody> updateSignUpDetails(@Body RequestBody requestBody);

    @POST("item_contributor_list")
    Call<ResponseBody> fetchEventSignUpContributors(@Body RequestBody requestBody);

    @POST("receive_feedback")
    Call<ResponseBody> addFeedback(@Body MultipartBody multipartBody);

    @POST("check_link")
    Call<ResponseBody> checkLink(@Body RequestBody requestBody);

    @POST("create_reminder")
    Observable<Response<JsonObject>> createReminder(@Body CreateEventRequest eventRequest);



    @GET
    Observable<Response<JsonObject>> getRoute(@Url String url);

    @POST("event_share_list")
    Observable<Response<EventsharelistResponse>> getShareList(@Body RequestBody requestBody);

    @POST("getMutualConnections")
    Call<ResponseBody> getMutualConnections(@Body RequestBody requestBody);

    @POST("get_family_mutual_list")
    Call<ResponseBody> getFamilyMutualConnections(@Body RequestBody requestBody);

    @POST("get_connections")
    Call<ResponseBody> getUserConnections(@Body RequestBody requestBody);

    @POST("update_family")
    Observable<Response<Void>> updateFamilyHistory(@Body FamilyHistoryUpdateRequest updateRequest);

    @POST("update_events")
    Call<ResponseBody> updateEventMedias(@Body MultipartBody requestBody);

    @POST("event_deleteorcancel")
    Call<ResponseBody> delterOrCancelEvent(@Body RequestBody requestBody);


    @POST("addEventContact")
    Call<ResponseBody> addEventContact(@Body RequestBody requestBody);


    @POST("deleteEventContact")
    Call<ResponseBody> deleteEventContact(@Body RequestBody requestBody);

    @POST("editEventContact")
    Call<ResponseBody> editEventContact(@Body RequestBody requestBody);

    @Multipart
    @POST("upload_file_tos3")
    Call<ResponseBody> uploadMultiple(
            @Part("name") RequestBody name,
            @Part List<MultipartBody.Part> files);

    @Multipart
    @POST("upload_file_tos3")
    Call<ResponseBody> uploadSingle(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);



    @Multipart
    @POST
    Call<ResponseBody> uploadMultipleChatAttachment(
            @Url String commentUrl,
            @Part("name") RequestBody name,
            @Part("post_id") RequestBody postId,
            @Part("comment") RequestBody comment,
            @Part("commented_by") RequestBody commentedBy,
            @Part("group_id") RequestBody group_id,
            @Part List<MultipartBody.Part> files);

    @Multipart
    @POST
    Call<ResponseBody> uploadMultipleChatAttachment(
            @Url String commentUrl,
            @Part("name") RequestBody name,
            @Part("post_id") RequestBody postId,
            @Part("comment") RequestBody comment,
            @Part("commented_by") RequestBody commentedBy,
            @Part("group_id") RequestBody group_id,
            @Part("quoted_item") RequestBody quoted_item,
            @Part("quoted_id") RequestBody quoted_id,
            @Part("quoted_user") RequestBody quoted_user,
            @Part("quoted_date") RequestBody quoted_date,

            @Part List<MultipartBody.Part> files);

    @Multipart
    @POST
    Call<ResponseBody> uploadMultipleChatAttachment(
            @Url String commentUrl,
            @Part("name") RequestBody name,
            @Part("topic_id") RequestBody postId,
            @Part("comment") RequestBody comment,
            @Part("commented_by") RequestBody commentedBy,
            @Part List<MultipartBody.Part> files);

    @POST("needs/create")
    Observable<Response<Void>> createNeed(@Body Need request);

    @POST("needs/update")
    Observable<Response<Void>> updateNeed(@Body Need request);
    @POST("needs/delete")
    Observable<Response<Void>> deleteNeed(@Body RequestBody request);
    @POST("needs/create_items")
    Observable<Response<ItemCreateResponse>> createItemsNeed(@Body RequestBody request);

    @POST("needs/update_items")
    Observable<Response<Void>> updateItemsNeed(@Body RequestBody request);

    @POST("needs/delete_items")
    Observable<Response<Void>> deleteItemsNeed(@Body RequestBody request);

    @POST("posts/addUpdate_Announcement_Seen")
    Observable<Response<Void>> addUpdate_Announcement_Seen(@Body RequestBody request);

    @POST("posts/get_announcement_banner_count")
    Observable<Response<ArrayList<AnnouncementCount>>> get_announcement_banner_count(@Body RequestBody request);

    @POST("posts/create")
    Observable<Response<com.familheey.app.Post.PostResponse>> createPost(@Body PostRequest request);

    @POST("publish_post")
    Observable<Response<Void>> createThankYouPost(@Body PostRequest request);

    @POST("get_post_default_image")
    Observable<Response<ArrayList<HistoryImages>>> get_post_default_image(@Body RequestBody request);


    @POST("copy_file_between_bucket")
    Observable<Response<Void>> copy_file_between_bucket(@Body RequestBody request);

    @POST("posts/update_post")
    Observable<Response<Void>> updatePost(@Body PostRequest request);

    @POST("posts/update_post")
    Call<ResponseBody> updatePostInBackground(@Body RequestBody requestBody);


    @POST("posts/activatePost")
    Observable<Response<Void>> activatePost(@Body RequestBody request);

    @POST("posts/deactivatePost")
    Observable<Response<Void>> deactivatePost(@Body RequestBody request);


    @POST("posts/pending_approvals")
    Observable<Response<PostResponse>> getPendingApprovals(@Body RequestBody requestBody);

    @POST("posts/post_by_family")
    Observable<Response<PostResponse>> getPost(@Body RequestBody requestBody);

    /*Megha(30/08/21)-> api for rating*/
    @POST("posts/rate")
    Observable<Response<ResponseBody>> rate(@Body RequestBody requestBody);

    @POST("posts/post_sticky_by_family")
    Observable<Response<List<StickyPost>>> getStickyPost(@Body RequestBody requestBody);

    @POST("post/stickyPost")
    Observable<Response<Void>> makeStickyPost(@Body RequestBody requestBody);

    @POST("posts/get_my_post_aggregate")
    Observable<Response<PostResponse>> getPostAggregate(@Body RequestBody requestBody);

    @POST("clearNotification")
    Observable<Response<Void>> clearNotification(@Body RequestBody requestBody);

    @POST("readNotifications")
    Observable<Response<Void>> readNotifications(@Body RequestBody requestBody);

    @POST("posts/get_my_post")
    Observable<Response<PostResponse>> getMyPost(@Body RequestBody requestBody);

    @POST("posts/getByID")
    Observable<Response<PostResponse>> getMyPostWithSelectedFamilies(@Body RequestBody requestBody);

    @POST("posts/announcement_list")
    Observable<Response<PostResponse>> getAnnouncement(@Body RequestBody requestBody);

    @POST("posts/approve_reject_post")
    Observable<Response<PostResponse>> approveRejectPost(@Body RequestBody requestBody);

    @POST("posts/public_feed")
    Observable<Response<PostResponse>> getpublicfeed(@Body RequestBody requestBody);


    @POST("posts/deletePost")
    Observable<Response<Void>> deletePost(@Body RequestBody requestBody);

    @POST("posts/mute_conversation")
    Observable<Response<ConversationMute>> muteConversation(@Body RequestBody requestBody);

    @POST("posts/remove_post")
    Observable<Response<Void>> removePost(@Body RequestBody requestBody);

    @POST("uploadFile")
    Observable<Response<ResponseBody>> albumUpload(@Body AlbumRequest requestBody);

    @POST("spam_report")
    Observable<Response<Void>> reportPost(@Body RequestBody requestBody);

    @POST("posts/unread")
    Observable<Response<Void>> unread_Unread_Post(@Body RequestBody requestBody);

    @POST("posts/add_view_count")
    Observable<Response<Void>> addViewCount(@Body RequestBody requestBody);

    @POST
    Call<ResponseBody> postComment(@Url String commentUrl, @Body RequestBody requestBody);

    @POST
    Call<ResponseBody> postComment(@Url String commentUrl, @Body RequstComment requestBody);

    @POST("posts/post_share")
    Observable<Response<Void>> postShare(@Body PostShareRequest postShareRequest);

    @POST("posts/getCommentsByPost")
    Call<ResponseBody> getComments(@Body RequestBody requestBody);

    //Dinu(03-09-2021)--to fetch post comment replies
    @POST("posts/getCommentReplies")
    Call<ResponseBody> getCommentReplies(@Body RequestBody requestBody);

    @POST("posts/list_post_views")
    Observable<Response<EventsharelistResponse>> getPostViewList(@Body RequestBody requestBody);

    @POST("posts/getCommonSharedUserList")
    Observable<Response<ArrayList<ShareUser>>> getPostShareList(@Body RequestBody requestBody);

    @POST("posts/updateLastReadMessage")
    Observable<Response<EventsharelistResponse>> readStatus(@Body RequestBody requestBody);

    @POST("delete_event_signupitems")
    Call<ResponseBody> deleteSignup(@Body RequestBody requestBody);

    @POST("registerToken")
    Observable<Response<Void>> saveDevice(@Body Device requestBody);

    @POST("removeFolder")
    Call<ResponseBody> removeFolder(@Body RequestBody requestBody);

    @POST("user_mutual_group")
    Call<ResponseBody> getMutualFamilies(@Body RequestBody requestBody);

    @POST("onBoardCheck")
    Observable<Response<ResponseBody>> onBoardCheck(@Body RequestBody requestBody);

    @POST("GetKeys")
    Observable<Response<Keys>> getKeys(@Body RequestBody requestBody);

    @POST("update_file_name")
    Call<ResponseBody> updateFileName(@Body RequestBody requestBody);

    @POST("pending_requests")
    Call<ResponseBody> getPendingRequests(@Body RequestBody requestBody);

    @POST("delete_pending_requests")
    Call<ResponseBody> deletePendingRequest(@Body RequestBody requestBody);

    @POST("pending_requests")
    Observable<Response<CancelRequestResponse>> getPendingRequest(@Body RequestBody requestBody);

    @POST("needs/post_need_list")
    Observable<Response<NeedRequestWrapper>> getNeedsRequestList(@Body RequestBody requestBody);

    @POST("needs/post_need_detail")
    Observable<Response<NeedDetailWrapper>> getNeedsRequestDetail(@Body RequestBody requestBody);

    @POST("needs/contribution_list")
    Observable<Response<ContributorsWrapper>> getNeedsContributors(@Body RequestBody requestBody);

    @POST("contributionlistByUser")
    Observable<Response<ArrayList<Contributor>>> contributionlistByUser(@Body RequestBody requestBody);

    @POST("contributionStatusUpdation")
    Observable<Response<Void>> contributionStatusUpdation(@Body RequestBody requestBody);

    @POST("needs/contribution_create")
    Observable<Response<JsonObject>> addContribution(@Body RequestBody requestBody);

    @POST("groupMapUpdate")
    Observable<Response<JsonObject>> groupMapUpdate(@Body RequestBody requestBody);

    @POST("openAppGetParams")
    Observable<Response<UrlParse>> openAppGetParams(@Body RequestBody requestBody);

    @POST("invitationResp")
    Observable<Response<ResponseBody>> respondToFamilyInvitationRx(@Body RequestBody requestBody);


    @POST("admin_request_action")
    Observable<Response<AdminAcceptResponse>> memberRequestActionNew(@Body RequestBody requestBody);

    @POST("getAccessToken")
    Observable<JsonObject> delegation(@Body RequestBody requestBody);


    @POST("family_link_exist")
    Observable<JsonObject> familyLinkExist(@Body RequestBody requestBody);


    @POST("payment/stripe_oauth_link_generation")
    Observable<Response<JsonObject>> stripe_oauth_link_generation(@Body RequestBody requestBody);


    @POST("payment/stripeGetaccountById")
    Observable<Response<JsonObject>> stripeGetaccountById(@Body RequestBody requestBody);


    @POST("needs/get_contribute_itemAmount_split")
    Observable<Response<ArrayList<ContributeItemAmountSplit>>> get_contributeItemAmountSplit(@Body RequestBody requestBody);

    @POST("elastic/getRecords")
    Observable<Response<ElasticSearch>> getRecords(@Body RequestBody requestBody);


    @POST("respondToEvents")
    Observable<Response<JsonObject>> respondToRSVP1(@Body RequestBody requestBody);

    @POST("get_event_byId")
    Observable<Response<GetEventByIdResponse>> getEventsByEventId(@Body RequestBody requestBody);

    @POST("postUpdate")
    Observable<Response<Void>> postUpdate(@Body UpdateRequest request);
}
