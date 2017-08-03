package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.seta.setall.R
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.extensions.toYuan
import com.seta.setall.common.extensions.toast
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.PackageDetailMvpView
import com.seta.setall.steam.presenters.PackageDetailPresenter
import kotlinx.android.synthetic.main.activity_package_list.*
import kotlinx.android.synthetic.main.item_steam_package.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.properties.Delegates

class PackageListActivity : BaseActivity(), PackageDetailMvpView {

    val packDetailPresenter = PackageDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<PackageDetailBean>>()
    val selectedIds = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_list)
        mRvPackageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = BasicAdapter(R.layout.item_steam_package) {
            view, position, data ->
            with(view) {
                mIvHeader.loadImg(data.page_image)
                mTvPackName.text = data.name
                mIvHeaderCheck.setVisible(selectedIds.contains(data.id))
//                mTvAppsCount.text = getStringFormated(R.string.pack_contains_count, data.apps.size, data.price.final)
                mTvAppsCount.text = String.format(getString(R.string.pack_contains_count), data.apps.size, data.price.final.toYuan())
//                mTvAppsCount.text = getStringFormated(R.string.pack_contains_count, 1, 1)
                var content = ""
                val lastIndex = data.apps.lastIndex
                data.apps.forEachIndexed {
                    index, app ->
                    content += app.name
                    if (index != lastIndex) {
                        content += ", "
                    }
                }
                mTvApps.text = content
                onClick {
                    if (selectedIds.contains(data.id)) {
                        selectedIds.remove(data.id)
                    } else {
                        selectedIds.add(data.id)
                    }
                    adapter.notifyItemChanged(position)
                }
            }
        }
        mRvPackageList.adapter = adapter

        packDetailPresenter.attachView(this)
        val packIds = intent.getIntegerArrayListExtra(SteamConstants.PACK_IDS)
        packIds?.let {
            loadingDialog?.show()
            packDetailPresenter.loadPackages(packIds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        packDetailPresenter.detachView()
    }

    override fun onPackageDetailLoad(packDetails: List<PackageDetailBean>) {
        loadingDialog?.hide()
        adapter.refreshData(packDetails)
    }

    override fun onPackageDetailLoadFail(t: Throwable?) {
        loadingDialog?.hide()
        toast(R.string.games_load_fail)
    }
}
