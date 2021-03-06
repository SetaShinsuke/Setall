package com.seta.setall.steam.api

/**
 * Created by SETA_WORK on 2017/7/3.
 */
class SteamConstants {

    companion object {
        val STEAM_API_HOST = "http://api.steampowered.com/"
        val STEAM_STORE_API_HOST = "http://store.steampowered.com/"

        val STEAM_STORE_APP_URL = "http://store.steampowered.com/app/"
        val STEAM_API_KEY = "40A665497280D522561D5DA4F8E14C0D"
        val STEAM_PRF_NAME = "steam"

        val STEAM_DIR = "/setall_steam"
        val DB_BACKUP_FILE_NAME = "setall_steam.stm"
        val STEAM_USER_ID = "steam_user_id"

        //Steam App 类型
        val TYPE_UNKNOWN = "unknown"
        val TYPE_GAME = "game"
        val TYPE_DLC = "dlc"
        val TYPE_BUNDLE_PACK = "bundle_pack"

        val CODE_SELECT_GAMES = 100 //给订单添加游戏
        val CODE_SELECT_DLCS = 101 //给订单添加DLC
        val CODE_SELECT_PACKS = 102 //给订单添加package

        val SELECTED_IDS = "SELECTED_IDS"
        val GAME_IDS = "GAME_IDS"
        val PACK_IDS = "PACK_IDS"
    }
}
