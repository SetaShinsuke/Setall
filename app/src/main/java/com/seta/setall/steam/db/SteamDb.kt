package com.seta.setall.steam.db

import android.content.Context
import com.google.gson.Gson
import com.seta.setall.common.extensions.clear
import com.seta.setall.common.extensions.parseList
import com.seta.setall.common.extensions.toVarargArray
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.mvp.writeFile
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import rx.Observable

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


    //todo:返回Observable？
    fun findAllApps(vararg types: String,
                    doObserve: Boolean = false,
            //                    callback: ((Observable<List<SteamApp>>) -> Unit)? = null) = dbHelper.use {
                    callback: ((List<SteamApp>) -> Unit)? = null) = dbHelper.use {
        var selectReq = "${SteamAppTable.TYPE} = ? "
        val columns = select(SteamAppTable.TABLE_NAME)
        var colFiltered = columns
        if (types.isNotEmpty()) {
            types.forEach {
                selectReq += "OR ${SteamAppTable.TYPE} = ? "
            }
            colFiltered = columns.whereSimple(selectReq, *types)
        }
        val result: List<SteamAppDb> = colFiltered.parseList {
            SteamAppDb(HashMap(it.mapValues {
                if (it.value is Long && (it.value as Long) < Int.MAX_VALUE) {
                    return@mapValues (it.value as Long).toInt()
                } else if (it.value is Unit) {
                    return@mapValues null
                } else {
                    return@mapValues it.value
                }
            }), null)
        }

        if (doObserve) {
            return@use Observable.just(result)
        }
        val toRet = result.map {
            steamDbMapper.convertAppsToDomain(it)
        }
//        callback?.invoke(Observable.just(toRet))
        callback?.invoke(toRet)
        return@use toRet
    }

    fun export(context: Context) = UtilMethods.exportDb(context, SteamDbHelper.STEAM_DB_NAME)

    fun backUp(context: Context, path: String = "apps_bkp${System.currentTimeMillis()}.json") {
        findAllApps {
            apps ->
            val appTableJson = Gson().toJson(apps)
            writeFile(path, appTableJson)
        }
    }
}