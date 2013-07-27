package com.hister.mydictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by khaled on 7/24/13.
 */
public class AlertGoPro {
    public AlertGoPro() {

    }

    public void showDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Access Denied");
        builder.setMessage("For this feature you need THE PRO version, would you like to install it now ?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uriUrl = Uri.parse("market://details?id=ir.khaled.mydictionary");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                context.startActivity(launchBrowser);
                showDialog(context);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialog(context);
            }
        });
        AlertDialog dialogGoPro = builder.create();
        dialogGoPro.show();
        dialogGoPro.setCanceledOnTouchOutside(false);
    }

}
