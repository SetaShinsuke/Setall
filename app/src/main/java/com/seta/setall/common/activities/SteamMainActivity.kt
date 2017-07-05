package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.extensions.DelegateSteam
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity

class SteamMainActivity : AppCompatActivity() {

    val userId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvUserInfo.text = "User id : $userId"
    }
}
