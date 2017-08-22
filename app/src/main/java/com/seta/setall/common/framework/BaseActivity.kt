package com.seta.setall.common.framework

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.seta.setall.R
import com.seta.swipebackutility.SwipeBackLayout
import com.seta.swipebackutility.Utils
import com.seta.swipebackutility.app.SwipeBackActivityBase
import com.seta.swipebackutility.app.SwipeBackActivityHelper
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates

/**
 * Created by Seta.Driver on 2017/7/26.
 */
open class BaseActivity : AppCompatActivity(), SwipeBackActivityBase {
    private var mHelper by Delegates.notNull<SwipeBackActivityHelper>()
    private var homeAsBackEnabled = true
    var loadingDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHelper = SwipeBackActivityHelper(this)
        mHelper.onActivityCreate()
        setHomeAsBackEnabled(homeAsBackEnabled)
        loadingDialog = ProgressDialog(this)
                .apply {
                    setMessage(getString(R.string.loading))
                    setCancelable(false)
                }
    }

    fun registerBus() {
        EventBus.getDefault().register(this)
    }

    fun postEvent(event: Any) {
        EventBus.getDefault().post(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.dismiss()
        loadingDialog = null
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun setHomeAsBackEnabled(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
        supportActionBar?.setDisplayShowHomeEnabled(enabled)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (homeAsBackEnabled && item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mHelper.onPostCreate()
    }

    override fun findViewById(id: Int): View {
        val v = super.findViewById(id) ?: return mHelper.findViewById(id)
        return v
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        return mHelper.swipeBackLayout
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    override fun scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this)
        swipeBackLayout.scrollToFinishActivity()
    }
}