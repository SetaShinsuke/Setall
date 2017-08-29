package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.seta.setall.R
import com.seta.setall.common.extensions.deleteLine
import com.seta.setall.common.extensions.money
import com.seta.setall.common.extensions.toFloatYuan2
import com.seta.setall.common.extensions.toYMD
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.domain.models.Transaction
import com.seta.setall.steam.mvpViews.TransactionRestoreMvpView
import com.seta.setall.steam.presenters.TransactionRestorePresenter
import kotlinx.android.synthetic.main.activity_transaction_list.*
import kotlinx.android.synthetic.main.item_trans_history.view.*
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class TransactionListActivity : BaseActivity(), TransactionRestoreMvpView {

    val transRestorePresenter = TransactionRestorePresenter()
    var adapter by Delegates.notNull<BasicAdapter<Transaction>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)
        mRvTransactions.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        adapter = BasicAdapter(R.layout.item_trans_history) {
            itemView, position, trans ->
            with(itemView) {
                mTvDate.text = "${position+1}.${trans.date.toYMD()}"
                var text = ""
                trans.steamApps.forEachIndexed {
                    index, steamApp ->
                    text += "${steamApp.name} - ￥${steamApp.purchasedPrice.toFloatYuan2()}"
                    if (index < trans.steamApps.lastIndex) {
                        text += "\n"
                    }
                }
                mTvTransDetail.text = text
                mTvTotal.deleteLine().money = trans.steamApps.map { it.initPrice }
                        .fold(0) { total: Int?, next: Int? -> total?.let { next?.plus(it) } }
                mTvTotalFinal.money = trans.steamApps.map { it.purchasedPrice }.fold(0) {
                    total: Int?, next: Int? ->
                    total?.let { next?.plus(it) }
                }
            }
        }
        mRvTransactions.adapter = adapter
        transRestorePresenter.attachView(this)
        transRestorePresenter.restoreTransactions()
    }

    override fun onTranListRestored(tranList: List<Transaction>) {
        adapter.refreshData(tranList)
    }

    override fun onTranListRestoreFail(t: Throwable) {
        toast("Restore fail!\n${t.message}")
    }

    override fun onDestroy() {
        super.onDestroy()
        transRestorePresenter.detachView()
    }
}
