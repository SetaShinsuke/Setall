package com.seta.setall.steam.presenters

import android.content.Context
import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.common.utils.writeFile
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.DataBackupMvpView

/**
 * Created by SETA_WORK on 2017/8/29.
 */
class DataBackupPresenter : BasePresenter<DataBackupMvpView>() {

    fun backupData(context: Context) {
    }
}