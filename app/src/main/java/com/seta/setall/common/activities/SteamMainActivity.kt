package com.seta.setall.common.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.GameBean
import com.seta.setall.steam.api.models.OwnedGameBean
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.db.SteamDbHelper
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.OwnedGamesView
import com.seta.setall.steam.presenters.OwnedGamesPresenter
import kotlinx.android.synthetic.main.activity_steam_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class SteamMainActivity : AppCompatActivity(), OwnedGamesView {
    var userId: String? by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")
    val ownedGamePresenter: OwnedGamesPresenter = OwnedGamesPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steam_main)
        if (userId == "") {
            logD("userId : $userId")
            startActivity<SteamLoginActivity>()
            finish()
        }
        mTvUserInfo.text = "User id : $userId"
        ownedGamePresenter.attachView(this)
        ownedGamePresenter.loadOwnedGames(userId)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnLogout -> {
                userId = null
                finish()
                toast("已注销！")
                startActivity<SteamLoginActivity>()
            }
        }
    }

    override fun onGamesLoad(ownedGameBean: OwnedGameBean) {
        val games: List<GameBean> = ownedGameBean.games.sortedWith(compareBy({ it.name }, { it.appid }))
        var content: String = "Total : ${ownedGameBean.game_count}\nAll steamApps : "
        games.forEachIndexed { index, gameBean -> content += "\n$index ${gameBean.name}" }
        mTvUserInfo.text = content

        val steamApps = ArrayList<SteamApp>()
        games.forEachIndexed { i, (appid, name) ->
            steamApps.add(SteamApp(appid.toLong(), name, "CNY", 100 * i, 10 * i, Date(), 0, null))
        }
        val steamDb = SteamDb.instance
        steamDb.saveSteamApps(steamApps)
        UtilMethods.exportDb(this, SteamDbHelper.STEAM_DB_NAME)

        //伪造数据
//        LogX.mark()
//        LogX.fastLog("开始保存 记录!!")
//        val transList = ArrayList<Transaction>()
//        for (i in 0..5) {
//            val trans = Transaction(i.toLong(), Date(), "buyer_$i", "owner_$i", ArrayList<SteamApp>())
//            if (2 * i < games.size) {
//                with(games[i]) {
//                    (trans.steamApps as ArrayList).add(SteamApp(appId.toLong(), name, "CNY", 100 * i, 10 * i, Date(), 0, null))
//                }
//                with(games[2 * i]) {
//                    (trans.steamApps as ArrayList).add(SteamApp(appId.toLong(), name, "CNY", 200 * i, 20 * i, Date(), 0, null))
//                }
//            }
//            transList.add(trans)
//        }
//        val steamDb = SteamDb()
//        steamDb.saveTransactions(transList)
//        UtilMethods.exportDb(this, SteamDbHelper.STEAM_DB_NAME)
    }

    override fun onGamesLoadFail(t: Throwable) {
        mTvUserInfo.text = ""
    }
}
