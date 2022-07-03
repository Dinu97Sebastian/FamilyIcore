package com.familheey.app.Notification

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.Utilities
import kotlinx.android.synthetic.main.item_activity.view.*


class ActivityAdapter(internal val items: MutableList<Activity>?, internal val listener: ActivityItemClick) : androidx.recyclerview.widget.RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items?.get(position)

            itemView.btn_more.setOnClickListener {
                itemMenu(it, position)
            }
            if (item!!.isMessageRead()) {
                itemView.notSeen.visibility = View.GONE
                itemView.btn_view.visibility = View.GONE
            } else {
                itemView.notSeen.visibility = View.VISIBLE
                itemView.btn_view.visibility = View.VISIBLE
            }

            itemView.txt_time.text = Utilities.getFormattedDate(item.createTime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                itemView.txt_tittle.text = (Html.fromHtml(item.message, Html.FROM_HTML_MODE_COMPACT))
            } else {
                itemView.txt_tittle.text = (Html.fromHtml(item.message))
            }
            if (item.type.equals("home") || item.type.equals("post") || item.type.equals("topic") || item.type.equals("announcement")) {
                if (item.subType.equals("familyfeed") || item.subType.equals("conversation") || item.subType.equals("topic") || item.subType.equals("")) {
                    postView(itemView, item)
                } else if (item.subType.equals("requestFeed") && !item.category.equals("request") && !item.category.equals("contribution_create")) {

                    //buttons
                    itemView.txt_one.visibility = View.VISIBLE
                    itemView.txt_two.visibility = View.GONE
                    itemView.txt_three.visibility = View.GONE
                    // itemView.txt_one.width = 350
                    itemView.txt_one.text = "  Support Now  "

                    itemView.requestView.visibility = View.VISIBLE
                    itemView.eventView.visibility = View.GONE
                    itemView.postView.visibility = View.GONE
                    itemView.familyView.visibility = View.GONE

                    itemView.txt_request_des.text = item.location
                    itemView.txt_req_user_name.text = item.createdByUser

                    Glide.with(itemView.familyLogo.context)
                            .load(R.drawable.family_logo)
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(itemView.familyLogo)
                    if (item.createdByPropic != null) {
                        Glide.with(itemView.req_pro_pic.context)
                                .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.createdByPropic)
                                .apply(Utilities.getCurvedRequestOptions())
                                .placeholder(R.drawable.avatar_male)
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.req_pro_pic)
                    } else {
                        Glide.with(itemView.req_pro_pic.context)
                                .load(R.drawable.avatar_male)
                                .apply(Utilities.getCurvedRequestOptions())
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.req_pro_pic)
                    }
                } else {

                    postView(itemView, item)
                }

            } else if (item.type.equals("user") || item.type.equals("family")) {

                itemView.requestView.visibility = View.GONE
                itemView.eventView.visibility = View.GONE
                itemView.postView.visibility = View.GONE
                itemView.familyView.visibility = View.VISIBLE


                itemView.txt_created_by.text = "By ${item.createdByUser}"
                itemView.family_location.text = item.location
                itemView.txt_family_member_count.text = item.membercount

                if (item.coverImage != null) {
                    Glide.with(itemView.familyLogo.context)
                            .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + item.coverImage)
                            .apply(Utilities.getCurvedRequestOptionsSmall())
                            .placeholder(R.drawable.family_logo)
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(itemView.familyLogo)
                } else {
                    Glide.with(itemView.familyLogo.context)
                            .load(R.drawable.family_logo)
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .into(itemView.familyLogo)
                }

                if (item.subType.equals("request")) {

                    //buttons
                    itemView.txt_one.visibility = View.VISIBLE
                    itemView.txt_two.visibility = View.VISIBLE
                    itemView.txt_three.visibility = View.GONE
                    itemView.txt_one.text = "    Accept    "
                    itemView.txt_two.text = "    Decline    "
                    // itemView.txt_one.width = 250
                    // itemView.txt_two.width = 250
                    itemView.txt_one.setOnClickListener { listener.onItemAcceptOrReject(position, "accepted") }
                    itemView.txt_two.setOnClickListener { listener.onItemAcceptOrReject(position, "rejected") }
                } else if (item.subType.equals("family_link")) {

                    //buttons
                    itemView.txt_one.visibility = View.VISIBLE
                    itemView.txt_two.visibility = View.VISIBLE
                    itemView.txt_three.visibility = View.GONE
                    itemView.txt_one.text = "    Accept    "
                    itemView.txt_two.text = "    Decline    "
                    // itemView.txt_one.width = 250
                    //itemView.txt_two.width = 250
                    itemView.txt_one.setOnClickListener { listener.linkingAcceptOrReject(position, "accepted") }
                    itemView.txt_two.setOnClickListener { listener.linkingAcceptOrReject(position, "rejected") }
                } else if (item.category?.contains("Membership reminder")!!) {

                    //buttons
                    itemView.txt_one.visibility = View.VISIBLE
                    itemView.txt_two.visibility = View.GONE
                    itemView.txt_three.visibility = View.GONE
                    itemView.txt_one.text = "  PAY NOW  "
                    //  itemView.txt_one.width = 300
                } else {

                    //buttons
                    itemView.txt_one.visibility = View.GONE
                    itemView.txt_two.visibility = View.GONE
                    itemView.txt_three.visibility = View.GONE
                }


            } else if (item.type.equals("event")) {
                if (item.subType.equals("") || item.subType.equals("guest_attending") || item.subType.equals("guest_interested")) {
                    itemView.requestView.visibility = View.GONE
                    itemView.eventView.visibility = View.VISIBLE
                    itemView.postView.visibility = View.GONE
                    itemView.familyView.visibility = View.GONE

                    if(item.location==""){
                        itemView.locationIcon.visibility = View.GONE
                    }

                    itemView.txt_event_location.text = item.location
                    itemView.txt_eveny_created_by.text = item.createdByUser
                    itemView.txt_event_date.text = item.dateFormat()

                    // Dinu(03-03-2021)->added category for check the event deleted or not
                    if (item.subType.equals("") && item.rsvp!! && item.category!="delete") {
                        //buttons
                        if(item.category!="reminder"){
                            itemView.txt_one.visibility = View.VISIBLE
                            itemView.txt_two.visibility = View.VISIBLE
                            itemView.txt_three.visibility = View.VISIBLE
                            itemView.txt_one.text = "GOING"

                            itemView.txt_one.setOnClickListener { listener.onItemGoingOrInterstedOrNotIntersted(position, "going") }
                            itemView.txt_two.setOnClickListener { listener.onItemGoingOrInterstedOrNotIntersted(position, "interested") }
                            itemView.txt_three.setOnClickListener { listener.onItemGoingOrInterstedOrNotIntersted(position, "not-going") }
                        }
                        else{
                            itemView.txt_one.visibility = View.GONE
                            itemView.txt_two.visibility = View.GONE
                            itemView.txt_three.visibility = View.GONE
                        }
                    } else {
                        itemView.txt_one.visibility = View.GONE
                        itemView.txt_two.visibility = View.GONE
                        itemView.txt_three.visibility = View.GONE
                    }



                    if (item.coverImage != null) {
                        Glide.with(itemView.familyLogo.context)
                                .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.EVENT_IMAGE + item.coverImage)
                                .apply(Utilities.getCurvedRequestOptionsSmall())
                                .placeholder(R.drawable.family_logo)
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.familyLogo)
                    } else {
                        Glide.with(itemView.familyLogo.context)
                                .load(R.drawable.family_logo)
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.familyLogo)
                    }
                    if (item.createdByPropic != null) {
                        Glide.with(itemView.event_user_profile.context)
                                .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.createdByPropic)
                                .apply(Utilities.getCurvedRequestOptions())
                                .placeholder(R.drawable.avatar_male)
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.event_user_profile)
                    } else {
                        Glide.with(itemView.event_user_profile.context)
                                .load(R.drawable.avatar_male)
                                .apply(Utilities.getCurvedRequestOptions())
                                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                                .into(itemView.event_user_profile)
                    }
                }
            }

            else if (item.type.equals("profile")) {

                itemView.requestView.visibility = View.GONE
                itemView.eventView.visibility = View.GONE
                itemView.postView.visibility = View.GONE
                itemView.familyView.visibility = View.GONE


                itemView.txt_created_by.visibility = View.GONE
                itemView.family_location.visibility = View.GONE
                itemView.txt_family_member_count.visibility = View.GONE

                    //buttons
                    itemView.txt_one.visibility = View.VISIBLE
                    itemView.txt_two.visibility = View.VISIBLE
                    itemView.txt_three.visibility = View.GONE
                    itemView.txt_one.text = "    Update Now    "
                    itemView.txt_two.text = "    Update Later    "
                    // itemView.txt_one.width = 250
                    // itemView.txt_two.width = 250
                    itemView.txt_one.setOnClickListener { listener.onItemUpdateNowOrUpdateLater(position, "updateNow") }
                    itemView.txt_two.setOnClickListener { listener.onItemUpdateNowOrUpdateLater(position, "updateLater") }
            }else if(item.type.equals("reverification")){
                /**for ticket 693**/
                itemView.requestView.visibility = View.GONE
                itemView.eventView.visibility = View.GONE
                itemView.postView.visibility = View.GONE
                itemView.familyView.visibility = View.GONE


                itemView.txt_created_by.visibility = View.GONE
                itemView.family_location.visibility = View.GONE
                itemView.txt_family_member_count.visibility = View.GONE

                //buttons
                itemView.txt_one.visibility = View.VISIBLE
                itemView.txt_two.visibility = View.GONE
                itemView.txt_three.visibility = View.GONE
                itemView.txt_one.text = "    Verify Now    "
                // itemView.txt_one.width = 250
                // itemView.txt_two.width = 250
                itemView.txt_one.setOnClickListener { listener.onClickVerifyNow(position, "verifyNow") }
            }

            else {
                postView(itemView, item)
            }


        }
    }


    private fun itemMenu(v: View, position: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.popup_menu_activity, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.delete -> listener.onItemDelete(position)

            }
            true
        }
        popup.show()
    }

    //CHECKSTYLE:OFF
    private fun postView(itemView: View, item: Activity?) {
        //buttons
        itemView.txt_one.visibility = View.GONE
        itemView.txt_two.visibility = View.GONE
        itemView.txt_three.visibility = View.GONE

        itemView.requestView.visibility = View.GONE
        itemView.familyView.visibility = View.GONE
        itemView.eventView.visibility = View.GONE
        itemView.postView.visibility = View.VISIBLE

        if (item?.coverImage != null) {
            Glide.with(itemView.familyLogo.context)
                    .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + "post/" + item.coverImage)
                    .apply(Utilities.getCurvedRequestOptionsSmall())
                    .placeholder(R.drawable.family_logo)
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(itemView.familyLogo)
        } else {
            Glide.with(itemView.familyLogo.context)
                    .load(R.drawable.family_logo)
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(itemView.familyLogo)
        }

        if (item?.createdByPropic != null) {
            Glide.with(itemView.img_post_pro.context)
                    .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.createdByPropic)
                    .apply(Utilities.getCurvedRequestOptions())
                    .placeholder(R.drawable.avatar_male)
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(itemView.img_post_pro)
        } else {
            Glide.with(itemView.img_post_pro.context)
                    .load(R.drawable.avatar_male)
                    .apply(Utilities.getCurvedRequestOptions())
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(itemView.img_post_pro)
        }
        itemView.txt_post_user_name.text = item?.createdByUser

        if (item?.privacyType.equals("public")) {
            itemView.txt_post_family_type.text = "Public"
            itemView.post_divider.visibility = View.VISIBLE
        } else {
            if (item?.toGroupName.isNullOrEmpty()) {
                itemView.post_divider.visibility = View.GONE
            } else itemView.post_divider.visibility = View.VISIBLE
            itemView.txt_post_family_type.text = item?.toGroupName
        }
        if (item?.description.isNullOrEmpty()) {
            itemView.txt_des.visibility = View.GONE
        }
//        //Dinu(03/07/2021) for display conversation contents
//        else if(item?.category.equals("conversation")){
//            itemView.txt_des.visibility = View.VISIBLE
//            itemView.txt_des.text = item?.comment
//        }
        else{
            itemView.txt_des.visibility = View.VISIBLE
            itemView.txt_des.text = item?.description
        }

    }

    //CHECKSTYLE:ON

    interface ActivityItemClick {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
        fun onItemAcceptOrReject(position: Int, status: String)
        fun onItemGoingOrInterstedOrNotIntersted(position: Int, status: String)
        fun linkingAcceptOrReject(position: Int, status: String)
        // for handle profile update
       fun onItemUpdateNowOrUpdateLater(position: Int,status: String)
       fun onClickVerifyNow(position: Int,status: String)
    }

}