package com.seta.setall.steam.db

object TransActionTable {
    val TABLE_NAME = "Transactions"
    //    val ID = "_id"
    val TRANS_ID = "transId"
    val DATE = "date"
    val BUYER_NAME = "buyerName"
    val OWNER_NAME = "ownerName"
    val EXTRA_MSG = "extraMsg"
}

object SteamAppTable {
    val TABLE_NAME = "SteamAppDb"
    //    val ID = "_id"
    val APP_ID = "appId"
    val NAME = "name"
    val CURRENCY = "currency"
    val INIT_PRICE = "initPrice"
    val PURCHASED_PRICE = "purchasedPrice"
    val PURCHASED_DATE = "purchasedDate"
    val TYPE = "type"
    val ICON_ID = "iconImgId"
    val LOGO_ID = "logoImgId"
}

object TransAppRelationTable {
    val TABLE_NAME = "TransAppRelationTable"
    //    val ID = "_id"
    val TRANS_ID = "transId"
    val APP_ID = "appId"
}

object BundleAppRelationTable {
    val TABLE_NAME = "BundleAppRelationTable"
    val PACK_APP_ID = "packId"
    val GAME_ID = "gameId"
}
