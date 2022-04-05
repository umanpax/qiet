package com.qiet.modules.activities.base

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.qiet.R
import com.qiet.modules.fragments.mapalarm.MapAlarmFragment
import com.qiet.utils.Application
import com.qiet.utils.PrefsManager
import com.qiet.utils.TabsAdapter
import com.qiet.workflow.QietWorkflow

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerHamburger: ImageView
    private var drawerNavigationMenuIsOpen: Boolean = false
    private lateinit var presenter: BasePresenter
    private lateinit var prefManager: PrefsManager
    protected var workflow = QietWorkflow()
    lateinit var tabLayout: TabLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mapAlarmFragment: MapAlarmFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        Application.changeStatusBarColor(this, R.color.colorWhite)
        initBaseViews()
        initBaseWorkflow()
        initFragments()
        initBaseTabs()
        initBaseListeners()

        prefManager = PrefsManager(applicationContext)
        presenter = BasePresenter(this, workflow)
    }

    private fun initFragments() {
        mapAlarmFragment = MapAlarmFragment()
    }

    private fun initBaseViews() {
        drawerHamburger = findViewById(R.id.drawer_hamburger)
    }

    private fun initBaseListeners() {
    }

    private fun initBaseWorkflow() {
        workflow = Application.getQietWorkflow()
    }

    private fun initBaseTabs() {
        tabLayout = findViewById(R.id.tab_layout)

        tabLayout.apply {
            addTab(tabLayout.newTab().setIcon(null))
            tabGravity = TabLayout.GRAVITY_FILL
            tabLayout.setTabTextColors(
                ContextCompat.getColor(context, R.color.colorWhite),
                ContextCompat.getColor(context, R.color.colorWhite));
        }

        val viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        viewPager.offscreenPageLimit = 1
        val tabsAdapter = TabsAdapter(supportFragmentManager, tabLayout.tabCount,
            mapAlarmFragment,mapAlarmFragment.javaClass.name)
        tabLayout.visibility = View.GONE
        viewPager.adapter = tabsAdapter
        viewPager.currentItem = 0
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun onBackPressed() {
        if (drawerNavigationMenuIsOpen) {
            drawerLayout.closeDrawers()
        } else {
            finish()
        }
    }

}


