package com.joshua.a51bike.activity.control;

import com.joshua.a51bike.Interface.DialogInterface;

/**
 * Created by wangqiang on 2017/1/9.
 */

public class DialogControl {
    private String TAG = "DialogControl";
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
        dialog.myShow();
    }

    public void cancel() {
        dialog.myCancel();
//        Log.e(TAG, "dialogInterface is be cancel and is  null ? " + (dialog.getClass()));
    }
}
