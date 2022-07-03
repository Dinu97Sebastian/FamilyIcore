package com.familheey.app.Topic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.familheey.app.Activities.CreateEventActivity
import com.familheey.app.Activities.CreateFamilyActivity
import com.familheey.app.Announcement.CreateAnnouncementActivity
import com.familheey.app.Dialogs.NewUserHelperBottomSheetDialogFragment
import com.familheey.app.Discover.DiscoverActivity
import com.familheey.app.Discover.DiscoverSearchFragment
import com.familheey.app.Fragments.EventFragment
import com.familheey.app.Fragments.MyFamilyFragment
import com.familheey.app.Fragments.Posts.PostFragment
import com.familheey.app.Interfaces.HomeInteractor
import com.familheey.app.Models.FamilySuggestionWrapper
import com.familheey.app.Models.Response.FamilySearchModal
import com.familheey.app.Models.Response.UserNotification
import com.familheey.app.Need.CreateRequestActivity
import com.familheey.app.Post.CreatePostActivity
import com.familheey.app.R
import com.familheey.app.Utilities.Constants
import com.familheey.app.Utilities.Constants.ApiPaths.FIREBASE_DATABASE_URL
import com.familheey.app.Utilities.Constants.Bundle.IS_GLOBAL_SEARCH_ENABLED
import com.familheey.app.Utilities.SharedPref
import com.familheey.app.Utilities.SharedPref.getWalkThrough
import com.familheey.app.Utilities.SharedPref.setWalkThroughStatus
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_discover.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialogue.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class HomeFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener, NewUserHelperBottomSheetDialogFragment.OnNewUserHelperListener {
    private val CREATE_POST_REQUEST_CODE  = 101
    private val REQUEST_CREATE_REQUEST_CODE  = 501
    private var databaseReference: DatabaseReference? = null
    var unReadNotificationCount = 0
    var fragment: Fragment? = null
    private var backPressed: Long = 0
    private var isGlobalSearchEnabled = false
    private val userNotifications = ArrayList<UserNotification>()
    private var compositeDisposable: CompositeDisposable? = null
    private var suggestedFamilies = mutableListOf<FamilySearchModal>()
    companion object {
        @JvmStatic
        fun newInstance(isGlobalSearchEnabled: Boolean) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(IS_GLOBAL_SEARCH_ENABLED, isGlobalSearchEnabled)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isGlobalSearchEnabled = arguments?.getBoolean(IS_GLOBAL_SEARCH_ENABLED, false)
                ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
        bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem? -> onNavigationItemSelected(item!!) }
        initializeRestrictions()
    }

    private fun initializeRestrictions() {
        bottomNavigation.selectedItemId = R.id.navigationHome
 //Dinu(05/07/2021) for remove quickTour
//        if (getWalkThrough())
//            initializeSpotLight()
//        else

            if (!SharedPref.userHasFamily())
            getNewUserFamilySuggestions()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigationHome -> {
                initializeFireBase()
                fragment = if (SharedPref.userHasFamily())
                    PostFragment.newInstance()
                else
                    PostFragment.newInstance(1)
            }
            R.id.navigationCalendar -> {
                initializeFireBase()
                fragment = EventFragment.newInstance()
            }
            R.id.navigationProfile -> {
                initializeFireBase()
                fragment = MyFamilyFragment.newInstance()
            }
            R.id.navigationSearch -> {
                initializeFireBase()
                fragment = DiscoverSearchFragment.newInstance(1)
            }
            R.id.navigationAdd -> {
                val dialog = Dialog(requireActivity())
                dialog.setContentView(R.layout.dialogue)
                val window = dialog.window!!
                window.setBackgroundDrawableResource(android.R.color.transparent)
                val wlp = window.attributes
                wlp.gravity = Gravity.BOTTOM
                wlp.x = 0
                wlp.y = 200
                setAnimation(dialog.findViewById(R.id.full_View))
                if (!SharedPref.userHasFamily()) {
                    dialog.make_announcement.visibility = View.GONE
                    dialog.createrequest.visibility = View.GONE
                }

                dialog.create_post.setOnClickListener {
                    dialog.dismiss()
                    startActivityForResult(Intent(context, CreatePostActivity::class.java), CREATE_POST_REQUEST_CODE)

                    activity?.overridePendingTransition(R.anim.enter,
                            R.anim.exit)

                }
                dialog.make_announcement.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(activity, CreateAnnouncementActivity::class.java))

                    activity?.overridePendingTransition(R.anim.enter,
                            R.anim.exit)
                }
                dialog.createFamily.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(activity, CreateFamilyActivity::class.java))

                    activity?.overridePendingTransition(R.anim.enter,
                            R.anim.exit)
                }
                dialog.create_event_id.setOnClickListener {
                    dialog.dismiss()
                    startActivity(Intent(activity, CreateEventActivity::class.java))

                    activity?.overridePendingTransition(R.anim.enter,
                            R.anim.exit)
                }
                dialog.createrequest.setOnClickListener {
                    dialog.dismiss()
                    startActivityForResult(Intent(context, CreateRequestActivity::class.java), REQUEST_CREATE_REQUEST_CODE)

                    activity?.overridePendingTransition(R.anim.enter,
                            R.anim.exit)
                }

                dialog.show()
            }
        }
        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            activity?.supportFragmentManager?.beginTransaction()!!
                    .replace(R.id.fragmentsLoader, fragment)
                    .addToBackStack(fragment.javaClass.name)
                    .commit()
            return true
        }
        return false
    }

    private var notificationListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            userNotifications.clear()
            for (notificationDataSnaphot in dataSnapshot.children) {
                val userNotification = UserNotification()
                userNotification.key = notificationDataSnaphot.key
                userNotification.category = notificationDataSnaphot.child("category").value.toString()
                userNotification.create_time = notificationDataSnaphot.child("create_time").value.toString()
                userNotification.from_id = notificationDataSnaphot.child("from_id").value.toString()
                userNotification.link_to = notificationDataSnaphot.child("link_to").value.toString()
                userNotification.message = notificationDataSnaphot.child("message").value.toString()
                userNotification.message_title = notificationDataSnaphot.child("message_title").value.toString()
                userNotification.propic = notificationDataSnaphot.child("propic").value.toString()
                userNotification.type = notificationDataSnaphot.child("type").value.toString()
                if (notificationDataSnaphot.child("sub_type").value != null) userNotification.sub_type = notificationDataSnaphot.child("sub_type").value.toString() else userNotification.sub_type = ""
                userNotification.type_id = notificationDataSnaphot.child("type_id").value.toString()
                userNotification.visible_status = notificationDataSnaphot.child("visible_status").value.toString()
                userNotifications.add(userNotification)
            }
            try {
                unReadNotificationCount = UserNotification.getUnreadUserNotifications(userNotifications)
                when (val currentFragment: Fragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentsLoader)!!) {
                    is EventFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                    is DiscoverSearchFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                    is MyFamilyFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                    is PostFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                }
                val homeInteractor = context as HomeInteractor
                homeInteractor.setNotificationCount(unReadNotificationCount)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Failed to read value
            // Log.w(HomeActivity.TAG, "Failed to read value.", databaseError.toException())
        }
    }

    private fun initializeFireBase() {
        databaseReference = FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).getReference(Constants.ApiPaths.FIREBASE_USER_TYPE + SharedPref.getUserRegistration().id + "_notification")
       /* databaseReference?.removeEventListener(notificationListener)
        databaseReference?.addValueEventListener(notificationListener)*/
        val query = databaseReference?.orderByKey()?.limitToLast(150)

        if (query != null) {
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userNotifications.clear()
                    for (notificationDataSnaphot in snapshot.children) {
                        val userNotification = UserNotification()
                        userNotification.key = notificationDataSnaphot.key
                        userNotification.category = notificationDataSnaphot.child("category").value.toString()
                        userNotification.create_time = notificationDataSnaphot.child("create_time").value.toString()
                        userNotification.from_id = notificationDataSnaphot.child("from_id").value.toString()
                        userNotification.link_to = notificationDataSnaphot.child("link_to").value.toString()
                        userNotification.message = notificationDataSnaphot.child("message").value.toString()
                        userNotification.message_title = notificationDataSnaphot.child("message_title").value.toString()
                        userNotification.propic = notificationDataSnaphot.child("propic").value.toString()
                        userNotification.type = notificationDataSnaphot.child("type").value.toString()
                        if (notificationDataSnaphot.child("sub_type").value != null) userNotification.sub_type = notificationDataSnaphot.child("sub_type").value.toString() else userNotification.sub_type = ""
                        userNotification.type_id = notificationDataSnaphot.child("type_id").value.toString()
                        userNotification.visible_status = notificationDataSnaphot.child("visible_status").value.toString()
                        userNotifications.add(userNotification)
                    }
                    try {
                        unReadNotificationCount = UserNotification.getUnreadUserNotifications(userNotifications)
                        when (val currentFragment: Fragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentsLoader)!!) {
                            is EventFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                            is DiscoverSearchFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                            is MyFamilyFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                            is PostFragment -> currentFragment.setNotificationCount(unReadNotificationCount)
                        }
                        val homeInteractor = context as HomeInteractor
                        homeInteractor.setNotificationCount(unReadNotificationCount)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun setAnimation(view: View) {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(0.2f, 1.0f)
        alphaAnimation.duration = 400
        view.startAnimation(alphaAnimation)
        val scaleAnimation = ScaleAnimation(0.75f, 1.0f, 0.75f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.duration = 400
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)
        view.startAnimation(animationSet)
    }

    fun onBackPressed() {
        when {
            backPressed + 2000 > System.currentTimeMillis() -> {
                activity?.finishAffinity()
            }
            bottomNavigation.selectedItemId == R.id.navigationHome -> {
                Toast.makeText(context, resources.getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
                backPressed = System.currentTimeMillis()
            }
            else -> {
                bottomNavigation.selectedItemId = R.id.navigationHome
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initializeFireBase()
    }

    fun initializeSpotLight() {
        if (!getWalkThrough())
            return
        val sequence = TapTargetSequence(activity)
                .targets(
                        TapTarget.forView(bottomNavigation.findViewById(R.id.navigationSearch), "Discover and join families")
                                .cancelable(false)
                                .id(1),
                        TapTarget.forView(bottomNavigation.findViewById(R.id.navigationAdd), "Add your own posts, announcements")
                                .cancelable(false)
                                .id(2)

                )
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceCanceled(lastTarget: TapTarget?) {
                        setWalkThroughStatus(false)
                    }

                    override fun onSequenceFinish() {
                        try {
                            if (bottomNavigation.selectedItemId == R.id.navigationHome) {
                                when (val currentFragment: Fragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentsLoader)!!) {
                                    is PostFragment -> currentFragment.initializeSpotLight()
                                }
                            } else {
                                bottomNavigation.selectedItemId = R.id.navigationHome
                                Handler().postDelayed({
                                    when (val currentFragment: Fragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentsLoader)!!) {
                                        is PostFragment -> currentFragment.initializeSpotLight()
                                    }
                                }, 600)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            if (!SharedPref.userHasFamily())
                                getNewUserFamilySuggestions()
                        }
                    }

                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                        setWalkThroughStatus(false)
                    }
                })
        sequence.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_POST_REQUEST_CODE) {
            bottomNavigation.menu.findItem(R.id.navigationHome).isChecked = true
            fragment = PostFragment.newInstance()
            loadFragment(fragment)
        } else if (requestCode == REQUEST_CREATE_REQUEST_CODE) {
            bottomNavigation.menu.findItem(R.id.navigationHome).isChecked = true
            fragment = PostFragment.newInstance(2)
            loadFragment(fragment)
        }
    }

    fun getNewUserFamilySuggestions() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("user_id", SharedPref.getUserRegistration().id)
        val requestBody: RequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        compositeDisposable?.add(
                RetrofitUtil.createRxResource(requireContext(), RestApiService::class.java).getNewUserFamilySuggestions(requestBody).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                val familySuggestionWrapper: FamilySuggestionWrapper? = response?.body()
                                familySuggestionWrapper?.suggestedFamily?.let {
                                    suggestedFamilies.let {
                                        it.clear()
                                        it.addAll(familySuggestionWrapper.suggestedFamily)
                                        val dialogFragment = NewUserHelperBottomSheetDialogFragment.newInstance(suggestedFamilies as ArrayList<FamilySearchModal>)
                                        dialogFragment.setTargetFragment(this@HomeFragment, 102)
                                        dialogFragment.show(requireActivity().supportFragmentManager, "NewUser")
                                    }
                                }
                            }
                        }, { error ->
                            error.printStackTrace()
                        })
        )
    }

    override fun onNewUserDiscoverRequested() {
        /*bottomNavigation.selectedItemId = R.id.navigationSearch*/

        startActivity(Intent(context, DiscoverActivity::class.java).putExtra("POS", 1))

        activity?.overridePendingTransition(R.anim.enter,
                R.anim.exit)
    }
}
