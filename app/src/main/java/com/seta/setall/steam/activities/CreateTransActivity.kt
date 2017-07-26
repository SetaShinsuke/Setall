package com.seta.setall.steam.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.DateUtils
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.InputDialog
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.PlayerInfoBean
import com.seta.setall.steam.extensions.DelegateSteam
import com.seta.setall.steam.mvpViews.PlayerInfoMvpView
import com.seta.setall.steam.presenters.PlayerInfoPresenter
import kotlinx.android.synthetic.main.activity_create_trans.*
import org.jetbrains.anko.startActivityForResult
import java.util.*

class CreateTransActivity : BaseActivity(), PlayerInfoMvpView {

    val playerInfoPresenter: PlayerInfoPresenter = PlayerInfoPresenter()
    var steamUserId: String by DelegateSteam.steamPreference(this, SteamConstants.STEAM_USER_ID, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trans)
        playerInfoPresenter.attachView(this)
        if (steamUserId != "") {
            playerInfoPresenter.loadPlayerInfo(steamUserId)
        }
        mRvApps.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
//        enableHomeAsBack(true)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnAddApp -> startActivityForResult<OwnedGamesActivity>(SteamConstants.CODE_SELECT_GAMES)
            R.id.mBtnDate -> {
                val calendar = Calendar.getInstance()
                val datePickDialog = DatePickerDialog(this,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            logD("$year-$month-$dayOfMonth")
                            mTvDate.text = DateUtils.getYMD(year, month, dayOfMonth)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                datePickDialog.show()
            }
            R.id.mBtnBuyer -> {
                val inputDialog = InputDialog(this)
                inputDialog.show(R.string.buyer_name, object : InputDialog.InputDialogInterface {
                    override fun onContentConfirm(content: String) {
                        if (content == "") {
                            toast(R.string.name_null_warn)
                            return
                        }
                        mTvBuyer.text = content
                    }

                })
            }
            R.id.mBtnOwner -> {
                val inputDialog = InputDialog(this)
                inputDialog.show(R.string.owner_name, object : InputDialog.InputDialogInterface {
                    override fun onContentConfirm(content: String) {
                        if (content == "") {
                            toast(R.string.name_null_warn)
                            return
                        }
                        mTvOwner.text = content
                    }

                })
            }
        }
    }

    override fun onPlayerInfoLoad(playerInfoBean: PlayerInfoBean) {
        if (mTvBuyer.text == null || mTvBuyer.text == "") {
            mTvBuyer.text = playerInfoBean.personaname
        }
        if (mTvOwner.text == null || mTvOwner.text == "") {
            mTvOwner.text = playerInfoBean.personaname
        }
    }

    override fun onPlayerInfoLoadFail(t: Throwable) {
        logD("Load player info fail : ${t.message}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SteamConstants.CODE_SELECT_GAMES -> {
//                val adapter = BasicAdapter<SteamApp>(R.layout.item_steam_app,)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
