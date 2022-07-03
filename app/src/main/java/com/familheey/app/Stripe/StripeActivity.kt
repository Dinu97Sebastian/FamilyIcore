package com.familheey.app.Stripe

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.familheey.app.FamilheeyApplication
import com.familheey.app.Models.Response.Keys
import com.familheey.app.Networking.Retrofit.ApiServices
import com.familheey.app.Networking.Retrofit.RetrofitBase
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.StripeIntent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_stripe.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.lang.ref.WeakReference


class StripeActivity : AppCompatActivity(), CardAdapter.CardItemClick {
    val compositeDisposable = CompositeDisposable()
    private var paymentIntentClientSecret = ""
    var url = ""
    private var pAmount: String? = ""

    private var type: String? = ""
    private var groupId: String? = ""
    private var requestId: String? = ""
    private var itemId: String? = ""
    private var cardId = ""
    private var accountNumber: String? = ""
    private var contributionId: String? = ""
    private lateinit var stripe: Stripe

    private var progressDialog: SweetAlertDialog? = null
    private var data: MutableList<Card> = mutableListOf()
    private val conversion = "application/json; charset=utf-8"

    //CHECKSTYLE:OFF
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe)
        cardInputWidget.postalCodeRequired = false
        initStripe()
        initUi()
        payButton.setOnClickListener {
            if (rbonline.isChecked) {
                val params = cardInputWidget.paymentMethodCreateParams
                if (params != null) {
                    showProgressDialog()
                    val confirmParams = ConfirmPaymentIntentParams
                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret, null)
                    stripe.confirmPayment(this, confirmParams)
                }
            } else {
                if ("request" == type) {
                    contribute(pAmount)
                } else {
                    memberShipOfflinePay()
                }

            }
        }
        //CHECKSTYLE:ON

        rbonline.setOnClickListener {
            if (rbonline.isChecked) {
                carddetailview.visibility = View.VISIBLE
                offline_view.visibility = View.GONE
            }
        }

        rboffline.setOnClickListener {
            if (rboffline.isChecked) {
                carddetailview.visibility = View.GONE
                offline_view.visibility = View.VISIBLE
                rb_cash.isChecked = true
            }
        }

        goBack.setOnClickListener {
            onBackPressed()
        }

        newcardselection.setOnClickListener {
            if (carddetailview.visibility == View.VISIBLE) {
                carddetailview.visibility = View.GONE
                newcardselection.isChecked = false
            } else {
                carddetailview.visibility = View.VISIBLE
                newcardselection.isChecked = true
            }
            for (obj in data) {
                obj.isSelected = false
            }
            recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(position: Int) {
        carddetailview.visibility = View.GONE
        newcardselection.isChecked = false
        for (obj in data) {
            obj.isSelected = false
        }
        data[position].isSelected = true
        cardId = data[position].id
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun onItemDelete(position: Int) {

    }


    private fun getStripeAddPayment() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("amount", pAmount!!.toFloat() * 100)
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("to_type", type)

        jsonObject.addProperty("is_anonymous", intent.extras?.getBoolean("ANONYMOUS"))
        if ("request" != type) {
            jsonObject.addProperty("to_subtype", "")
            jsonObject.addProperty("to_subtype_id", "")
        } else {
            jsonObject.addProperty("to_subtype", "item")
            jsonObject.addProperty("to_subtype_id", itemId)
        }
        jsonObject.addProperty("to_type_id", requestId)
        if (contributionId != null && contributionId!!.isNotEmpty()) {
            jsonObject.addProperty("contributionId", contributionId)
        }

        val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitUtil.createRxResource(this, RestApiService::class.java).getstripeCreatePaymentIntent(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            paymentIntentClientSecret = it.body()?.get("client_secret")?.asString.toString()
                        }, { error ->
                            error.printStackTrace()
                        })
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val weakActivity = WeakReference<Activity>(this)
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                hideProgressDialog()
                val paymentIntent = result.intent
                val status = paymentIntent.status
                if (status == StripeIntent.Status.Succeeded) {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                } else {
                    displayAlert(weakActivity.get(), paymentIntent.lastPaymentError?.message
                            ?: "")
                }
            }

            override fun onError(e: Exception) {
                hideProgressDialog()
                displayAlert(weakActivity.get(), e.toString())
            }
        })
    }

    private fun displayAlert(activity: Activity?, message: String) {
        if (activity == null) {
            return
        }
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Payment failed")
            builder.setMessage(message)
            builder.setPositiveButton("Ok") { _, _ ->
                finish()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(this)
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        if (progressDialog != null) progressDialog?.dismiss()
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        try {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return super.dispatchTouchEvent(event)
    }


    private fun initStripe() {
        runOnUiThread {
            val key = SharedPref.read(SharedPref.STRIPE_KEY, "")
            if (key != "") {
                stripe = Stripe(this, key)
            } else getKeys()
        }
    }

    private fun initUi() {
        runOnUiThread {
            if (intent.extras != null) {
                pAmount = intent.extras?.getString("AMT")
                groupId = intent.extras?.getString("ID")
                itemId = intent.extras?.getString("ITEMID")
                requestId = intent.extras?.getString("RID")
                contributionId = intent.extras?.getString("CID")
                type = intent.extras?.getString("TYPE")
                Log.e("type", type + "")
                accountNumber = intent.extras?.getString("ACC")
                txtamount.text = "$pAmount"
            }
            if ("membership" == type) {
                if (accountNumber.isNullOrEmpty()) {
                    rbonline.visibility = View.GONE
                    rboffline.isChecked = true
                    carddetailview.visibility = View.GONE
                    offline_view.visibility = View.VISIBLE
                    rb_cash.isChecked = true
                } else {
                    checkBusinessAccountStatus()
                }
            } else {
                getStripeAddPayment()
            }
        }
    }

    private fun getKeys() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
        val application = FamilheeyApplication.get(applicationContext)
        val apiServices = RetrofitBase.createRxResource(applicationContext, ApiServices::class.java)
        compositeDisposable.add(apiServices.getKeys(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe({ response: Response<Keys?> ->
                    SharedPref.write(SharedPref.GOOGLE_API, response.body()!!.google_api_key)
                    SharedPref.write(SharedPref.STRIPE_KEY, response.body()!!.stripe)
                    initStripe()
                }) { })
    }

    private fun contribute(qty: String?) {
        val progressDialog = Utilities.getProgressDialog(this)
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("contribute_user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("post_request_item_id", itemId)
        jsonObject.addProperty("is_anonymous", intent.extras?.getBoolean("ANONYMOUS"))
        jsonObject.addProperty("contribute_item_quantity", qty)
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("payment_note", etxt_note.text.toString())
        if (contributionId != null && contributionId!!.isNotEmpty()) {
            jsonObject.addProperty("contributionId", contributionId)
        }
        var type = "Cash"
        if (rb_other.isChecked) type = "Other"
        else if (rb_cheque.isChecked) type = "Cheque"

        jsonObject.addProperty("payment_type", type)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
        val authService = RetrofitBase.createRxResource(applicationContext, ApiServices::class.java)
        compositeDisposable.add(authService.addContribution(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    progressDialog.dismissWithAnimation()
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG)
                })
    }


    private fun memberShipOfflinePay() {
        val progressDialog = Utilities.getProgressDialog(this)
        progressDialog.show()
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", requestId)
        jsonObject.addProperty("membership_customer_notes", etxt_note.text.toString())
        var type = "Cash"
        if (rb_other.isChecked) type = "Other"
        else if (rb_cheque.isChecked) type = "Cheque"
        jsonObject.addProperty("membership_payment_type", "OFFLINE")
        jsonObject.addProperty("membership_payment_method", type)
        jsonObject.addProperty("membership_payed_amount", pAmount)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
        val authService = RetrofitBase.createRxResource(applicationContext, ApiServices::class.java)
        compositeDisposable.add(authService.groupMapUpdate(requestBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    progressDialog.dismissWithAnimation()
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    Utilities.getErrorDialog(progressDialog, Constants.SOMETHING_WRONG)
                })
    }

    private fun checkBusinessAccountStatus() {
        showProgressDialog()
        val jsonObject = JsonObject()
        jsonObject.addProperty("group_id", groupId)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
        compositeDisposable.add(
                RetrofitBase.createRxResource(this, ApiServices::class.java).stripeGetaccountById(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({

                            if (it.body()?.get("payouts_enabled")?.asBoolean!! && it.body()?.get("charges_enabled")?.asBoolean!!) {
                                getStripeAddPayment()
                            } else {
                                rbonline.visibility = View.GONE
                                rboffline.isChecked = true
                                carddetailview.visibility = View.GONE
                                offline_view.visibility = View.VISIBLE
                                rb_cash.isChecked = true
                            }
                            hideProgressDialog()
                        }, {
                            hideProgressDialog()
                        })
        )
    }


}
