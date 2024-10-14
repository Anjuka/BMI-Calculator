package com.angleone.bmical

import android.app.Application
import com.appsflyer.AppsFlyerLib

class MainApplication : Application() {
    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        AppsFlyerLib.getInstance().init("fVRmEN59hCLfKQafpzqCMV",null,this)
        AppsFlyerLib.getInstance().start(this)
    }
}