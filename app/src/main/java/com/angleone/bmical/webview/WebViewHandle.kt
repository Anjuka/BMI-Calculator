package com.angleone.bmical.webview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.angleone.bmical.utils.AlertUtils
import com.angleone.bmical.utils.logd
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

class WebViewHandle(val activity: AppCompatActivity) {

    val mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
            p1?.proceed()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url
            val scheme = url?.scheme
            if (scheme == "intent"){
                var path = url.path
                var fullUrl = url.toString()
                var intent = Intent.parseUri(fullUrl,Intent.URI_INTENT_SCHEME)
                var fallbackUrl = intent.getStringExtra("browser_fallback_url")
                if (view!=null&&fallbackUrl!=null)  view.loadUrl(fallbackUrl)
                return true
            }
            val host = url?.host
            logd("WebView-> shouldOverrideUrlLoading: $url $scheme $host")
            when (scheme) {
                "https" -> {
                    //仅过滤某些host进行判断是否跳转，也可不过滤
                    if ("www.weixin.com" == host) {
                    }
                }
                "telegram", "tg", "whatapps", "ins", "fb", "youtube", "twitter" -> {
                    gotoOtherAppBySchemeProtocol(url)
                }
                else -> {}
            }
            if (url.toString().endsWith(".apk")&&view!=null&&!view.url.isNullOrBlank()){
                //用chrome打开此页面
                logd("WebView-> 用chrome打开此页面: ${view.url}")
                openUrlInChrome(view.url!!)
            }
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            openFileChooserImplForAndroid5(filePathCallback!!)
            return true
        }
    }
    var mUploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    private fun openFileChooserImplForAndroid5(uploadMsg: ValueCallback<Array<Uri>>) {
        mUploadMessageForAndroid5 = uploadMsg
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "image/*"
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5)
    }

    val FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 9901


    private fun gotoOtherAppBySchemeProtocol(url: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            //通过直接处理抛出的ActivityNotFound异常来确保程序不会崩溃
            e.printStackTrace()
        }
    }

    private fun openUrlInChrome(url: String) {
        // 创建 Intent 并设置 action
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        // 设置 Chrome 浏览器的包名
        intent.setPackage("com.android.chrome")

        // 检查是否有 Chrome 浏览器可以处理 Intent
        if (intent.resolveActivity(ActivityUtils.getTopActivity().packageManager) != null) {
            ActivityUtils.startActivity(intent)
        } else {
            // 如果没有安装 Chrome 浏览器，可以提示用户或使用默认浏览器
            AlertUtils.showAlertDialog(ActivityUtils.getTopActivity(),"Chrome not installed","Please install Chrome first","Okay","Cancel") {
                val intent2 = Intent(Intent.ACTION_VIEW)
                intent2.addCategory(Intent.CATEGORY_BROWSABLE);
                intent2.setData(Uri.parse("https://www.google.com/chrome/"))
                ActivityUtils.startActivity(intent2)
            };
        }
    }
}