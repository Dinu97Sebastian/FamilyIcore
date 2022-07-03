package com.familheey.app.PaymentHistory

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_payment_history.*
import kotlinx.android.synthetic.main.bottom_sheet_pyment_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class PaymentHistoryActivity : AppCompatActivity(), AllMembershipPaymentHistoryFragment.OnHeadlineSelectedListener {
    var familyId: String? = ""
    var userId: String? = ""
    var name: String? = ""
    private var fromYear = 0
    private var fromDay = 0
    private var fromMonth = 0
    private var toYear = 0
    private var toDay = 0
    private var toMonth = 0
    private var eventTabAdapter: EventTabAdapter? = null
    private var contributedSheetBehaviour: BottomSheetBehavior<FrameLayout>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)
        setDate()
        familyId = intent.getStringExtra(Constants.Bundle.FAMILY_ID)
        userId = intent?.getStringExtra(Constants.Bundle.ID)
        name = intent?.getStringExtra("NAME")
        if (!name.isNullOrBlank()) {
            toolBarTitle.text = "$name Payments"
        }
        imgSearch.visibility = View.GONE


        eventTabAdapter = EventTabAdapter(supportFragmentManager)

        val f = MembershipPaymentHistoryFragment.newInstance(txt_from.text.toString().trim(), txt_to.text.toString().trim(), familyId, userId)
        f.setOnHeadlineSelectedListener(this)
        eventTabAdapter?.addFragment(f, "MEMBERSHIP")


        val f1 = FundRequestPaymentHistoryFragment.newInstance(txt_from.text.toString().trim(), txt_to.text.toString().trim(), familyId, userId)
        f1.setOnHeadlineSelectedListener(this)
        eventTabAdapter?.addFragment(f1, "FUND REQUEST")

        view_pager.adapter = eventTabAdapter


        tabs.setupWithViewPager(view_pager)
        btn_back.setOnClickListener { onBackPressed() }

        fromdate.setOnClickListener {
            date(txt_from, true)
        }

        todate.setOnClickListener {
            date(txt_to, false)
        }
        initializeBottomSheets()
    }


    private fun date(editText: TextView, boolean: Boolean) {
        val c = Calendar.getInstance()

        val mYear: Int
        val mMonth: Int
        val mDay: Int
        if (boolean) {
            mYear = fromYear
            mMonth = fromMonth
            mDay = fromDay
        } else {
            mYear = toYear
            mMonth = toMonth
            mDay = toDay
        }
        val datePickerDialog = DatePickerDialog(this,
                { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->

                    c.set(year, monthOfYear, dayOfMonth)
                    if (boolean) {

                        fromYear = c[Calendar.YEAR]
                        fromDay = c[Calendar.DAY_OF_MONTH]
                        fromMonth = c[Calendar.MONTH]
                    } else {

                        toDay = c[Calendar.DAY_OF_MONTH]
                        toMonth = c[Calendar.MONTH]
                        toYear = c[Calendar.YEAR]
                    }
                    editText.text = getFormattedDate(c.timeInMillis)
                    onSearchQueryListener()
                }, mYear, mMonth, mDay)
        //   datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show()
    }

    private fun setDate() {
        val c = Calendar.getInstance()
        txt_to.text = getFormattedDate(c.timeInMillis)


        toDay = c[Calendar.DAY_OF_MONTH]
        toMonth = c[Calendar.MONTH]
        toYear = c[Calendar.YEAR]

        c.add(Calendar.DAY_OF_YEAR, -60)

        fromYear = c[Calendar.YEAR]
        fromDay = c[Calendar.DAY_OF_MONTH]
        fromMonth = c[Calendar.MONTH]
        txt_from.text = getFormattedDate(c.timeInMillis)
    }


    fun getFormattedDate(calendarTime: Long): String? {
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarTime
        return formatter.format(calendar.time)
    }

    private fun onSearchQueryListener() {
        val fragment1 = eventTabAdapter!!.getRegisteredFragment(0) as MembershipPaymentHistoryFragment
        fragment1.onSearch(txt_from.text.toString().trim(), txt_to.text.toString().trim())

        val fragment2 = eventTabAdapter!!.getRegisteredFragment(1) as FundRequestPaymentHistoryFragment
        fragment2.onSearch(txt_from.text.toString().trim(), txt_to.text.toString().trim())

    }

    override fun onArticleSelected(data: Data?) {
        showContributedSheet(data)
    }

    private fun initializeBottomSheets() {
        contributedSheetBehaviour = BottomSheetBehavior.from(payment_note)
        contributedSheetBehaviour?.peekHeight = 0

        contributedSheetBehaviour?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) bg.visibility = View.GONE
                else bg.visibility = View.VISIBLE
            }

            //CHECKSTYLE:OFF
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                /*
                Not need
                 */
            }
        })
        //CHECKSTYLE:ON
    }


    private fun showContributedSheet(data: Data?) {

        if (data?.membershipCustomerNotes.isNullOrEmpty()) {
            payment_note.ti_membership_payment_notes.visibility = View.GONE
        } else {

            payment_note.ti_membership_payment_notes.visibility = View.VISIBLE
            payment_note.etxt_membership_payment_notes.setText(data?.membershipCustomerNotes)
        }

        if (data?.membershipPaymentNotes.isNullOrEmpty()) {
            payment_note.ti_membership_customer_notes.visibility = View.GONE
        } else {
            payment_note.ti_membership_customer_notes.visibility = View.VISIBLE
            payment_note.etxt_membership_customer_notes.setText(data?.membershipPaymentNotes)
        }

        payment_note.btn_skip.setOnClickListener {
            contributedSheetBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
            payment_note.visibility = View.GONE
            bg.visibility = View.GONE
        }

        if (contributedSheetBehaviour!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            contributedSheetBehaviour!!.state = BottomSheetBehavior.STATE_EXPANDED
            payment_note.visibility = View.VISIBLE
            bg.visibility = View.VISIBLE
        } else {
            contributedSheetBehaviour!!.state = BottomSheetBehavior.STATE_COLLAPSED
            payment_note.visibility = View.GONE
            bg.visibility = View.GONE
        }

        if (data?.membershipCustomerNotes.isNullOrEmpty() && data?.membershipPaymentNotes.isNullOrEmpty() && !data?.paymentNote.isNullOrEmpty()) {
            payment_note.ti_membership_payment_notes.visibility = View.VISIBLE
            payment_note.etxt_membership_payment_notes.setText(data?.paymentNote)

        }
    }
}