package com.seta.setall.steam.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.seta.setall.common.framework.BaseApplication
import com.seta.setall.common.logs.LogX
import org.jetbrains.anko.db.*

/**
 * Created by SETA_WORK on 2017/7/14.
 * SQLiteOpenHelper for Steam
 */
class SteamDbHelper(ctx: Context = BaseApplication.instance) : ManagedSQLiteOpenHelper(ctx, STEAM_DB_NAME, null, STEAM_DB_VERSION) {
    companion object {
        val STEAM_DB_NAME = "steam.db"
        val STEAM_DB_VERSION = 1
        val instance by lazy { SteamDbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase) {
        LogX.d("Database onCreate().")
        db.createTable(TransActionTable.TABLE_NAME, true,
                TransActionTable.TRANS_ID to INTEGER + PRIMARY_KEY,
                TransActionTable.DATE to INTEGER,
                TransActionTable.BUYER_ID to TEXT,
                TransActionTable.OWNER_ID to TEXT)
        db.createTable(SteamAppTable.TABLE_NAME, true,
                SteamAppTable.APP_ID to INTEGER + PRIMARY_KEY,
                SteamAppTable.NAME to TEXT,
                SteamAppTable.CURRENCY to TEXT,
                SteamAppTable.INIT_PRICE to INTEGER,
                SteamAppTable.PURCHASED_PRICE to INTEGER,
                SteamAppTable.PURCHASED_DATE to INTEGER,
                SteamAppTable.TYPE to INTEGER
        )
        db.createTable(TransAppRelationTable.TABLE_NAME, true,
                TransAppRelationTable.TRANS_ID to INTEGER,
                TransAppRelationTable.APP_ID to INTEGER,
                FOREIGN_KEY(TransAppRelationTable.TRANS_ID, TransActionTable.TABLE_NAME, TransActionTable.TRANS_ID),
                FOREIGN_KEY(TransAppRelationTable.APP_ID, SteamAppTable.TABLE_NAME, SteamAppTable.APP_ID)
        )
        db.createTable(BundleAppRelationTable.TABLE_NAME,true,
                BundleAppRelationTable.PACK_APP_ID to INTEGER,
                BundleAppRelationTable.GAME_ID to INTEGER,
                FOREIGN_KEY(BundleAppRelationTable.PACK_APP_ID,SteamAppTable.TABLE_NAME,SteamAppTable.APP_ID),
                FOREIGN_KEY(BundleAppRelationTable.GAME_ID,SteamAppTable.TABLE_NAME,SteamAppTable.APP_ID)
                )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        LogX.d("Database onUpgrade, from $oldVersion to $newVersion.")
    }


}