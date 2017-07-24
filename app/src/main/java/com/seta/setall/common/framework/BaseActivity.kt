package com.seta.setall.common.framework

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.seta.swipebackutility.SwipeBackLayout
import com.seta.swipebackutility.app.SwipeBackActivityBase
import com.seta.swipebackutility.app.SwipeBackActivityHelper
import kotlin.properties.Delegates

/**
 * Created by SETA_WORK on 2017/7/24.
 */
class BaseActivity : AppCompatActivity(), SwipeBackActivityBase {

    var homeAsBackEnabled = false
    var mHelper: SwipeBackActivityHelper by Delegates.notNull<SwipeBackActivityHelper>()

    

    fun enableHomeAsBack(enabled: Boolean) {
        homeAsBackEnabled = enabled
        if (homeAsBackEnabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getSwipeBackLayout(): SwipeBackLayout {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSwipeBackEnable(enable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scrollToFinishActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}