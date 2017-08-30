package com.seta.setall.steam.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.seta.setall.R
import com.seta.setall.common.extensions.logD
import com.seta.setall.common.framework.BaseActivity
import com.seta.setall.common.interfaces.ResultHandler
import com.seta.setall.steam.db.SteamDb
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class TransRestoreActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trans_restore)
        if (intent.action == null
                || intent.action != Intent.ACTION_SEND
                || intent.action != Intent.ACTION_SEND_MULTIPLE
                || intent.extras == null
                || !intent.extras.containsKey(Intent.EXTRA_STREAM)
                ) {
            startActivity<MainActivity>()
            finish()
        }

//        val jsonPath = Uri2Path(this, intent.getParcelableExtra(Intent.EXTRA_STREAM))
//        val jsonPath = Uri2Path(this, intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
        val jsonPath = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM).path
        loadingDialog?.setMessage("正在恢复备份")
        loadingDialog?.show()
        SteamDb.instance.restoreFromFile(jsonPath, object : ResultHandler {
            override fun <T> onSuccess(result: T) {
                loadingDialog?.dismiss()
                toast("恢复成功!")
                startActivity<MainActivity>()
                finish()
            }

            override fun onFail(t: Throwable) {
                loadingDialog?.dismiss()
                toast("恢复失败\n${t.message}")
                logD("恢复失败\n${t.message}")
                startActivity<MainActivity>()
                finish()
            }

        })
    }
}
