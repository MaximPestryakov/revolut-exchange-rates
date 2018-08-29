package com.github.maximpestryakov.revolut

import android.app.Application
import android.os.Build
import android.os.Looper
import android.os.StrictMode
import com.github.maximpestryakov.revolut.di.AppModule
import com.squareup.leakcanary.LeakCanary
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        initStrictMode()
        super.onCreate()
        initTimber()
        initRxAndroid()
        initDi()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .apply {
                    detectNetwork()
                    detectCustomSlowCalls()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        detectResourceMismatches()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectUnbufferedIo()
                    }
                }
                .penaltyLog()
                .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .apply {
                    detectLeakedSqlLiteObjects()
                    detectActivityLeaks()
                    detectLeakedClosableObjects()
                    detectLeakedRegistrationObjects()
                    detectFileUriExposure()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        detectCleartextNetwork()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectContentUriWithoutPermission()
                    }
                }
                .penaltyLog()
                .build())
        }
    }

    private fun initLeakCanary(): Boolean {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return false
        }
        LeakCanary.install(this)
        return true
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initRxAndroid() {
        val mainScheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { mainScheduler }
    }

    private fun initDi() {
        startKoin(listOf(AppModule))
    }
}
