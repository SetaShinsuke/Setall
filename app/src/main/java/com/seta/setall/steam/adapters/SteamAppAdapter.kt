package com.seta.setall.steam.adapters

import android.app.DatePickerDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seta.setall.R
import com.seta.setall.common.extensions.*
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.views.InputDialog
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.domain.TransManager
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.extensions.loadImg
import kotlinx.android.synthetic.main.item_create_trans_header.view.*
import kotlinx.android.synthetic.main.item_steam_app_game.view.*
import kotlinx.android.synthetic.main.item_steam_app_pack.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by SETA_WORK on 2017/8/7.
 */
class SteamAppAdapter(var data: List<SteamApp> = ArrayList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_HEADER = 0
        val TYPE_GAME = 1
        val TYPE_DLC = 2
        val TYPE_PACK = 3
    }

    fun refreshData(newData: List<SteamApp>) {
        this.data = newData
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER
        }
        return when (data[position - 1].type) {
            SteamConstants.TYPE_UNKNOWN -> TYPE_GAME
            SteamConstants.TYPE_GAME -> TYPE_GAME
            SteamConstants.TYPE_DLC -> TYPE_DLC
            SteamConstants.TYPE_BUNDLE_PACK -> TYPE_PACK
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_HEADER -> return HeaderHolder(layoutInflater.inflate(R.layout.item_create_trans_header, parent, false))
            TYPE_GAME, TYPE_DLC -> {
                val view = layoutInflater.inflate(R.layout.item_steam_app_game, parent, false)
                return GameHolder(view)
            }
            TYPE_PACK -> return PackHolder(layoutInflater.inflate(R.layout.item_steam_app_pack, parent, false))
        }
        return null
    }

    override fun getItemCount(): Int = data.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEADER -> (holder as HeaderHolder).bindData()
            TYPE_GAME, TYPE_DLC -> (holder as GameHolder).bindData(data[position - 1])
            TYPE_PACK -> (holder as PackHolder).bindData(data[position - 1])
        }
    }

}

class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData() = with(itemView) {
        mTvDate.text = TransManager.tranTmp.date?.toYMD()
        mTvBuyer.text = TransManager.tranTmp.buyerId
        mTvOwner.text = TransManager.tranTmp.ownerId
        mTvDate.onClick {
            val calendar = Calendar.getInstance()
            val datePickDialog = DatePickerDialog(getContext(),
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        LogX.d("$year-$month-$dayOfMonth")
                        TransManager.tranTmp.date = DateUtils.getDateByYMD(year, month, dayOfMonth)
                        mTvDate.text = TransManager.tranTmp.date?.toYMD()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
            datePickDialog.show()
        }
        mBtnBuyer.onClick {
            val inputDialog = InputDialog(getContext())
            inputDialog.show(R.string.buyer_name, object : InputDialog.InputDialogInterface {
                override fun onContentConfirm(content: String) {
                    if (content == "") {
                        getContext().toast(R.string.name_null_warn)
                        return
                    }
                    mTvBuyer.text = content
                }

            }, mTvBuyer.text.toString())
        }
        mBtnOwner.onClick {
            val inputDialog = InputDialog(getContext())
            inputDialog.show(R.string.owner_name, object : InputDialog.InputDialogInterface {
                override fun onContentConfirm(content: String) {
                    if (content == "") {
                        getContext().toast(R.string.name_null_warn)
                        return
                    }
                    mTvOwner.text = content
                }
            }, mTvOwner.text.toString())
        }
        mBtnTransExtraMsg.onClick {
            val inputDialog = InputDialog(getContext())
            inputDialog.show(R.string.edit_msg, object : InputDialog.InputDialogInterface {
                override fun onContentConfirm(content: String) {
                    mTvTransExtraMsg.text = content
                }
            }, mTvTransExtraMsg.text.toString())
        }
    }
}

class GameHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindData(steamApp: SteamApp) = with(itemView) {
        mIvHeader.loadImg(steamApp.logoImgUrl)
        mTvGameName.text = steamApp.name
        mTvDlcBadge.setVisible(steamApp.type == SteamConstants.TYPE_DLC)

        if (mEtPriceInit.text == null || mEtPriceInit.text.toString() == "") {
            mEtPriceInit.setText(steamApp.initPrice.toFloatYuan2())
        }
        mEtPriceInit.onTextChange(object : TextChangeHandler {
            override fun onTextChange(s: Editable?) {
                if (s == null || s.toString() == "") {
                    steamApp.initPrice = null
                } else {
                    steamApp.initPrice = (s.toString().toFloat() * 100).toInt()
                }
            }

        })
        if (mEtPriceFinal.text == null || mEtPriceFinal.text.toString() == "") {
            mEtPriceFinal.setText(steamApp.purchasedPrice.toFloatYuan2())
        }
        mEtPriceFinal.onTextChange(object : TextChangeHandler {
            override fun onTextChange(s: Editable?) {
                if (s == null || s.toString() == "") {
                    steamApp.purchasedPrice = null
                } else {
                    steamApp.purchasedPrice = (s.toString().toFloat() * 100).toInt()
                }
            }
        })
    }
}

class PackHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(steamApp: SteamApp) = with(itemView) {
        mTvPackName.text = steamApp.name
        if (steamApp.games != null) {
            mTvAppsCount.text = String.format(context.getString(R.string.pack_contains_count), steamApp.games?.size)
        }
        mTvPackPriceInit.deleteLine().money = steamApp.initPrice
        mTvPackPriceFinal.money = steamApp.purchasedPrice
        mBtnEditPrice.onClick {
            getContext().toast("修改价格")
        }
        var content = ""
        val lastIndex = steamApp.games?.lastIndex
        steamApp.games?.forEachIndexed {
            index, app ->
            content += "${app.name} ${app.initPrice.toYuanStr()} - ${app.type}"
            if (index != lastIndex) {
                content += "\n"
            }
        }
        mTvApps.text = content
    }
}