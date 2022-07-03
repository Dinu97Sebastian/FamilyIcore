package com.familheey.app.Notification

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.core.widget.ContentLoadingProgressBar
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.familheey.app.Adapters.MyFamilyLisitingNotificationAdapter
import com.familheey.app.Discover.model.DiscoverGroups
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.SharedPref
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialogue_my_family.*
import kotlinx.android.synthetic.main.settings_activity.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NotificationSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_back.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left,
                R.anim.right)
    }

    class SettingsFragment : PreferenceFragmentCompat(), MyFamilyLisitingNotificationAdapter.MyfamilyItemClick {
        private var myfamily: ArrayList<DiscoverGroups> = ArrayList()
        private var selectedfamilyId: ArrayList<Int> = ArrayList()
        private var allFamilyIds: ArrayList<Int> = ArrayList()
        private var adapter: MyFamilyLisitingNotificationAdapter? = null
        private var publicNotification: SwitchPreferenceCompat? = null
        private var conversationNotification: SwitchPreferenceCompat? = null
        private var completeNotification: SwitchPreferenceCompat? = null
        private var selectedFamiliesNotification: SwitchPreferenceCompat? = null
        private var announcementNotification: SwitchPreferenceCompat? = null
        private var eventNotification: SwitchPreferenceCompat? = null
        private var compositeDisposable = CompositeDisposable()
        private var familyCount: TextView? = null
        private var myfamilyCount: Int? = 0
        private lateinit var dialog:Dialog
        private val conversion = "application/json; charset=utf-8"
        private var btnDone: androidx.appcompat.widget.AppCompatButton? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            getProfileDetails()
            publicNotification = findPreference("sync")
            conversationNotification = findPreference("sync1")
            completeNotification = findPreference("sync2")
            selectedFamiliesNotification = findPreference("sync3")
            if (!SharedPref.userHasFamily()) {
                selectedFamiliesNotification?.isVisible = false
            }
            announcementNotification = findPreference("sync4")
            eventNotification = findPreference("sync5")
            publicNotification?.setOnPreferenceChangeListener { _, newValue ->

                val jsonObject = JsonObject()
                if (newValue == true) {
                    jsonObject.addProperty("public_notification", true)
                } else {
                    jsonObject.addProperty("public_notification", false)
                }
                updateSettings(jsonObject)
                true
            }

            conversationNotification?.setOnPreferenceChangeListener { _, newValue ->
                val jsonObject = JsonObject()
                if (newValue == true) {
                    jsonObject.addProperty("conversation_notification", true)
                } else {
                    jsonObject.addProperty("conversation_notification", false)
                }
                updateSettings(jsonObject)
                true
            }
            completeNotification?.setOnPreferenceChangeListener { _, newValue ->
                val jsonObject = JsonObject()
                if (newValue == true) {
                    jsonObject.addProperty("notification", true)
                } else {
                    jsonObject.addProperty("notification", false)
                }
                updateSettings(jsonObject)
                true
            }
            selectedFamiliesNotification?.setOnPreferenceChangeListener { _, newValue ->
                familySelectionDialogue()
                true
            }
            announcementNotification?.setOnPreferenceChangeListener { _, newValue ->
                val jsonObject = JsonObject()
                if (newValue == true) {
                    jsonObject.addProperty("announcement_notification", true)
                } else {
                    jsonObject.addProperty("announcement_notification", false)
                }
                updateSettings(jsonObject)

                true
            }
            eventNotification?.setOnPreferenceChangeListener { _, newValue ->
                val jsonObject = JsonObject()
                if (newValue == true) {
                    jsonObject.addProperty("event_notification", true)
                } else {
                    jsonObject.addProperty("event_notification", false)
                }
                updateSettings(jsonObject)
                true
            }

        }

        override fun onItemCheckBoxClick(position: Int) {

            myfamily[position].selected = !myfamily[position].selected
            adapter?.notifyDataSetChanged()

            var count = 0
            for (member in myfamily) {
                if (member.selected) {
                    count += 1
                }

            }
            familyCount?.text = "$count Selected"
        }

        private fun getProfileDetails() {
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
            val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
            val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
            compositeDisposable.add(
                    requestInterface.getNotificationSettings(requestBody).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                allFamilyIds.clear()
                                allFamilyIds.addAll(it.body()?.get(0)?.familyIds!!)
                                eventNotification?.isChecked = it.body()?.get(0)?.eventNotification!!
                                publicNotification?.isChecked = it.body()?.get(0)?.publicNotification!!
                                conversationNotification?.isChecked = it.body()?.get(0)?.conversationNotification!!
                                completeNotification?.isChecked = it.body()?.get(0)?.notification!!
                                announcementNotification?.isChecked = it.body()?.get(0)?.announcementNotification!!
                                selectedfamilyId.clear()
                                myfamilyCount = it.body()?.get(0)?.familyCount
                                selectedFamiliesNotification?.isChecked = it.body()?.get(0)?.familyCount != it.body()?.get(0)?.familyNotificationOff?.size
                                selectedfamilyId.addAll(it.body()?.get(0)?.familyNotificationOff!!)
                            }, {
                            })
            )
        }

        private fun updateSettings(jsonObject: JsonObject) {
            jsonObject.addProperty("id", SharedPref.getUserRegistration().id)
            val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
            compositeDisposable.add(
                    requestInterface.updateSettings(requestBody).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                selectedfamilyId.clear()
                                selectedFamiliesNotification?.isChecked = myfamilyCount != it.body()?.familyNotificationOff?.size
                                selectedfamilyId.addAll(it?.body()?.familyNotificationOff!!)
                            }, {
                            })
            )
        }

        private fun familySelectionDialogue() {

             dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.dialogue_my_family)
            val window = dialog.window!!
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawableResource(android.R.color.transparent)
            btnDone = dialog.btn_done
            familyCount = dialog.txt_count
            adapter = MyFamilyLisitingNotificationAdapter(myfamily, this)
            dialog.rvfamilylist.adapter = adapter

            val linearLayoutManager = LinearLayoutManager(activity)
            dialog.rvfamilylist.layoutManager = linearLayoutManager
            dialog.rvfamilylist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView,
                                        dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == myfamily.size - 1)
                    {
                          //dialog.loadMore.show()
                          //getMyFamilies(dialog)
                    }
                }
            })
            getMyFamilies(dialog)
            dialog.txt_selection.setOnClickListener {
                if (dialog.txt_selection.text.equals("Select All")) {
                    selectAll()
                    dialog.txt_selection.text = "Deselect All"
                    dialog.txt_selection.setTextColor(Color.parseColor("#4BAE50"))
                } else {
                    deselectAll()
                    dialog.txt_selection.text = "Select All"
                    dialog.txt_selection.setTextColor(Color.parseColor("#000000"))
                }
            }
            dialog.btn_close.setOnClickListener {

                selectedFamiliesNotification?.isChecked = myfamilyCount != selectedfamilyId.size
                unselect()
                dialog.dismiss()
            }
            dialog.btn_done.setOnClickListener {

                createJsonForSelectedFamily()
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun getMyFamilies(dialog: Dialog) {
            if(myfamily.size==0)
                dialog.prograss.show()
            val jsonObject = JsonObject()
            jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
            jsonObject.addProperty("offset", myfamily.size.toString())
            //allFamilyIds.size
            jsonObject.addProperty("limit", myfamilyCount)
            val requestBody: RequestBody = jsonObject.toString().toRequestBody(conversion.toMediaTypeOrNull())
            val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
            compositeDisposable.add(
                    requestInterface.getMyFamilies(requestBody).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
//                                myfamily.clear()
                                myfamily.addAll(it.body()?.data!!)
                                btnDone?.visibility = View.VISIBLE
                                setSelections()
                                dialog.prograss.hide()
                                dialog.loadMore.hide()
                            }, {
                            })
            )
        }

        private fun setSelections() {
            if (selectedfamilyId.size > 0) {
                for (member in myfamily) {
                    member.selected = true
                }
                for (id in selectedfamilyId) {
                    for (member in myfamily) {
                        if (member.id == id) {
                            member.selected = false
                            break
                        }
                    }
                }
            } else {
                for (member in myfamily) {
                    member.selected = true
                }
            }
            adapter?.notifyDataSetChanged()
        }

        private fun unselect() {
            for (member in myfamily) {
                member.selected = false
            }
        }

        private fun selectAll() {
            for (member in myfamily) {
                member.selected = true
            }
            adapter?.notifyDataSetChanged()
            familyCount?.text = "$myfamilyCount Selected"
        }

        private fun deselectAll() {
            for (member in myfamily) {
                member.selected = false
            }
            adapter?.notifyDataSetChanged()
            familyCount?.text = ""
        }

        private fun createJsonForSelectedFamily() {
            val ar: ArrayList<Int>? = ArrayList()
               for (selected in myfamily) {
                   if (!selected.selected) {
                           ar?.add(selected.id)
                   }
               }
            val jsonObject = JsonObject()
            val gson = GsonBuilder().create()
            val myCustomArray = gson.toJsonTree(ar).asJsonArray
            jsonObject.add("family_notification_off", myCustomArray)
            updateSettings(jsonObject)
            unselect()
        }

        override fun onResume() {
            super.onResume()
        }
    }
}