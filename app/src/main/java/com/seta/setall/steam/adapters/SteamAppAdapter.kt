package com.seta.setall.steam.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seta.setall.R
import com.seta.setall.common.extensions.setVisible
import com.seta.setall.common.views.InputDialog
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.extensions.loadImg
import kotlinx.android.synthetic.main.item_steam_app_game.view.*
import kotlinx.android.synthetic.main.item_steam_app_pack.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by SETA_WORK on 2017/8/7.
 */
class SteamAppAdapter(var data: List<SteamApp> = ArrayList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_GAME = 0
        val TYPE_DLC = 1
        val TYPE_PACK = 2
    }

    override fun getItemViewType(position: Int): Int = when (data[position].type) {
        SteamConstants.TYPE_UNKNOWN -> TYPE_GAME
        SteamConstants.TYPE_GAME -> TYPE_GAME
        SteamConstants.TYPE_DLC -> TYPE_DLC
        SteamConstants.TYPE_BUNDLE_PACK -> TYPE_PACK
        else -> super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_GAME, TYPE_DLC -> {
                val view = layoutInflater.inflate(R.layout.item_steam_app_game, parent, false)
                return GameHolder(view)
            }
            TYPE_PACK -> return PackHolder(layoutInflater.inflate(R.layout.item_steam_app_pack, parent, false))
        }
        return null
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            TYPE_GAME, TYPE_DLC -> (holder as GameHolder).bindData(data[position])
            TYPE_PACK -> (holder as PackHolder).bindData(data[position])
        }
    }

}

class GameHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindData(steamApp: SteamApp) = with(itemView) {
        mIvHeader.loadImg(steamApp.logoImgUrl)
        mTvGameName.text = steamApp.name
        mTvDlcBadge.setVisible(steamApp.type == SteamConstants.TYPE_DLC)
        mTvExtraMsg.onClick {
            //备注
            InputDialog(getContext()).show(R.string.edit_msg, object : InputDialog.InputDialogInterface {
                override fun onContentConfirm(content: String) {
                    mTvExtraMsg.text = content
                }

            })
        }
    }
}

class PackHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(steamApp: SteamApp) = with(itemView) {
        mIvPackHeader.loadImg(steamApp.logoImgUrl)
        mTvPackName.text = steamApp.name
        var content = ""
        val lastIndex = steamApp.games?.lastIndex
        steamApp.games?.forEachIndexed {
            index, app ->
            content += app.name
            if (index != lastIndex) {
                content += "\n"
            }
        }
        mTvApps.text = content
    }
}