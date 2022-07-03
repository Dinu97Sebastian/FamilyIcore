package com.familheey.app.Need

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.R
import com.familheey.app.Stripe.StripeActivity
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_single_contributions_listing.*
import kotlinx.android.synthetic.main.item_toolbar_animated_search.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.util.*

open class SingleContrionsListing : AppCompatActivity(), SingleContributorAdapter.OnClickListener {
    private val STRIPE_REQUEST_CODE  = 1001
    private var singleContributorAdapter: SingleContributorAdapter? = null
    private var contributors: ArrayList<Contributor> = ArrayList()
    private var need: Item? = null
    private var needRequest: Need? = Need()
    private var id = ""
    private var userId = ""
    private var userName = ""
    private var type: String? = ""
    private var phone: String? = ""
    private var subscriptions = CompositeDisposable()
    private var isAdmin = false
    private var isAnonymus = false
    private var progressDialog: SweetAlertDialog? = null
    private var contributedSheetBehaviour: BottomSheetBehavior<FrameLayout>? = null
    private var thanksPostSheetBehaviour: BottomSheetBehavior<FrameLayout>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_contributions_listing)
        id = intent.getStringExtra(Constants.Bundle.ID)!!
        userId = intent.getStringExtra(Constants.Bundle.DATA)!!

        userName = intent.getStringExtra("NAME")!!
        type = intent.getStringExtra(Constants.Bundle.IDENTIFIER)
        isAdmin = intent.getBooleanExtra(Constants.Bundle.IS_ADMIN, false)
        isAnonymus = intent.getBooleanExtra(Constants.Bundle.IS_ANONYMOUS, false)

        need = intent.getParcelableExtra("NEED")
        needRequest = intent.getParcelableExtra(Constants.Bundle.DETAIL)
        initializeToolbar()
        getContributors(false)
        initializeBottomSheets()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == STRIPE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            contributors.clear()
            singleContributorAdapter?.notifyDataSetChanged()
            getContributors(true)
        }
    }

    override fun onPayNow(contributor: Contributor) {
        phone = contributor.phone
        startActivityForResult(Intent(applicationContext, StripeActivity::class.java)
                .putExtra("CID", contributor.id.toString())
                .putExtra("ITEMID", id)
                .putExtra("TYPE", "request")
                .putExtra("RID", intent.getStringExtra("RID"))
                .putExtra("ID", intent.getStringExtra(Constants.Bundle.FAMILY_ID))
                .putExtra("AMT", contributor.contributeItemQuantity.toString()), STRIPE_REQUEST_CODE)

    }

    override fun onthankyouPost(contributor: Contributor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setMessage("Do you want to acknowledge this contribution ?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            thankYouPostSkip(contributor)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }

        builder.show()

    }

    private fun initializeToolbar() {
        toolBarTitle.text = ""
        imgSearch.visibility = View.GONE
        goBack.setOnClickListener { onBackPressed() }
    }

    fun showShimmer() {
        emptyResultText.visibility = View.GONE
        if (shimmerLoader != null) {
            shimmerLoader.visibility = View.VISIBLE
            shimmerLoader.startShimmer()
        }
    }

    fun hideShimmer() {
        if (shimmerLoader != null) {
            shimmerLoader.stopShimmer()
            shimmerLoader.visibility = View.GONE
        }
    }

    private fun networkError(context: Context?) {
        if (context == null) return
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Uh oh! Check your internet connection and retry.")
                .setCancelable(false)
                .setPositiveButton("Retry") { _: DialogInterface?, _: Int -> getContributors(false) }.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    finish()
                    dialog.dismiss()
                }
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val alert = builder.create()
        alert.setTitle("Connection Unavailable")
        alert.show()
        params.setMargins(0, 0, 20, 0)
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).layoutParams = params
    }

    private fun getContributors(refresh: Boolean) {
        showShimmer()
        val jsonObject = JsonObject()

        jsonObject.addProperty("user_name", userName)
        jsonObject.addProperty("contribute_use_id", userId)
        jsonObject.addProperty("request_item_id", id)
        if (isAnonymus)
            jsonObject.addProperty("is_anonymous", "true")
        else
            jsonObject.addProperty("is_anonymous", "false")

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val authService = RetrofitBase.createRxResource(applicationContext, ApiServices::class.java)
        subscriptions.add(authService.contributionlistByUser(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    contributors = it.body() as ArrayList<Contributor>
                    singleContributorAdapter = SingleContributorAdapter(contributors, isAdmin, type, this, this)
                    contributorsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    contributorsList.adapter = singleContributorAdapter
                    hideShimmer()
                    if (refresh)
                        showContributedSheet()

                }) {
                    hideShimmer()
                    networkError(this)
                })
    }

    //CHECKSTYLE:OFF
    private fun initializeBottomSheets() {

        contributedSheetBehaviour = BottomSheetBehavior.from(contributedSheet)
        contributedSheetBehaviour?.peekHeight = 0

        thanksPostSheetBehaviour = BottomSheetBehavior.from(thanks_post)
        contributedSheetBehaviour?.peekHeight = 0



        contributedSheetBehaviour?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) bg.visibility = View.GONE
                else bg.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /*
                Not needed
                 */
            }
        })


        thanksPostSheetBehaviour!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState)
                    bg.visibility = View.GONE
                else bg.visibility = View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /*
                Not needed
                 */
            }
        })

    }

    //CHECKSTYLE:ON
    private fun showContributedSheet() {
        val successTick = contributedSheet.findViewById<ImageView>(R.id.successTick)
        successTick.setOnClickListener { view: View? -> contributedSheetBehaviour!!.setState(BottomSheetBehavior.STATE_COLLAPSED) }
        val callNow: MaterialButton = contributedSheet.findViewById(R.id.callNow)
        callNow.setOnClickListener { v: View? ->
            contributedSheetBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
            if (phone == null || phone.equals("")) {
                Toast.makeText(this, "Phone number not registered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            startActivity(intent)
        }
        if (contributedSheetBehaviour!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            contributedSheetBehaviour!!.setState(BottomSheetBehavior.STATE_EXPANDED)
        } else {
            contributedSheetBehaviour!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN && thanksPostSheetBehaviour!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            val outRect = Rect()
            thanks_post.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                thanksPostSheetBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                bg.visibility = View.GONE
            }
        }

        return super.dispatchTouchEvent(event)
    }

    open fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this)
        progressDialog?.show()
    }

    open fun hideProgressDialog() {
        if (progressDialog != null) progressDialog?.dismiss()
    }


    private fun thankYouPostSkip(selectedContribution: Contributor) {
        showProgressDialog()
        val jsonObject = JsonObject()

        jsonObject.addProperty("contribution_id", selectedContribution.id.toString() + "")
        jsonObject.addProperty("contribute_user_id", selectedContribution.contributeUserId.toString() + "")
        jsonObject.addProperty("request_item_id", selectedContribution.postRequestItemId.toString() + "")
        jsonObject.addProperty("skip_thank_post", true)

        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val application = FamilheeyApplication.get(this)
        val apiServices = RetrofitBase.createRxResource(this, ApiServices::class.java)
        subscriptions.add(apiServices.contributionStatusUpdation(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ response: Response<Void?>? ->
                    selectedContribution.isSkip_thank_post = true
                    selectedContribution.isIs_acknowledge = true
                    selectedContribution.isIs_thank_post = false
                    selectedContribution.isIs_pending_thank_post = false
                    singleContributorAdapter?.notifyDataSetChanged()
                    bg.visibility = View.GONE
                    if (thanksPostSheetBehaviour != null && thanksPostSheetBehaviour!!.state == BottomSheetBehavior.STATE_EXPANDED) thanksPostSheetBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
                    hideProgressDialog()
                }) { throwable: Throwable? -> hideProgressDialog() })
    }

}