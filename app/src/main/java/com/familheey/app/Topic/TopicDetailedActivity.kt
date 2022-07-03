package com.familheey.app.Topic

import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Fragments.Posts.PostData
import com.familheey.app.Fragments.Posts.PostSliderAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.item_toolbar_normal_no_notif.*
import kotlinx.android.synthetic.main.item_topics.*
import kotlin.math.roundToInt

class TopicDetailedActivity : AppCompatActivity() {

    private lateinit var topic: Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detailed)
        initializeToolbar()
        fillDatas()
    }

    private fun fillDatas() {
        userName.text = topic.createdUser
        postedTimeAgo.text = topic.createdAt
        description.text = topic.description
        conversationCount.text = topic.title
        Glide.with(applicationContext)
                .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.LOGO + topic.title)
                .apply(Utilities.getCurvedRequestOptions())
                .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .placeholder(R.drawable.family_logo)
                .into(userImage)
        description.text = topic.description ?: ""
        addViewMoreLogic(topic.description ?: "", description, tempDescription, readMoreLess)
        //ImageSlider
        if (topic.topicAttachment?.size!! > 0) {
            imageSlider.visibility = View.VISIBLE
            if (topic.topicAttachment?.get(0)?.type?.contains("image")!!) {
                if (topic.topicAttachment?.get(0)?.width != null && topic.topicAttachment?.get(0)?.height != null) {
                    val params: ViewGroup.LayoutParams = imageSlider.layoutParams
                    params.height = getwidgetsize(topic.topicAttachment?.get(0)?.width!!, topic.topicAttachment?.get(0)?.height!!)
                    topic.topicAttachment?.get(0)?.height1 = params.height.toString() + ""
                    imageSlider.layoutParams = params
                } else {
                    val params: ViewGroup.LayoutParams = imageSlider.layoutParams
                    params.height = getwidgetsize()
                    imageSlider.layoutParams = params
                }
            } else if (topic.topicAttachment?.get(0)?.type?.contains("video")!!) {
                val params: ViewGroup.LayoutParams = imageSlider.layoutParams
                params.height = 850
                imageSlider.layoutParams = params
            } else {
                val params: ViewGroup.LayoutParams = imageSlider.layoutParams
                params.height = 600
                imageSlider.layoutParams = params
            }
            val adapter = PostSliderAdapter(applicationContext, PostData())
            imageSlider.sliderAdapter = adapter
            imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM)
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        } else {
            imageSlider.visibility = View.GONE
        }
    }

    private fun getwidgetsize(): Int {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val s = screenWidth / 4
        return (s * 3).roundToInt()
    }

    private fun getwidgetsize(width: String, hight: String): Int {
        val screenWidth: Float
        val wf = width.toFloat()
        val hf = hight.toFloat()
        screenWidth = if (wf > 450) {
            Resources.getSystem().displayMetrics.widthPixels + 40.toFloat()
        } else {
            Resources.getSystem().displayMetrics.widthPixels.toFloat()
        }
        return (screenWidth / wf * hf).roundToInt()
    }

    private fun addViewMoreLogic(description: String, albumDescription: TextView, textTemp: TextView, txtLessOrMore: TextView) {
        albumDescription.text = description
        textTemp.text = description
        textTemp.post {
            if (textTemp.lineCount > 2) {
                txtLessOrMore.visibility = View.VISIBLE
                albumDescription.maxLines = 2
                albumDescription.ellipsize = TextUtils.TruncateAt.END
                albumDescription.text = description
            } else {
                txtLessOrMore.visibility = View.GONE
                albumDescription.text = description
            }
        }
        albumDescription.setOnClickListener { }
        textTemp.setOnClickListener { }
        txtLessOrMore.setOnClickListener {
            if (txtLessOrMore.text == "Read More") {
                txtLessOrMore.text = "Read Less"
                albumDescription.text = description
                albumDescription.maxLines = Int.MAX_VALUE
                albumDescription.ellipsize = null
            } else {
                txtLessOrMore.text = "Read More"
                albumDescription.maxLines = 2
                albumDescription.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }

    private fun initializeToolbar() {
        toolBarTitle.text = "Topic Detail"
        goBack.setOnClickListener {
            onBackPressed()
        }
    }
}
