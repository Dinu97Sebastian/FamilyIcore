package com.familheey.app.membership

import MembershipDashboard
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.familheey.app.Activities.FamilyDashboardActivity
import com.familheey.app.Fragments.FamilyDashboard.BackableFragment
import com.familheey.app.Interfaces.FamilyDashboardInteractor
import com.familheey.app.R
import com.familheey.app.Topic.RestApiService
import com.familheey.app.Topic.RetrofitUtil
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.FamilyDashboardIdentifiers
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.Utilities
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_membership.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class MembershipFragment : BackableFragment(), MemberDashboardAdapter.MemberDashboardItemClick {
    private val REQUEST_CODE  = 5001
    private var familyDashboardInteractor: FamilyDashboardInteractor? = null
    private var groupId: String = ""
    private var type: String = "All"
    private val black = "#000000"
    private val white = "#FFFFFF"
    private var compositeDisposable = CompositeDisposable()
    private var data: MembershipDashboard? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString(Constants.Bundle.ID).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_membership, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txt_all.setOnClickListener {

            if(type != "all") {
                type = "all"
                getDashBoard()
                txt_all.setTextColor(Color.parseColor(white))
                txt_active.setTextColor(Color.parseColor(black))
                txt_inactive.setTextColor(Color.parseColor(black))
                txt_all.setBackgroundResource(R.drawable.select_member_dashboard)
                txt_active.setBackgroundResource(R.drawable.nonselect_member_dashboard)
                txt_inactive.setBackgroundResource(R.drawable.nonselect_member_dashboard)
            }
        }
        txt_active.setOnClickListener {
            if (type != "current" && data?.membershipCounts?.get(0)?.activeCount!! > 0) {
                type = "current"
                getDashBoard()
                txt_all.setTextColor(Color.parseColor(black))
                txt_active.setTextColor(Color.parseColor(white))
                txt_inactive.setTextColor(Color.parseColor(black))
                txt_active.setBackgroundResource(R.drawable.select_member_dashboard)
                txt_all.setBackgroundResource(R.drawable.nonselect_member_dashboard)
                txt_inactive.setBackgroundResource(R.drawable.nonselect_member_dashboard)
            }
        }
        txt_inactive.setOnClickListener {
            if (type != "overdue" && data?.membershipCounts?.get(0)?.expairedCount!! > 0) {
                type = "overdue"
                getDashBoard()
                txt_all.setTextColor(Color.parseColor(black))
                txt_active.setTextColor(Color.parseColor(black))
                txt_inactive.setTextColor(Color.parseColor(white))
                txt_active.setBackgroundResource(R.drawable.nonselect_member_dashboard)
                txt_all.setBackgroundResource(R.drawable.nonselect_member_dashboard)
                txt_inactive.setBackgroundResource(R.drawable.select_member_dashboard)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDashBoard()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        familyDashboardInteractor = Utilities.getListener(this, FamilyDashboardInteractor::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        familyDashboardInteractor = null
    }

    override fun onBackButtonPressed() {
        (activity as FamilyDashboardActivity?)?.backAction()
    }

    override fun onResume() {
        super.onResume()
        if (familyDashboardInteractor != null)
            requireActivity().findViewById<CardView>(R.id.addComponent).visibility=View.VISIBLE
        requireActivity().findViewById<ImageView>(R.id.imgSearch).visibility=View.INVISIBLE
            //familyDashboardInteractor!!.onFamilyAddComponentVisible(FamilyDashboardIdentifiers.TypeFamilyMembership)
    }


    fun createMemberShipType() {
        startActivityForResult(Intent(context, AddMembershipTypeActivity::class.java).putExtra(Constants.Bundle.ID, groupId), REQUEST_CODE)
    }

    private fun getDashBoard() {
        prograss.visibility=View.VISIBLE
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        jsonObject.addProperty("group_id", groupId)
        if(type != "All"){
        jsonObject.addProperty("filter", type)
        }
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestInterface = RetrofitUtil.createRxResource(requireActivity(), RestApiService::class.java)
        compositeDisposable.add(
                requestInterface.getMembershipDashboard(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (prograss != null)
                                prograss.visibility = View.GONE
                            data = it.body()
                            rv_list.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireActivity(), 2)
                            rv_list.adapter = MemberDashboardAdapter(data?.membershipData!!, this)
                            txt_all.text = "All (" + data?.membershipCounts?.get(0)?.allCount + ")"
                            txt_active.text = "Current (" + data?.membershipCounts?.get(0)?.activeCount + ")"
                            txt_inactive.text = "Expired (" + data?.membershipCounts?.get(0)?.expairedCount + ")"
                            if (data?.membershipCounts?.get(0)?.totalDueAmount != null) {
                                txt_due.text = data?.membershipCounts?.get(0)?.totalDueAmount.toString()
                            } else {
                                txt_due.text = "0"
                            }
                        }, {
                            if (prograss != null)
                                prograss.visibility = View.GONE
                        })
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
                MembershipFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.Bundle.ID, id)
                    }
                }
    }

    override fun onItemClick(position: Int) =
            startActivityForResult(Intent(requireActivity(), MembershipUserListActivity::class.java).putExtra(Constants.Bundle.FAMILY_ID, groupId).putExtra(Constants.Bundle.ID, data?.membershipData!![position].membershipid.toString()).putExtra(Constants.Bundle.DATA, data?.membershipData!![position].membershipName).putExtra(Constants.Bundle.TYPE, "DASH").putExtra(Constants.Bundle.IDENTIFIER, data?.membershipCounts?.get(0)?.defaultMembershipId), REQUEST_CODE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getDashBoard()
        }
    }
}