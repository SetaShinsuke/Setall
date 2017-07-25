package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.steam.activity.SteamMainActivity
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity<SteamMainActivity>()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnSteam -> startActivity<SteamMainActivity>()
        }
    }
}
