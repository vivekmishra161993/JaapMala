package com.mtt.presentation.ui.screens.whats_new

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionProvider @Inject constructor( @ApplicationContext private val context: Context) {

    fun getVersionCode(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .longVersionCode
                .toInt()
        } else {
            @Suppress("DEPRECATION")
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionCode
        }
    }

    fun getVersionName(): String {
        return context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: ""
    }
}
