package com.familheey.app.Need

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.skydoves.androidribbon.ribbonView
import kotlinx.android.synthetic.main.item_needs_contributor.view.*


class SingleContributorAdapter(internal val items: ArrayList<Contributor>, internal val isAdmin: Boolean, internal val type: String?, internal val context: Context, internal val listener: OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<SingleContributorAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_needs_contributor, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {}
    }
    //CHECKSTYLE:OFF
    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val item = items[position]
            if (item.contributeUserId == 2) {
                itemView.txt_known_member.visibility = View.VISIBLE
                itemView.txt_known_member.text = "Non App Member"
            } else {
                itemView.txt_known_member.visibility = View.GONE
            }
            if (item.isIs_anonymous) {
                if (isAdmin) {
                    itemView.postedIn.text = item.location
                    if (item.contributeUserId == 2) {
                        itemView.name.text = item.paid_user_name
                    } else {
                        itemView.name.text = item.fullName + "(Anonymous)"
                    }
                } else {

                    itemView.postedIn.text = "---------"
                    itemView.name.text = "Anonymous"
                }
            } else {
                itemView.postedIn.text = item.location
                if (item.contributeUserId == 2) {
                    itemView.name.text = item.paid_user_name
                } else {
                    itemView.name.text = item.fullName
                }
                if (item.propic != null) {
                    Glide.with(itemView.context)
                            .load(ApiPaths.S3_DEV_IMAGE_URL_SQUARE + ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                            .apply(Utilities.getCurvedRequestOptions())
                            .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                            .placeholder(R.drawable.avatar_male)
                            .into(itemView.userProfileImage)
                }
            }


            itemView.multiple.visibility = View.GONE
            itemView.thankyou.text = "Acknowledge"
            itemView.thankyou.setOnClickListener { view: View? -> listener.onthankyouPost(items.get(position)) }
            if (isAdmin && item.contributeUserId != 2) {
                itemView.call.visibility = View.GONE
                if (item.isIs_acknowledge || item.isIs_thank_post) {
                    itemView.thankyou.visibility = View.GONE
                    itemView.cb_on.visibility = View.GONE
                } else {
                    itemView.cb_on.visibility = View.GONE
                    itemView.thankyou.visibility = View.VISIBLE
                }
            } else {
                itemView.whole_view.setBackgroundColor(Color.parseColor("#FFFFFF"))
                itemView.cb_on.visibility = View.GONE
                if (isAdmin && item.contributeUserId != 2) {
                    itemView.call.visibility = View.VISIBLE
                } else {
                    itemView.call.visibility = View.GONE
                }
                itemView.thankyou.visibility = View.GONE
            }


            if (item.isIs_acknowledge) {

                itemView.ribbonLayout01.ribbonHeader = ribbonView(context) {
                    setText("Acknowledged")
                    setRibbonBackgroundColor(Color.parseColor("#62FA2E"))
                    setTextColor(Color.parseColor("#62FA2E"))
                    setTextSize(10f)
                    setRibbonRotation(45)
                }
                itemView.paynow.visibility = View.GONE
            } else {

                itemView.ribbonLayout01.ribbonHeader = ribbonView(context) {
                    setText("Acknowledged")
                    setRibbonBackgroundColor(Color.parseColor("#F8ED11"))
                    setTextColor(Color.parseColor("#F8ED11"))
                    setTextSize(10f)
                    setRibbonRotation(45)
                }
                if ("fund" == type && item.contributeUserId.toString() == (SharedPref.getUserRegistration().id)) {
                    itemView.paynow.visibility = View.VISIBLE
                }
            }
            itemView.itemsContributed.text = item.contributeItemQuantity.toString()
            if (item.formattedCreatedAt != null && item.formattedCreatedAt.isNotEmpty()) {
                itemView.contributedOn.text = (item.formattedCreatedAt)
                itemView.contributedOn.visibility = View.VISIBLE
            } else {
                itemView.contributedOn.visibility = View.GONE
            }

            itemView.paynow.setOnClickListener {
                listener.onPayNow(item)
            }
        }
    }
//CHECKSTYLE:ON

    interface OnClickListener {
        fun onPayNow(contributor: Contributor)
        fun onthankyouPost(contributor: Contributor)
    }


}