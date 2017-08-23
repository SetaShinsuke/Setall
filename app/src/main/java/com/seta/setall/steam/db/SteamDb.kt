package com.seta.setall.steam.db

import android.content.Context
import com.seta.setall.common.extensions.clear
import com.seta.setall.common.extensions.parseList
import com.seta.setall.common.extensions.toVarargArray
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select

/**
 * Created by SETA_WORK on 2017/7/14.
 */
class SteamDb(val dbHelper: SteamDbHelper = SteamDbHelper.instance,
              val steamDbMapper: SteamDbMapper = SteamDbMapper()) {

    companion object {
        val instance by lazy { SteamDb() }
    }

    fun saveTransaction(transaction: Transaction) = dbHelper.use {
        with(steamDbMapper.convertTransFromDomain(transaction)) {
            //保存交易记录
            val rowId = insert(TransActionTable.TABLE_NAME, *map.toVarargArray())
            LogX.d("Insert tran , row id : $rowId")
            //保存每个游戏
            steamAppDbs.forEach {
                saveApp(rowId.toInt(), it)
            }
        }
    }

    fun saveTransactions(allTransactions: List<Transaction>) = dbHelper.use {
        clear(TransActionTable.TABLE_NAME)

        allTransactions.forEach {
            //保存订单条目
            with(steamDbMapper.convertTransFromDomain(it)) {
                //保存交易记录
                insert(TransActionTable.TABLE_NAME, *map.toVarargArray())
                //保存每个游戏
                steamAppDbs.forEach {
                    saveApp(transId, it)
                }
            }
        }
    }

    //    private fun saveApp(transId: Int?, steamAppDb: SteamAppDb): Unit = dbHelper.writableDatabase.transaction {
    private fun saveApp(transId: Int? = null, steamAppDb: SteamAppDb): Unit = dbHelper.use {
        with(steamAppDb) {
            replace(SteamAppTable.TABLE_NAME, *map.toVarargArray())
            //保存关系
            if (transId != null) {
                insert(TransAppRelationTable.TABLE_NAME, *TransAppRelation(transId, appId).map.toVarargArray())
            }
            games?.forEach {
                insert(BundleAppRelationTable.TABLE_NAME, *BundleAppRelation(appId, it.appId).map.toVarargArray())
                saveApp(null, it)
            }
        }
    }

    fun saveAllGames(games: List<SteamApp>) = dbHelper.use {
        clear(SteamAppTable.TABLE_NAME)
        games.forEach {
            with(steamDbMapper.convertAppFromDomain(it)) {
                insert(SteamAppTable.TABLE_NAME, *map.toVarargArray())
            }
        }
    }


    fun findAllApps(callback: (List<SteamApp>) -> Unit) = dbHelper.use {
        val firres = select(SteamAppTable.TABLE_NAME)
        val result = firres.parseList {
            SteamAppDb(HashMap(it.mapValues {
                if(it.value is Long && (it.value as Long) < Int.MAX_VALUE) {
                    return@mapValues (it.value as Long).toInt()
                }else if(it.value is Unit){
                    return@mapValues null
                }else{
                    return@mapValues it.value
                }
            }), null)
        }
        callback(result.map {
            steamDbMapper.convertAppsToDomain(it)
        })
    }

    fun export(context: Context) = UtilMethods.exportDb(context, SteamDbHelper.STEAM_DB_NAME)
}