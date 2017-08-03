package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.seta.setall.R
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.extensions.toYuan
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.PackageDetailMvpView
import com.seta.setall.steam.presenters.PackageDetailPresenter
import kotlinx.android.synthetic.main.activity_package_list.*
import kotlinx.android.synthetic.main.item_steam_package.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class PackageListActivity : BaseActivity(), PackageDetailMvpView {

    val packDetailPresenter = PackageDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<PackageDetailBean>>()
    //    val selectedIds = ArrayList<Int>()
    val selectedPackages = ArrayList<PackageDetailBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_list)
        mRvPackageList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = BasicAdapter(R.layout.item_steam_package) {
            view, position, data ->
            with(view) {
                mIvHeader.loadImg(data.page_image)
                mTvPackName.text = data.name
                mIvHeaderCheck.setVisible(selectedPackages.contains(data))
//                mTvAppsCount.text = getStringFormated(R.string.pack_contains_count, data.apps.size, data.price.final)
                mTvAppsCount.text = String.format(getString(R.string.pack_contains_count), data.apps.size, data.price.final.toYuan())
//                mTvAppsCount.text = getStringFormated(R.string.pack_contains_count, 1, 1)
                var content = ""
                val lastIndex = data.apps.lastIndex
                data.apps.forEachIndexed {
                    index, app ->
                    content += app.name
                    if (index != lastIndex) {
                        content += "\n"
                    }
                }
                mTvApps.text = content
                onClick {
                    if (selectedPackages.contains(data)) {
                        selectedPackages.remove(data)
                    } else {
                        selectedPackages.add(data)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.go_next, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.menu_commit -> {
                if(selectedPackages.isEmpty()){
                    toast(R.string.no_item_selected)
                    return super.onOptionsItemSelected(item)
                }
                val packs = ArrayList<SteamApp>()
                selectedPackages.forEach {
                    it.apps.forEach {
                        packs.add(SteamApp(it.id, it.name))
                    }
                }
                TransManager.steamApps.addAll(packs.distinct())
                startActivity<GameListActivity>(SteamConstants.GAME_IDS to TransManager.steamApps.map { it.appId })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (selectedPackages.isNotEmpty()) {
            alert {
                titleResource = R.string.hint
                message = String.format(getString(R.string.cancel_game_select_hint), selectedPackages.size)
                positiveButton(R.string.confirm) { dialog ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                negativeButton(R.string.cancel) { dialog -> dialog.dismiss() }
                show()
            }
            return
        }
        super.onBackPressed()
    }
}
