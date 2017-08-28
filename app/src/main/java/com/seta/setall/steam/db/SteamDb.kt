package com.seta.setall.steam.db

import android.content.Context
import com.seta.setall.common.extensions.clear
import com.seta.setall.common.extensions.parseList
import com.seta.setall.common.extensions.toVarargArray
import com.seta.setall.common.extensions.varyByDb
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import rx.Observable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    fun saveTransactions(allTransactions: List<Transaction>) = allTransactions.forEach { saveTransaction(it) }

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

    /**
     * 同步方法，但是提供回调
     * @return Observable<List<SteamAppDb>>
     */
    fun findAllApps(vararg types: String,
                    callback: ((List<SteamApp>) -> Unit)? = null): Observable<List<SteamAppDb>> = dbHelper.use {
        var selectReq = "${SteamAppTable.TYPE} = ? "
        val columns = select(SteamAppTable.TABLE_NAME)
        var colFiltered = columns
        if (types.isNotEmpty()) {
            repeat(types.lastIndex) {
                selectReq += "OR ${SteamAppTable.TYPE} = ? "
            }
            LogX.d("Apps select req: $selectReq, types: ${types.toList()}")
            colFiltered = columns.whereSimple(selectReq, *types)
        }
        val result: List<SteamAppDb> = colFiltered.parseList {
            //todo:恢复关系表 bundle-app
            SteamAppDb(HashMap(it.varyByDb()), null)
        }

        val toRet = result.map {
            steamDbMapper.convertAppsToDomain(it)
        }
        callback?.invoke(toRet)
        return@use Observable.just(result)
    }

    fun findAppById(appId: String) = dbHelper.use {
        val req = "${}"
    }

    fun findTransActions(minDate: Date? = null,
                         maxDate: Date? = null,
                         callback: ((List<Transaction>) -> Unit)? = null): Observable<List<Transaction>>
            = dbHelper.use {
        val columns = select(TransActionTable.TABLE_NAME)
        var columnsFiltered = columns
        var selectReq: String = ""
        val paramArray = ArrayList<String>()
        minDate?.let {
            selectReq += " ${TransActionTable.DATE} >= ${it.time} "
            paramArray.add(it.time.toString())
        }
        if (selectReq.isNotBlank()) {
            selectReq += " OR "
        }
        maxDate?.let {
            selectReq += " ${TransActionTable.DATE} <= ${it.time} "
            paramArray.add(it.time.toString())
        }
        LogX.d("Transactions req: $selectReq, Params: ${paramArray.toTypedArray().toList()}")
        //查找
        columnsFiltered = columns.whereSimple(selectReq, *(paramArray.toTypedArray()))
        val result: List<TransactionDb> = columnsFiltered.parseList {
            //todo:恢复关系表 trans-app
            TransactionDb(HashMap(it.varyByDb()), ArrayList<SteamAppDb>())
        }
        val toRet = result.map { steamDbMapper.convertTransToDomain(it) }
        callback?.invoke(toRet)
        LogX.d("Find all transactions : $toRet")
        return@use Observable.just(toRet)
    }

    fun findAppsByTransId(transId: Int,
                          callback: ((List<SteamApp>) -> Unit)) = dbHelper.use {
        val req = " ${TransAppRelationTable.TRANS_ID} = ?"
        val relations = select(TransAppRelationTable.TABLE_NAME)
                .whereSimple(req, transId.toString())
                .parseList {
                    TransAppRelation(HashMap(it.varyByDb()))
                }


    }


    fun export(context: Context) = UtilMethods.exportDb(context, SteamDbHelper.STEAM_DB_NAME)

    fun backUp(context: Context, path: String = "/apps_bkp${System.currentTimeMillis()}.json") = dbHelper.use {
        val observableApps: Observable<List<SteamAppDb>> = findAllApps()
    }
//    {
//        findAllApps {
//            apps ->
//            val appTableJson = Gson().toJson(apps)
//            writeFile(SteamConstants.STEAM_DIR, path, appTableJson)
//        }
//    }
}