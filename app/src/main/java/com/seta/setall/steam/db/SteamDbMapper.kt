package com.seta.setall.steam.db

import com.seta.setall.steam.domain.models.SteamApp
import com.seta.setall.steam.domain.models.Transaction
import java.util.*

/**
 * Created by SETA_WORK on 2017/7/18.
 */
class SteamDbMapper {

    //region Domain -> Db
    fun convertTransFromDomain(transaction: Transaction) = with(transaction) {
        val allGames = steamApps.map {
            convertAppFromDomain(it)
        }
        TransactionDb(transId, date?.time, buyerId, ownerId, extraMsg, allGames)
    }

    fun convertAppFromDomain(steamApp: SteamApp): SteamAppDb = with(steamApp) {
        val containedGames = games?.map {
            convertAppFromDomain(it)
        }
        SteamAppDb(appId, name, currency, initPrice, purchasedPrice, purchasedDate?.time, type, iconImgUrl, logoImgUrl, containedGames)
    }
    //endregion

    //region Db -> Domain
    fun convertTransToDomain(transactionDb: TransactionDb) = with(transactionDb) {
        val gamesOfTrans = steamAppDbs.map {
            convertAppsToDomain(it)
        }
        var dateParam: Date? = null
        if (date is Long) {
            dateParam = Date(date as Long)
        }
        Transaction(transId, dateParam, buyerName, ownerName, extraMsg, gamesOfTrans)
    }

    fun convertAppsToDomain(steamApp: SteamAppDb): SteamApp = with(steamApp) {
        val gamesInBundle = games?.map {
            convertAppsToDomain(it)
        }
        val date: Date?
        if (purchasedDate == null) {
            date = null
        } else {
            date = Date(purchasedDate as Long)
        }
        SteamApp(appId, name, currency, initPrice, purchasedPrice, date, type, iconImgUrl, logoImgUrl, gamesInBundle)
    }
    //endregion
}