package com.angleone.bmical.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.angleone.bmical.config.FIXED_URL
import com.angleone.bmical.config.HOST_URL
import com.angleone.bmical.R
import com.angleone.bmical.config.URL_PARAM
import com.angleone.bmical.utils.logd
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient

class WebViewActivity : AppCompatActivity() {

    //定义合约请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            logd("已授权应用权限")
            Toast.makeText(this, "已授权应用权限", Toast.LENGTH_SHORT).show()
        } else {
            // TODO: Inform user that that your app will not show notifications.
            logd("未授权应用权限")
            Toast.makeText(this, "未授权应用权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

//    private fun loadUrl(): String? {
//        val url = URL(CONFIG_FILE_URL)
//        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//        connection.requestMethod = "GET"
//
//        val responseCode = connection.responseCode
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            val reader = BufferedReader(InputStreamReader(connection.inputStream))
//            val response = StringBuilder()
//            var line: String?
//            while (reader.readLine().also { line = it } != null) {
//                response.append(line)
//            }
//            reader.close()
//
//            logd("Response: $response")
//            //response是一个json，转为Map对象
//            try {
//                JSONObject(response.toString()).let {
//                    val host = it.getString("host")
//                    connection.disconnect()
//                    return host
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        } else {
//            logd("HTTP request failed with status code $responseCode")
//        }
//
//        connection.disconnect()
//        return null
//    }

//    private fun loadUrlWithBlock(action: (url: String) -> Unit) {
//        //开启一个线程
//        GlobalScope.launch(Dispatchers.IO) {
//            val url = loadUrl()
//            // 切换回主线程来更新 UI
//            withContext(Dispatchers.Main) {
//                // 在这里更新 UI 或处理网络请求结果
//                if (url == null) {
//                    AlertUtils.showAlertDialog(
//                        this@WebViewActivity,
//                        "错误",
//                        "无法从服务器获取到Server",
//                        "重试",
//                        null
//                    ) {
//                        loadUrlWithBlock(action)
//                    }
//                } else {
//                    action(url)
//                }
//            }
//        }
//
//    }

    lateinit var webViewHandle: WebViewHandle
    lateinit var mAgentWeb: AgentWeb

    @SuppressLint("SetJavaScriptEnabled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webViewHandle = WebViewHandle(this)

        askNotificationPermission()

        val preAgentWeb: AgentWeb.PreAgentWeb =
            AgentWeb.with(this) //传入Activity  在Fragment中使用Activity和Fragment两个都传
                .setAgentWebParent(
                    this.findViewById(R.id.main_container),
                    LinearLayout.LayoutParams(-1, -1)
                ) //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .useDefaultIndicator(-1, 1) // 使用默认进度条
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //WebView安全类型
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl()
                .setWebChromeClient(webViewHandle.mWebChromeClient)
                .setWebViewClient(webViewHandle.mWebViewClient)
                .createAgentWeb()
                .ready()

        //如果FIXED_URL不为空，则使用固定URL
        if (FIXED_URL.isNotBlank()) {
            mAgentWeb = preAgentWeb.go(FIXED_URL)
            mAgentWeb.webCreator.webView.settings.javaScriptEnabled = true
            mAgentWeb.jsInterfaceHolder.addJavaObject("jsBridge", JsInterfaceAW(mAgentWeb, this))
        }else{
            //从https://fhuang.s3.ap-east-1.amazonaws.com/1010取出要访问的url路径
            //写一个HTTP访问去请求
            //拿到返回的url路径
            //然后用AgentWeb去访问
//            loadUrlWithBlock { url ->
                mAgentWeb = preAgentWeb.go(HOST_URL + URL_PARAM)
                mAgentWeb.webCreator.webView.settings.javaScriptEnabled = true
                mAgentWeb.jsInterfaceHolder.addJavaObject("jsBridge", JsInterfaceAW(mAgentWeb, this))
//            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mAgentWeb.back()) {
//                finish()
            }
            val allowBack = intent.getIntExtra("allowBack",0)
            if (allowBack==1){
                finish()
            }
            return true
        }
        return false
    }

    val FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 9901
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == webViewHandle.mUploadMessageForAndroid5) return
            val result =
                if (intent == null || resultCode != AppCompatActivity.RESULT_OK) null else intent.data
            if (result != null) {
                webViewHandle.mUploadMessageForAndroid5!!.onReceiveValue(arrayOf(result))
            } else {
                webViewHandle.mUploadMessageForAndroid5!!.onReceiveValue(arrayOf())
            }
            webViewHandle.mUploadMessageForAndroid5 = null
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }
}