package com.familheey.app.PaymentHistory

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.familheey.app.Adapters.EventTabAdapter
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Utilities
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_payment_history.*
import kotlinx.android.synthetic.main.bottom_sheet_pyment_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class AllPaymentHistoryActivity : AppCompatActivity(), AllMembershipPaymentHistoryFragment.OnHeadlineSelectedListener {
    private var familyId: String? = ""
    private var name: String? = ""
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
        name = intent?.getStringExtra("NAME")
        if (!name.isNullOrBlank()) {
            toolBarTitle.text = "$name Payments"
        }
        eventTabAdapter = EventTabAdapter(supportFragmentManager)
        val f = AllMembershipPaymentHistoryFragment.newInstance(txt_from.text.toString().trim(), txt_to.text.toString().trim(), familyId)
        f.setOnHeadlineSelectedListener(this)
        eventTabAdapter?.addFragment(f, "MEMBERSHIP")


        val f1 = AllMembersFundRequestPaymentHistoryFragment.newInstance(txt_from.text.toString().trim(), txt_to.text.toString().trim(), familyId)
        f1.setOnHeadlineSelectedListener(this)
        eventTabAdapter?.addFragment(f1, "FUND REQUEST")

        view_pager.adapter = eventTabAdapter
        tabs.setupWithViewPager(view_pager)

        initializeSearchClearCallback()
        btn_back.setOnClickListener { onBackPressed() }
        imgSearch.setOnClickListener {
            Utilities.showCircularReveal(constraintSearch)
            showKeyboard()
        }
        imageBack.setOnClickListener {
            Utilities.hideCircularReveal(constraintSearch)
            search_post.setText("")
            search_post.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
        }

        fromdate.setOnClickListener {
            date(txt_from, true)
        }

        todate.setOnClickListener {
            date(txt_to, false)
        }
        initializeBottomSheets()
    }


    private fun showKeyboard() {
        if (search_post.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(search_post, InputMethodManager.SHOW_IMPLICIT)
        }


    }

    //CHECKSTYLE:OFF
    private fun initializeSearchClearCallback() {
        search_post.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /*
                Not needed
                 */
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /*
                Not needed
                 */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) clearSearch.visibility = View.INVISIBLE else clearSearch.visibility = View.VISIBLE
            }
        })
        clearSearch.setOnClickListener {
            search_post.setText("")
            onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH)

        }

        search_post.setOnEditorActionListener { _, actionId, event ->
            onSearchQueryListener(actionId)
        }
    }

    //CHECKSTYLE:ON
    protected fun onSearchQueryListener(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val fragment1 = eventTabAdapter!!.getRegisteredFragment(0) as AllMembershipPaymentHistoryFragment
            fragment1.onSearch(search_post.text.toString(), txt_from.text.toString().trim(), txt_to.text.toString().trim())


            val fragment2 = eventTabAdapter!!.getRegisteredFragment(1) as AllMembersFundRequestPaymentHistoryFragment
            fragment2.onSearch(search_post.text.toString(), txt_from.text.toString().trim(), txt_to.text.toString().trim())

            try {
                val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(search_post.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
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
                { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->

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
                    onSearchQueryListener(EditorInfo.IME_ACTION_SEARCH)
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
                Not needed
                 */
            }
        })
    }
    //CHECKSTYLE:ON

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

        if (!data?.paymentNote.isNullOrEmpty() && data?.membershipCustomerNotes.isNullOrEmpty() && data?.membershipPaymentNotes.isNullOrEmpty()) {
            payment_note.ti_membership_payment_notes.visibility = View.VISIBLE
            payment_note.etxt_membership_payment_notes.setText(data?.paymentNote)
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
    }
}