package com.seta.setall.steam.db

import com.seta.setall.common.logs.LogX

/**
 * Created by SETA_WORK on 2017/7/14.
 */
class SteamDb(val dbHelper: SteamDbHelper = SteamDbHelper.instance) {

    fun testDb() {
        LogX.d("TestDb, name : ${dbHelper.databaseName}")
    }
}