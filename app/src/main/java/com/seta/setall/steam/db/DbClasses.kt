package com.seta.setall.steam.db

/**
 * 持久化保存已库中游戏的信息
 * 与 Domain 层的区别:
 *      1.包含一个map, 便于存储到数据库;
 *      2.不是 data 类;
 */
class TransactionDb(val map: MutableMap<String, Any?>, val steamAppDbs: List<SteamAppDb>) {
    var transId: Int by map
    var date: Long by map
    var buyerName: String by map
    var ownerName: String by map
    var extraMsg: String? by map

    constructor(transId: Int, date: Long, buyerId: String, ownerId: String, extraMsg: String?, steamAppDbs: List<SteamAppDb>) : this(HashMap(), steamAppDbs) {
        this.transId = transId
        this.date = date
        this.buyerName = buyerId
        this.ownerName = ownerId
        this.extraMsg = extraMsg
    }
}

/**
 * game 与 bundlePack 统一为 SteamAppDb
 * @param games bundle 中包含的所有游戏，可为空(type 不是 bundlePack 时)
 */
class SteamAppDb(val map: MutableMap<String, Any?>, val games: List<SteamAppDb>?) {
    var appId: Int by map
    var name: String by map
    var currency: String by map //币种
    var initPrice: Int by map //原价
    var purchasedPrice: Int by map //购入价格
    var purchasedDate: Long by map //购买日期 inMills
    var type: Int by map //0:game, 1:dlc, 2:bundlePack
    var iconImgId: String by map
    var logoImgId: String by map

    constructor(appId: Int, name: String, currency: String, initPrice: Int, purchasedPrice: Int, purchasedDate: Long, type: Int,
                iconImgId: String, logoImgId: String,
                games: List<SteamAppDb>?)
            : this(HashMap(), games) {
        this.appId = appId
        this.name = name
        this.currency = currency
        this.initPrice = initPrice
        this.purchasedPrice = purchasedPrice
        this.purchasedDate = purchasedDate
        this.type = type
        this.iconImgId = iconImgId
        this.logoImgId = logoImgId
    }
}

class TransAppRelation(val map: MutableMap<String, Any?>) {
    var transId: Int by map
    var appId: Int by map

    constructor(transId: Int, appId: Int) : this(HashMap()) {
        this.transId = transId
        this.appId = appId
    }
}

class BundleAppRelation(val map: MutableMap<String, Any?>) {
    var packId: Int by map
    var gameId: Int by map

    constructor(packId: Int, gameId: Int) : this(HashMap()) {
        this.packId = packId
        this.gameId = gameId
    }
}
