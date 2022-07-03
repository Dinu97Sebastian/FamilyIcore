package com.familheey.app.Activities

import android.Manifest
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.familheey.app.Adapters.ImageSliderAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_chat_full_view.*

class ChatImageFullView : AppCompatActivity() {

    private var imageUrls: ArrayList<String>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_full_view)
        imageUrls = intent.getStringArrayListExtra(Constants.Bundle.DATA)
        initializeToolbar()
        initAdapter()

        imagedownload.setOnClickListener {

            if (isReadWritePermissionGranted()) {
                val url = imageUrls?.get(imageSlider.currentPagePosition)
                val s = Constants.ApiPaths.IMAGE_BASE_URL + "file_name/"
                Toast.makeText(this, "Downloading media...", Toast.LENGTH_SHORT).show()
                if (url != null) {
                    Utilities.downloadDocuments(this, url, url.substring(s.length, url.length))
                }
            } else {
                showPermission()
            }
        }
    }

    private fun initializeToolbar() {
        toolBarTitle.text = "Photos"
        goBack.setOnClickListener { finish() }
    }

    private fun initAdapter() {
        val imageSliderAdapter = ImageSliderAdapter(this, imageUrls)
        imageSlider.sliderAdapter = imageSliderAdapter
        imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.indicatorSelectedColor = Color.GREEN
        imageSlider.indicatorUnselectedColor = Color.GRAY
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        return if (TedPermission.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) true else {
            requestPermission()
            false
        }
    }


    private fun isReadWritePermissionGranted(): Boolean {
        return TedPermission.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    //CHECKSTYLE:OFF
    private fun requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        imagedownload.performClick()
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {
                        /*
                Don't need this
               */
                    }
                })
                .setDeniedMessage("If you reject permission,you can not download images\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
    }

    //CHECKSTYLE:ON
    private fun showPermission() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialogue_permission)
        val window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.btn_continue).setOnClickListener {
            dialog.dismiss()
            isReadStoragePermissionGranted()
        }
        dialog.show()
    }
}