package com.familheey.app.Fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.familheey.app.Adapters.MemberLisitingAdapter
import com.familheey.app.Adapters.MemberSelectedAdapter
import com.familheey.app.Discover.model.DiscoverUsers
import com.familheey.app.Models.Request.AddMember
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialogue_user_profile.*
import kotlinx.android.synthetic.main.listmember_main.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class MemberAddToGroupFragment : Fragment(), MemberLisitingAdapter.MemberItemClick, MemberSelectedAdapter.MemberSelectedItemClick {

    private var progressDialog: SweetAlertDialog? = null
    private var compositeDisposable = CompositeDisposable()
    private var groupId: String? = ""
    private var adapter: MemberLisitingAdapter? = null
    private var selectedMemberAdapter: MemberSelectedAdapter? = null
    private var members: ArrayList<DiscoverUsers> = ArrayList()
    private var selectedMembers: ArrayList<DiscoverUsers> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            groupId = requireArguments().getString(Constants.Bundle.FAMILY_ID)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.listmember_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerviews()
        initializeSearchClearCallback()
        getMembers()
    }

    override fun onItemCheckBoxClick(position: Int) {
        if (members[position].selected) {
            selectedMembers.remove(members[position])
            if (selectedMembers.size == 0) {
                member_list.visibility = View.GONE
                btn_invite.visibility = View.GONE
            }
        } else {
            btn_invite.visibility = View.VISIBLE
            member_list.visibility = View.VISIBLE
            if (selectedMembers.indexOf(members[position]) < 0) {
                selectedMembers.add(members[position])
            }
        }
        members[position].selected = !members[position].selected
        selectedMemberAdapter?.notifyDataSetChanged()
        adapter?.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        val item: DiscoverUsers? = members[position]

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialogue_user_profile)
        val window = dialog.window!!
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.prograass.show()
        getProfileDetails(item?.id.toString(), dialog)
        dialog.name.text = item?.fullName
        dialog.txt_location.text = item?.location
        dialog.btn_close.setOnClickListener {
            dialog.dismiss()
        }
        if (item?.propic != null) {
            Glide.with(requireActivity())
                    .load(Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE + Constants.ApiPaths.IMAGE_BASE_URL + Constants.Paths.PROFILE_PIC + item.propic)
                    .apply(Utilities.getCurvedRequestOptionsSmall())
                    .placeholder(R.drawable.avatar_male)
                    .transition(DrawableTransitionOptions.withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(dialog.userProfileImage)
        } else {
            Glide.with(requireActivity())
                    .load(R.drawable.avatar_male)
                    .into(dialog.userProfileImage)
        }

        dialog.show()
    }

    override fun onItemCloseClick(position: Int) {
        val pos = members.indexOf(selectedMembers[position])
        if (pos >= 0) {
            members[pos].selected = false
            adapter?.notifyDataSetChanged()
        }

        selectedMembers.remove(selectedMembers[position])
        selectedMemberAdapter?.notifyDataSetChanged()
        if (selectedMembers.size == 0) {
            member_list.visibility = View.GONE
            btn_invite.visibility = View.GONE
        }
    }

    fun showProgressDialog() {
        progressDialog = Utilities.getProgressDialog(activity)
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        if (progressDialog != null) progressDialog!!.dismiss()
    }

    private fun initRecyclerviews() {
        adapter = MemberLisitingAdapter(members, this)
        list_recyclerView.adapter = adapter
        list_recyclerView.layoutManager = LinearLayoutManager(activity)

        selectedMemberAdapter = MemberSelectedAdapter(selectedMembers, this)
        member_list.adapter = selectedMemberAdapter
        member_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    }


    private fun initializeSearchClearCallback() {
        searchMembers.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchQueryListener()
                true
            } else {
                false
            }
        }
    //CHECKSTYLE:OFF
        searchMembers.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                /*
                Don't need this
               */
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /*
                Don't need this
               */
            }

            //CHECKSTYLE:ON
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) clearSearch.visibility = View.INVISIBLE else clearSearch.visibility = View.VISIBLE
            }
        })
        clearSearch.setOnClickListener {
            searchMembers.setText("")
            onSearchQueryListener()
        }

        btn_invite.setOnClickListener {
            addToGroup()
        }
    }

    private fun onSearchQueryListener(): Boolean {
        getMembers()
        return true
    }

    private fun setSelections() {
        for (selectedMember in selectedMembers) {
            for (member in members) {
                if (member.id == selectedMember.id) {
                    member.selected = true
                    break
                }
            }
        }
        adapter?.notifyDataSetChanged()
    }


    private fun getMembers() {
        val jsonObject = JsonObject()
        progressListMember.show()
        jsonObject.addProperty("searchtxt", searchMembers.text.toString())
        jsonObject.addProperty("userid", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("offset", "0")
        jsonObject.addProperty("type", "users")
        jsonObject.addProperty("group_id", groupId)
        jsonObject.addProperty("limit", "500")
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.searchData(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            members.clear()
                            members.addAll(it.body()?.users!!)
                            setSelections()
                            progressListMember.hide()
                        }, {
                            try {
                                progressListMember.hide()
                            } catch (ex: Exception) {
                                /*
                Don't need this
               */
                            }
                        })
        )
    }

    private fun getProfileDetails(id: String, dialog: Dialog) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("profile_id", id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.viewUserProfile(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            dialog.txt_work.text = it.body()?.profile?.work
                            dialog.txt_introduction.text = it.body()?.profile?.about
                            dialog.familiesCount.text = it.body()?.count?.familyCount.toString()
                            dialog.connectionsCount.text = it.body()?.count?.connections.toString()
                            dialog.mutualCount.text = it.body()?.count?.mutualConnections.toString()
                            dialog.prograass.hide()
                        }, {
                        })
        )
    }


    private fun addToGroup() {
        showProgressDialog()
        val req = AddMember()
        val userId: ArrayList<Int>? = ArrayList()
        req.fromId = SharedPref.getUserRegistration().id
        req.groupId = groupId.toString()
        for (member in selectedMembers) {
            userId?.add(member.id)
        }
        req.userId = userId
        val requestInterface = RetrofitUtil.createRxResource(activity?.applicationContext!!, RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.addToFamily(req).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            hideProgressDialog()
                            selectedMembers.clear()
                            selectedMemberAdapter?.notifyDataSetChanged()
                            member_list.visibility = View.GONE
                            btn_invite.visibility = View.GONE
                            //07-09-21
                            Toast.makeText(requireActivity(),"Invitation has been sent",Toast.LENGTH_SHORT).show();
                            getMembers()
                        }, {
                            hideProgressDialog()
                        })
        )
    }


    companion object {
        @JvmStatic
        fun newInstance(familyId: String?): MemberAddToGroupFragment {
            return MemberAddToGroupFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.Bundle.FAMILY_ID, familyId)

                }
            }
        }
    }
}