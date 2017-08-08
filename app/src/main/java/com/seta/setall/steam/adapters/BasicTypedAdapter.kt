package com.seta.setall.steam.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by SETA_WORK on 2017/8/7.
 */
class BasicTypedAdapter<D : BasicTypeInterface>(var data: List<D>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val typeMap = HashMap<String, Int>()

    override fun getItemViewType(position: Int): Int {
        return data[position].viewType
    }

    override fun getItemCount(): Int {
        return data.map { it.viewType }.distinct().size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface BasicTypeInterface {
    val viewType: Int
}
