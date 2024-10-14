package com.angleone.bmical.webview

import android.content.Context
import android.webkit.JavascriptInterface
import com.appsflyer.AppsFlyerLib
import com.angleone.bmical.MainApplication
import com.angleone.bmical.event.Events
import com.just.agentweb.AgentWeb
import org.json.JSONObject


class JsInterfaceAW(val agent: AgentWeb, val context: Context) {

    @JavascriptInterface
    fun postMessage( name:String, data:String){

        val eventValues: MutableMap<String, Any> = HashMap()
        if (data.isNotBlank()){
            try {
                JSONObject(data).let {
                    for (key in it.keys()){
                        eventValues[key] = it.get(key)
                    }
                }
            } catch (_:Exception){

            }
        }

        when(name){
            //登录
            Events.LOGIN -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.LOGIN, eventValues
                )
            }
            //登出
            Events.LOGOUT ->{
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.LOGOUT, eventValues
                )
            }
            //点击注册
            Events.REGISTER_CLICK ->{
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.REGISTER_CLICK, eventValues
                )
            }
            //注册成功
            Events.REGISTER -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.REGISTER, eventValues
                )
            }
            //点击充值
            Events.RECHARGE_CLICK -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.RECHARGE_CLICK, eventValues
                )
            }
            //首充成功
            Events.FIRST_RECHARGE -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.FIRST_RECHARGE, eventValues
                )
            }
            //复充成功
            Events.RECHARGE -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.RECHARGE, eventValues
                )
            }
            //进入游戏
            Events.ENTERGAME -> {
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.ENTERGAME, eventValues
                )
            }
            Events.SHOP ->{
                AppsFlyerLib.getInstance().logEvent(
                    MainApplication.instance.applicationContext,
                    Events.SHOP, eventValues
                )
            }
        }
    }
}