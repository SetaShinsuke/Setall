package com.seta.setall.steam.db

object TransActionTable {
    val TABLE_NAME = "Transactions"
//    val ID = "_id"
    val TRANS_ID = "transId"
    val DATE = "date"
    val BUYER_ID = "buyerId"
    val OWNER_ID = "ownerId"
}

object SteamAppTable {
    val TABLE_NAME = "SteamApp"
//    val ID = "_id"
    val APP_ID = "appId"
    val NAME = "name"
    val CURRENCY = "currency"
    val INIT_PRICE = "initPrice"
    val PURCHASED_PRICE = "purchasedPrice"
    val PURCHASED_DATE = "purchasedDate"
    val TYPE = "type"
}

object TransAppRelationTable {
    val TABLE_NAME = "TransAppRelationTable"
//    val ID = "_id"
    val TRANS_ID = "transId"
    val APP_ID = "appId"
}

