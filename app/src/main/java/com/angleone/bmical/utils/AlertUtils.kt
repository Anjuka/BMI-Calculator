package com.angleone.bmical.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object AlertUtils {
    fun showAlertDialog(context: Context, title:String, message: String, confirmBtn:String, cancelBtn:String?, confirmAction:()->Unit){
        val alertDialogBuilder = AlertDialog.Builder(context)

        // 设置弹窗标题
        alertDialogBuilder.setTitle(title)

        // 设置弹窗消息
        alertDialogBuilder.setMessage(message)

        // 设置积极按钮
        alertDialogBuilder.setPositiveButton(confirmBtn, DialogInterface.OnClickListener { dialog, which ->
            // 点击确定按钮的处理逻辑
            confirmAction()
            dialog.dismiss()
        })

        if (cancelBtn==null){
            alertDialogBuilder.setCancelable(false)
        }else{
            // 设置消极按钮
            alertDialogBuilder.setNegativeButton(cancelBtn, DialogInterface.OnClickListener { dialog, which ->
                // 点击取消按钮的处理逻辑
                dialog.dismiss()
            })
        }

        // 创建并显示弹窗
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}