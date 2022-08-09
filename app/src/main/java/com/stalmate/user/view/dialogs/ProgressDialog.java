package com.stalmate.user.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.stalmate.user.R;


public class ProgressDialog {
    public static final int DIALOG_CENTERED = 2;
    public static final int DIALOG_FULLSCREEN = 1;
    private Context context;
    private Dialog dialog;
    public static ProgressDialog progressDialog;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void showDialog(int dialogType) {
        switch (dialogType) {
            case 1:
                fullScreenDialog();
                return;
            case 2:
                centeredDialog();
                return;
            default:
                centeredDialog();
        }
    }

    private void fullScreenDialog() {
        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.dialog_progress_fullscreen);
        this.dialog.setCancelable(false);
        if (this.dialog.getWindow() != null) {
            this.dialog.getWindow().setBackgroundDrawable(null);
            this.dialog.getWindow().clearFlags(2);
            this.dialog.getWindow().setLayout(-1, -1);
        }
        this.dialog.show();
    }

    private void centeredDialog() {
        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.dialog_progress_centered);
        if (this.dialog.getWindow() != null) {
            this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.dialog.getWindow().setLayout(-2, -2);
        }
        this.dialog.setCancelable(false);

        if (!((Activity)context).isFinishing())
        this.dialog.show();
    }



    public boolean isShowing() {
        return this.dialog != null && this.dialog.isShowing();
    }

    public void dismiss() {
        try {
            this.dialog.dismiss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
