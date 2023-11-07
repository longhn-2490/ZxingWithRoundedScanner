package com.example.zxingwithroundedscanner

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    val adapter by lazy {
        ViewPagerAdapter(this, listOf(ScanFragment(), HistoryFragment()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent))
        setMarginSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setupTabLayout()
    }

    private fun setupTabLayout() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Scan"
                1 -> "History"
                else -> null
            }
        }.attach()
    }
    private fun setMarginSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { view, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    fun setStatusBarColor(color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    fun goToWebView(url: String) {
        supportFragmentManager.beginTransaction().add(R.id.rootView, WebViewFragment.newInstance(url)).commit()
    }

    private var alert: AppAlert? = null

    fun showAlert(
        message: String?,
        title: String? = null,
        positiveButtonText: Int = R.string.ok,
        negativeButtonText: Int? = null,
        neutralButtonText: Int? = null,
        isCancelable: Boolean = false,
        onNegativeButtonClick: (() -> Unit)? = null,
        onNeutralButtonClick: (() -> Unit)? = null,
        onPositiveButtonClick: (() -> Unit)? = null
    ) {
        if (message == null || alert?.isShowing == true) return
        AppAlert.Builder(this)
            .title(title)
            .message(message)
            .positiveButton(positiveButtonText, onPositiveButtonClick)
            .negativeButton(negativeButtonText, onNegativeButtonClick)
            .neutralButton(neutralButtonText, onNeutralButtonClick)
            .cancelable(isCancelable)
            .show().apply {
                alert = this
            }
    }
}
