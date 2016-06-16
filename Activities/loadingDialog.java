package com.example.dalena.trackerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Dalena on 6/5/2016.
 */
public class loadingDialog extends AlertDialog {
    protected loadingDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.loading_layout);
    }

    @Override
    public  void hide() {
        super.hide();
        setContentView(R.layout.delivery_list_layout);
    }
}