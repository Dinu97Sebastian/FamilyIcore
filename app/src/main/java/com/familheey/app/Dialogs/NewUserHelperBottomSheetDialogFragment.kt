package com.familheey.app.Dialogs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.familheey.app.Activities.CreateFamilyActivity
import com.familheey.app.Adapters.FamilySuggestionAdapter
import com.familheey.app.Models.Response.FamilySearchModal
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.CornerFamily
import kotlinx.android.synthetic.main.item_new_user_helper.*


class NewUserHelperBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var familySuggestionAdapter: FamilySuggestionAdapter? = null
    private var suggestedFamilies = mutableListOf<FamilySearchModal>()
    private var listener: OnNewUserHelperListener? = null

    companion object {
        @JvmStatic
        fun newInstance(suggestedFamilies: ArrayList<FamilySearchModal>) =
                NewUserHelperBottomSheetDialogFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList(Constants.Bundle.DATA, suggestedFamilies)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
        suggestedFamilies = arguments?.getParcelableArrayList<FamilySearchModal>(Constants.Bundle.DATA)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_new_user_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBottomSheetOperations()
    }

    private fun initializeBottomSheetOperations() {
        val radius = resources.getDimension(R.dimen.card_top_radius)
        newUserHelper.shapeAppearanceModel = newUserHelper.shapeAppearanceModel
                .toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setBottomRightCornerSize(0f)
                .setBottomLeftCornerSize(0f)
                .build()
        viewAll.setOnClickListener {
            try {
                listener?.onNewUserDiscoverRequested()
                dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        skipForNow.setOnClickListener {
            dismiss()
        }
        createFamily.setOnClickListener {
            startActivity(Intent(context, CreateFamilyActivity::class.java))
        }
        familySuggestionAdapter = FamilySuggestionAdapter(requireContext(), suggestedFamilies)
        familySuggestions.layoutManager = LinearLayoutManager(context)
        familySuggestions.adapter = familySuggestionAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as OnNewUserHelperListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnNewUserHelperListener {
        fun onNewUserDiscoverRequested()
    }
}