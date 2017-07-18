package com.seta.setall.steam.db

import com.seta.setall.common.extensions.clear
import com.seta.setall.common.extensions.toVarargArray
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import org.jetbrains.anko.db.insert

/**
 * Created by SETA_WORK on 2017/7/14.
 */
class SteamDb(val dbHelper: SteamDbHelper = SteamDbHelper.instance,
              val steamDbMapper: SteamDbMapper = SteamDbMapper()) {

    companion object {
        val instance by lazy { SteamDb() }
    }

    fun saveTransactions(allTransactions: List<Transaction>) = dbHelper.use {
        clear(TransActionTable.TABLE_NAME)

        allTransactions.forEach {
            //保存订单条目
            with(steamDbMapper.convertTransFromDomain(it)) {
                insert(TransActionTable.TABLE_NAME, *map.toVarargArray())
                steamApps.forEach {
                    insert(SteamAppTable.TABLE_NAME, *map.toVarargArray())
                    //保存关系
                    val relation = TransAppRelation(transId, it.appId)
                    insert(TransAppRelationTable.TABLE_NAME, *relation.map.toVarargArray())
                }
            }
        }
    }

    fun saveSteamApps(games: List<SteamApp>) = dbHelper.use {
        clear(SteamAppTable.TABLE_NAME)
        games.forEach {
            with(steamDbMapper.convertAppFromDomain(it)) {
                insert(SteamAppTable.TABLE_NAME, *map.toVarargArray())
            }
        }
    }
}