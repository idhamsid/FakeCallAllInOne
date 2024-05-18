package com.javadroid.fakecall.config;

import android.app.Activity;
import android.app.ProgressDialog;

public class DialogWait {
    private final  Activity activity;
    private ProgressDialog pd;

    public DialogWait(Activity myActivity) {
        activity = myActivity;
    }

    public void Show(String tittle){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        pd = progressDialog;
        progressDialog.setMessage(tittle);//"Please wait...."
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
    }

    public void Dismiss(){
        if (!(pd ==null)){
            pd.dismiss();
        }
    }
}
