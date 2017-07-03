package com.seta.setall.common.http

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by SETA_WORK on 2017/7/3.
 */
class NetworkInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response? {
//        val original: Request = chain.request()
//        val request = original.newBuilder().build()
        val response = chain.proceed(chain.request()) ?: throw IOException("Response null.")
        return response
    }

}