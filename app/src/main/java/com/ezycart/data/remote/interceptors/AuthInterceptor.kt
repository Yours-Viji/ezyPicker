package com.ezycart.data.remote.interceptors

import com.ezycart.data.datastore.PreferencesManager
import com.ezycart.presentation.common.data.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val authToken = runBlocking {
            preferencesManager.getAuthToken()
        }

        val xAuthToken = runBlocking {
            preferencesManager.getXAuthToken()
        }

        val jwtToken = runBlocking {
            preferencesManager.getJwtToken()
        }

        val appMode = runBlocking {
            preferencesManager.getAppMode()
        }

        val requestBuilder = originalRequest.newBuilder()

        authToken?.takeIf { it.isNotBlank() }?.let { token ->
            requestBuilder.header(Constants.AUTHORIZATION, "Bearer $token")
        }

        xAuthToken?.takeIf { it.isNotBlank() }?.let { token ->
            requestBuilder.header(Constants.X_AUTHORIZATION, "Bearer $token")
        }

        jwtToken?.takeIf { it.isNotBlank() }?.let { token ->
            requestBuilder.header("jwt-authorization", "Bearer $token")
        }

        requestBuilder.header(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
        requestBuilder.header(Constants.ACCEPT, Constants.APPLICATION_JSON)
        requestBuilder.header(Constants.API_KEY, Constants.API_KEY_VALUE)
        requestBuilder.header(Constants.DEVICE_TYPE, Constants.DEVICE_TYPE_VALUE)
        requestBuilder.header(Constants.ACCEPTED_LANGUAGE, "English")
        requestBuilder.header("deviceLocale", "English")
        requestBuilder.header(Constants.DEVICE_ID, Constants.deviceId)
        requestBuilder.header(Constants.DEVICE_OS_VERSION, android.os.Build.VERSION.RELEASE)
        requestBuilder.header(Constants.DEVICE_BRAND, android.os.Build.BRAND)
        requestBuilder.header(Constants.APP_VERSION, "1.0.0")
        requestBuilder.header(Constants.BUILD_NO, "01")

        //requestBuilder.header("jwt-authorization", "Bearer ${Constants.jwtToken}")
        requestBuilder.header("appMode", appMode.name)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}