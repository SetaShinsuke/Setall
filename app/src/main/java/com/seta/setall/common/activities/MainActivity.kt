package com.seta.setall.common.activities

import android.os.Bundle
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.steam.activities.SteamMainActivity
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity<SteamMainActivity>()
        setHomeAsBackEnabled(false)
        setSwipeBackEnable(false)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnSteam -> startActivity<SteamMainActivity>()
        }
    }
}
