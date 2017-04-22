package com.joshua.a51bike.activity.control;

import android.util.Log;

import com.joshua.a51bike.Interface.DialogInterface;

/**
 * dialog 的控制类
 *
 * Created by wangqiang on 2017/1/9.
 */

public class DialogControl {
    private String TAG = "MarginAlerDialog";
    private static DialogControl dialogControl = new DialogControl();
    private DialogInterface dialog;

    public static DialogControl getDialogControl() {
        return dialogControl;
    }

    public void setDialog(DialogInterface dialogInterface) {
        if (null != dialogInterface) {
            dialogInterface.myCancel();
        }
        this.dialog = dialogInterface;
    }

    public DialogInterface getDialog() {
        return dialog;
    }

    public void show() {
        Log.i(TAG, "show: show ----------------");
        dialog.myShow();
    }

    public void cancel() {
        Log.i(TAG, "cancel: cancel--------------- ");
        dialog.myCancel();
    }
}
