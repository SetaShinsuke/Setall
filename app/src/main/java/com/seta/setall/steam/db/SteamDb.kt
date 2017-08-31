package com.seta.setall.steam.db

import android.content.Context
import com.google.gson.Gson
import com.seta.setall.common.extensions.*
import com.seta.setall.common.interfaces.ResultHandler
import com.seta.setall.common.logs.LogX
import com.seta.setall.common.utils.UtilMethods
import com.seta.setall.common.utils.readFile
import com.seta.setall.common.utils.writeFile
import com.seta.setall.steam.api.SteamConstants
import com.seta.setall.steam.api.models.TransRestoredBean
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

    fun saveApps(transId: Int, apps: List<SteamApp>) = apps.forEach { saveApp(transId, steamDbMapper.convertAppFromDomain(it)) }

    /**
     * 同步方法，但是提供回调
     * @return Observable<List<SteamAppDb>>
     */
    fun findAllApps(vararg types: String,
                    callback: ((List<SteamApp>) -> Unit)? = null): Observable<List<SteamApp>> = dbHelper.use {
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
        val result: List<SteamApp> = colFiltered.parseList {
            //todo:恢复关系表 bundle-app
            SteamAppDb(HashMap(it.varyByDb()), null)
        }.map { steamDbMapper.convertAppToDomain(it) } //List<SteamApp> 但是 games 必为空
                .map {
                    findAppById(it.appId)
                }.filterIsInstance<SteamApp>()
        LogX.d("Copy result : $result")
        callback?.invoke(result)
        return@use Observable.just(result)
    }

    fun findAppById(appId: Int): SteamApp? = dbHelper.use {
        val req = " ${SteamAppTable.APP_ID} = ?"
        val result: SteamApp? = select(SteamAppTable.TABLE_NAME)
                .whereSimple(req, appId.toString())
                .parseOpt {
                    //todo:恢复 pack-app 关系表
                    steamDbMapper.convertAppToDomain(SteamAppDb(HashMap(it.varyByDb()), null))
                }
        LogX.d("FindAppById type : ${result?.type}")
        if (result?.type == SteamConstants.TYPE_BUNDLE_PACK) {
            val gamesInPack: List<SteamApp> = findAppsInPack(appId)
            return@use result.copy(games = gamesInPack)
        }
        return@use result
    }

    fun findAppsInPack(packAppId: Int): List<SteamApp> = dbHelper.use {
        val req = " ${BundleAppRelationTable.PACK_APP_ID} = ? "
        val relations: List<BundleAppRelation> = select(BundleAppRelationTable.TABLE_NAME)
                .whereSimple(req, packAppId.toString())
                .parseList {
                    BundleAppRelation(HashMap(it.varyByDb()))
                }
        val apps: ArrayList<SteamApp> = ArrayList()
        relations.forEach {
            //查找 pack-app 关系时，阻断递归查询app的 pack-app 关系
            findAppById(it.gameId)?.let {
                apps.add(it)
            }
        }
        return@use apps
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
        val result: List<Transaction> = columnsFiltered.parseList {
            //恢复关系表 trans-app
            TransactionDb(HashMap(it.varyByDb()), ArrayList<SteamAppDb>())
        }.map {
            steamDbMapper.convertTransToDomain(it)
        }.map {
            transaction ->
            var transResult: Transaction = Transaction()
            findAppsByTransId(transaction.transId) {
                apps ->
                transResult = transaction.copy(steamApps = apps)
            }
            return@map transResult
        }
        callback?.invoke(result)
        LogX.d("Find all transactions : $result")
        return@use Observable.just(result)
    }

    fun findAppsByTransId(transId: Int?,
                          callback: ((List<SteamApp>) -> Unit)): Observable<List<SteamApp>> = dbHelper.use {
        val req = " ${TransAppRelationTable.TRANS_ID} = ?"
        val relations: List<TransAppRelation> = select(TransAppRelationTable.TABLE_NAME)
                .whereSimple(req, transId.toString())
                .parseList {
                    TransAppRelation(HashMap(it.varyByDb()))
                }
        val apps: ArrayList<SteamApp> = ArrayList()
        relations.forEach {
            findAppById(it.appId)?.let {
                apps.add(it)
            }
        }
        LogX.d("findAppsByTransId result apps : $apps")
        callback.invoke(apps)
        return@use Observable.just(apps)
    }


    fun export(context: Context): String? = UtilMethods.exportDb(context, SteamDbHelper.STEAM_DB_NAME)

    fun backUp(dir: String = SteamConstants.STEAM_DIR,
               path: String = "/trans_bkp${System.currentTimeMillis()}.json",
               callback: ((String) -> Unit)
    ) =
            SteamDb.instance.findTransActions {
                val toSave: TransRestoredBean = TransRestoredBean(it)
//                val restored: List<TransRestoredBean> = Gson().fromJson<List<TransRestoredBean>>(content)
//                LogX.d("Backup restore test : $restored")
                val result: String? = writeFile(dir, path, Gson().toJson(toSave))
                if (result != null) {
                    callback("导出成功!\n$result")
                } else {
                    callback("导出失败!请重试!")
                }
            }

    fun restoreFromFile(filePath: String?, resultHandler: ResultHandler) {
        val jsonStr: String? = readFile(filePath)
        jsonStr?.let {
            try {
                val transList: List<Transaction> = Gson().fromJson<TransRestoredBean>(jsonStr).transactions
                saveTransactions(transList.map { it.copy(transId = null) })
                resultHandler.onSuccess(transList)
            } catch (e: Exception) {
                resultHandler.onFail(e)
            }
        }
    }
}