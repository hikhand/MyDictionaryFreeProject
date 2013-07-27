package com.hister.mydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by khaled on 7/27/13.
 */
public class Upgrade {

//
//    public void upgrade(Context context) {
//        LayoutInflater inflater = context.getLayoutInflater();
//        dialogLogin = new AlertDialog.Builder(this)
//                .setView(inflater.inflate(R.layout.dialog_signup, null))
//                .setPositiveButton(R.string.login,
//                        new Dialog.OnClickListener() {
//                            public void onClick(DialogInterface d, int which) {
//                            }
//                        })
//                .setNegativeButton(R.string.signUp, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogSignUp();
//                    }
//                })
//                .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK &&
//                                event.getAction() == KeyEvent.ACTION_UP &&
//                                !event.isCanceled()) {
//                            Backup.super.onBackPressed();
//                            return true;
//                        }
//                        return false;
//                    }
//                })
//                .create();
//        dialogLogin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        dialogLogin.show();
//
//        TextView tvHeader = (TextView) dialogLogin.findViewById(R.id.tvHeader);
//        tvHeader.setText("Login");
//
//        etEmail = (EditText) dialogLogin.findViewById(R.id.etEmail);
//        etUsername = (EditText) dialogLogin.findViewById(R.id.etUsername);
//        etPassword = (EditText) dialogLogin.findViewById(R.id.etPassword);
//
//        etEmail.setVisibility(View.GONE);
//        etUsername.setHint("Enter Your Username");
//        etPassword.setHint("Enter Your Password");
//
//        dialogLogin.setCanceledOnTouchOutside(false);
//        Button theButton = dialogLogin.getButton(DialogInterface.BUTTON_POSITIVE);
//        theButton.setOnClickListener(new CustomListenerLogin(dialogLogin));
//    }


}
