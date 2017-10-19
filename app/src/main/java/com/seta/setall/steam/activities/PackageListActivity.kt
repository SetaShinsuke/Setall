package com.seta.setall.steam.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.extensions.toYuan
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.views.adapters.BasicAdapter
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.PackageDetailBean
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.events.CreateStartEvent
import com.seta.setall.steam.extensions.loadImg
import com.seta.setall.steam.mvpViews.PackageDetailMvpView
import com.seta.setall.steam.presenters.PackageDetailPresenter
import kotlinx.android.synthetic.main.activity_package_list.*
import kotlinx.android.synthetic.main.item_steam_package.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class PackageListActivity : BaseActivity(), PackageDetailMvpView {

    val packDetailPresenter = PackageDetailPresenter()
    var adapter by Delegates.notNull<BasicAdapter<PackageDetailBean>>()
    val selectedPackages = ArrayList<PackageDetailBean>()
    val gameIds = ArrayList<Int>() //OwnedGamesActivity中选中的所有主gameId

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
                mTvAppsCount.text = String.format(getString(R.string.pack_contains_count_price), data.apps.size, data.price.final.toYuan())
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
        logD("Pack ids : $packIds")
        gameIds.addAll(intent.getIntegerArrayListExtra(SteamConstants.GAME_IDS))
        if (packIds != null && packIds.isNotEmpty()) {
            loadingDialog?.show()
            packDetailPresenter.loadPackages(packIds)
        } else {
            toast(R.string.no_package_to_select)
        }
        registerBus()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: CreateStartEvent) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        packDetailPresenter.detachView()
    }

    override fun onPackageDetailLoad(packDetails: List<PackageDetailBean>) {
        loadingDialog?.dismiss()
        adapter.refreshData(packDetails.filter { it.apps.size > 1 }) //剔除只有1个游戏的包
    }

    override fun onPackageDetailLoadFail(t: Throwable?) {
        loadingDialog?.dismiss()
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
                if (TransManager.steamApps.isEmpty()) {
                    toast(R.string.no_item_selected)
                    return super.onOptionsItemSelected(item)
                }
                val allSelectedAppIds = ArrayList<Int>() //选中的包中包含的Game
                selectedPackages.forEach {
                    allSelectedAppIds.addAll(it.apps.map { it.id }.distinct()) //包里的所有appId
                }
                //从 Manager 缓存的 Apps 中
                //过滤掉 包含在包中的 apps, 即剩下的 SteamApps 都是 未在包中的游戏、或者是包
                //也即: 这些 SteamApp 的 type，要么是待定(UNKNOWN),要么是包(BUNDLE_PACK)
                //1.剔除包内的App
                val allApps = TransManager.steamApps.filter { !allSelectedAppIds.contains(it.appId) } as ArrayList<SteamApp>
                //2.添加包
                allApps.addAll(selectedPackages.distinct().map { SteamApp(it) })
                TransManager.steamApps.clear() //重置 Manager 的缓存
                TransManager.steamApps.addAll(allApps)
                TransManager.steamApps.forEach { logD(it.name) }
                //去掉包含在包中的 app, 留下尚未选择的 主game
                startActivity<GameListActivity>(SteamConstants.GAME_IDS to
                        gameIds.filter {
                            gameId ->
                            selectedPackages.none {
                                it.apps.map { it.id }.contains(gameId)
                            }
                        })
                //下一步,剔除所有的包，只提交 type 未知的 Game/DLC
//                startActivity<GameListActivity>(
//                        SteamConstants.GAME_IDS to
//                                TransManager.steamApps.filter {
//                                    it.type == SteamConstants.TYPE_UNKNOWN
//                                }.map { it.appId })
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
