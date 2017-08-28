package com.seta.setall.steam.presenters

import com.seta.setall.common.mvp.BasePresenter
import com.seta.setall.steam.db.SteamDb
import com.seta.setall.steam.mvpViews.TransactionRestoreMvpView

/**
 * Created by SETA_WORK on 2017/8/28.
 */
class TransactionRestorePresenter : BasePresenter<TransactionRestoreMvpView>() {

    fun restoreTransactions() {
        SteamDb.instance.findTransActions {
            transList ->
            mvpView?.onTranListRestored(transList)
        }
    }
}