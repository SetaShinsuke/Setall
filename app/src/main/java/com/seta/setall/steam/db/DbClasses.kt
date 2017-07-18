package com.seta.setall.steam.db

/**
 * 持久化保存已库中游戏的信息
 * 与 Domain 层的区别:
 *      1.包含一个map, 便于存储到数据库;
 *      2.不是 data 类;
 */
class Transaction(val map: MutableMap<String, Any?>, val steamApps: List<SteamApp>) {
    var transId: Long by map
    var date: Long by map
    var buyerId: String by map
    var ownerId: String by map

    constructor(transId: Long, date: Long, buyerId: String, ownerId: String, steamApps: List<SteamApp>) : this(HashMap(), steamApps) {
        this.transId = transId
        this.date = date
        this.buyerId = buyerId
        this.ownerId = ownerId
    }
}

/**
 * game 与 bundlePack 统一为 SteamApp
 * @param games bundle 中包含的所有游戏，可为空(type 不是 bundlePack 时)
 */
class SteamApp(val map: MutableMap<String, Any?>, val games: List<SteamApp>?) {
    var appId: Long by map
    var name: String by map
    var currency: String by map //币种
    var initPrice: Int by map //原价
    var purchasedPrice: Int by map //购入价格
    var purchasedDate: Long by map //购买日期 inMills
    var type: Int by map //0:game, 1:dlc, 2:bundlePack

    //todo:保存封面图
//    var iconImgId by map
//    var logoImgId by map

    constructor(appId: Long, name: String, currency: String, initPrice: Int, purchasedPrice: Int, purchasedDate: Long, type: Int, games: List<SteamApp>?)
            : this(HashMap(), games) {
        this.appId = appId
        this.name = name
        this.currency = currency
        this.initPrice = initPrice
        this.purchasedPrice = purchasedPrice
        this.purchasedDate = purchasedDate
        this.type = type
    }
}

class TransAppRelation(val map: MutableMap<String, Any?>) {
    var transId by map
    var appId by map

    constructor(transId: Long, appId: Long) : this(HashMap()) {
        this.transId = transId
        this.appId = appId
    }
}
