package com.familheey.app.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.familheey.app.Discover.ui.main.DiscoverFamilyFragment
import com.familheey.app.NewUserWelcomeActivity
import com.familheey.app.R
import kotlinx.android.synthetic.main.activity_discover.*


class DiscoverFamilyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_family)
        loadFragment(DiscoverFamilyFragment.newInstance(1));
        btn_back_home.setOnClickListener {
            onBackPressed()
        }

        search_discover.setOnEditorActionListener { _, actionId, _ ->
            onSearchQueryListener(actionId)
        }
    }

    protected fun onSearchQueryListener(actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search()
            try {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
    }

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }
    private fun search() {
        val discoverFragment: DiscoverFamilyFragment = supportFragmentManager.fragments[0] as DiscoverFamilyFragment
        discoverFragment.onSearch(search_discover.text.toString());
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, NewUserWelcomeActivity::class.java))
        finish()
    }
}