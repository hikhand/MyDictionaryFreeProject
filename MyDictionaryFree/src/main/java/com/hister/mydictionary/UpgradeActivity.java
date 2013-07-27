package com.hister.mydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeActivity extends Activity {
    SharedPreferences UserInfo;
    SharedPreferences.Editor EditorUserInfo;

    AlertDialog dialogLogin;
    AlertDialog dialogSingUp;
    AlertDialog dialogAskLogin;
    EditText etEmail;
    EditText etUsername;
    EditText etPassword;
    TextView tvUsername;

    FTPClient con;

    String userEmail = "";
    String userUsername = "";
    String userPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        setElementsId();

        if (userUsername.equals("")) {
            dialogAskLogin();
        }
    }


    void setElementsId() {
        dialogLogin = new AlertDialog.Builder(this).create();
        dialogSingUp = new AlertDialog.Builder(this).create();
        dialogAskLogin = new AlertDialog.Builder(this).create();
        UserInfo = getSharedPreferences("userInfo", 0);
        EditorUserInfo = UserInfo.edit();

        userUsername = UserInfo.getString("userUsername", "");
        userPassword = UserInfo.getString("userPassword", "");
        tvUsername = (TextView) findViewById(R.id.tvUsernameUpgrade);
        tvUsername.setText("Logged in as: " + userUsername);
    }

    void loggedIn() {
        setElementsId();
    }


    void dialogAskLogin() {
        dialogAskLogin = new AlertDialog.Builder(this)
                .setMessage("to create backup you need to login or create an account.")
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogLogin();
                    }
                })
                .setNegativeButton(R.string.signUp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogSignUp();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            UpgradeActivity.super.onBackPressed();
                            return true;
                        }
                        return false;
                    }
                })
                .create();
        dialogAskLogin.show();
        dialogAskLogin.setCanceledOnTouchOutside(false);
    }




    void dialogLogin() {
        LayoutInflater inflater = this.getLayoutInflater();
        dialogLogin = new AlertDialog.Builder(this)
                .setView(inflater.inflate(R.layout.dialog_signup, null))
                .setPositiveButton(R.string.login,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                            }
                        })
                .setNegativeButton(R.string.signUp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogSignUp();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            UpgradeActivity.super.onBackPressed();
                            return true;
                        }
                        return false;
                    }
                })
                .create();
        dialogLogin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialogLogin.show();

        TextView tvHeader = (TextView) dialogLogin.findViewById(R.id.tvHeader);
        tvHeader.setText("Login");

        etEmail = (EditText) dialogLogin.findViewById(R.id.etEmail);
        etUsername = (EditText) dialogLogin.findViewById(R.id.etUsername);
        etPassword = (EditText) dialogLogin.findViewById(R.id.etPassword);

        etEmail.setVisibility(View.GONE);
        etUsername.setHint("Enter Your Username");
        etPassword.setHint("Enter Your Password");

        dialogLogin.setCanceledOnTouchOutside(false);
        Button theButton = dialogLogin.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListenerLogin(dialogLogin));
    }

    class CustomListenerLogin implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListenerLogin(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {

            class FtpTask extends AsyncTask<Void, Integer, Void> {
                EditText etUsernameL = (EditText) dialogLogin.findViewById(R.id.etUsername);
                EditText etPasswordL = (EditText) dialogLogin.findViewById(R.id.etPassword);
                String strUsername = etUsernameL.getText().toString();
                String strPassword = etPasswordL.getText().toString();
                String strEmail = "";

                boolean succeed = false;
                String error = "";
                ProgressDialog progressBar;
                private Context context;
                String canFind = "";
                String password = "";

                public FtpTask(Context context) { this.context = context; }

                protected void onPreExecute()
                {
                    progressBar = new ProgressDialog(context);
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Connecting to server ...");
                    progressBar.show();
                }

                protected Void doInBackground(Void... args) {
                    try {
                        con = new FTPClient();
                        con.connect(InetAddress.getByName("5.9.0.183"));

                        if (con.login("windowsp", "KHaledBLack73")) {
                            con.enterLocalPassiveMode(); // important!

                            publishProgress(0);
                            boolean canAdd = con.makeDirectory(File.separator+"MyDictionary"+File.separator+"backups"+File.separator+strUsername);
                            if (canAdd) {
                                con.removeDirectory(File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername);
                                canFind = "no such user";
                            } else {
                                canFind = "successful";

                                InputStream inputStream = con.retrieveFileStream(File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername + File.separator + "userPassword");
                                con.completePendingCommand();
                                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                                password = r.readLine();
                                inputStream.close();
                                r.close();

                                inputStream = con.retrieveFileStream(File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername + File.separator + "userEmail");
                                con.completePendingCommand();
                                r = new BufferedReader(new InputStreamReader(inputStream));
                                strEmail = r.readLine();
                                inputStream.close();
                                r.close();
                            }
                            succeed = true;
                        } else {
//                            Toast.makeText(Backup.this, "couldn't connect to server", Toast.LENGTH_SHORT).show();
                        }

                        con.logout();
                        con.disconnect();

                    } catch (Exception e) {
                        error = e.toString();
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Void result) {
                    Log.v("FTPTask", "FTP connection complete");
                    if (canFind.equals("successful")) {
                        if (strPassword.equals(password)) {
                            EditorUserInfo.putString("userUsername", strUsername);
                            EditorUserInfo.putString("userPassword", strPassword);
                            EditorUserInfo.putString("userEmail", strEmail);
                            EditorUserInfo.commit();
                            try {
                                FileOutputStream outputStream;
                                outputStream = openFileOutput("userUsername", Context.MODE_PRIVATE);
                                outputStream.write(strUsername.getBytes());
                                outputStream.close();

                                outputStream = openFileOutput("userPassword", Context.MODE_PRIVATE);
                                outputStream.write(strPassword.getBytes());
                                outputStream.close();

                                outputStream = openFileOutput("userEmail", Context.MODE_PRIVATE);
                                outputStream.write(strEmail.getBytes());
                                outputStream.close();
                            } catch (IOException e) {
//                                Toast.makeText(Backup.this, e.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            dialogLogin.dismiss();
                            Toast.makeText(UpgradeActivity.this, "you successfully logged in.", Toast.LENGTH_SHORT).show();
                            loggedIn();
                        } else {
                            Toast.makeText(UpgradeActivity.this, "password is wrong, try again", Toast.LENGTH_SHORT).show();
                        }
                    } else if (canFind.equals("no such user")) {
                        Toast.makeText(UpgradeActivity.this, "username is wrong, try again", Toast.LENGTH_SHORT).show();

                    } else if (canFind.equals("unsuccessful")) {
                        Toast.makeText(UpgradeActivity.this, "process ran into a problem.", Toast.LENGTH_SHORT).show();
                    } else if (!succeed) {
                        Toast.makeText(UpgradeActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    progressBar.dismiss();
                }

                protected void onProgressUpdate(Integer... args) {
                    if (args[0] == 0)
                        progressBar.setMessage("Matching details ...");
                }
            }
            new FtpTask(UpgradeActivity.this).execute();
        }
    }


    void dialogSignUp() {
        LayoutInflater inflater = this.getLayoutInflater();
        dialogSingUp = new AlertDialog.Builder(this)
                .setView(inflater.inflate(R.layout.dialog_signup, null))
                .setPositiveButton(R.string.create,
                        new Dialog.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                            }
                        })
                .setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogLogin();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            UpgradeActivity.super.onBackPressed();
                            return true;
                        }
                        return false;
                    }
                })
                .create();
        dialogSingUp.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialogSingUp.show();

        TextView tvHeader = (TextView) dialogSingUp.findViewById(R.id.tvHeader);
        tvHeader.setText("Sign Up");

        etEmail = (EditText) dialogSingUp.findViewById(R.id.etEmail);
        etUsername = (EditText) dialogSingUp.findViewById(R.id.etUsername);
        etPassword = (EditText) dialogSingUp.findViewById(R.id.etPassword);

        etEmail.setHint("Enter your email address");
        etUsername.setHint("Enter a Username");
        etPassword.setHint("Enter a Password");

        dialogSingUp.setCanceledOnTouchOutside(false);
        Button theButton = dialogSingUp.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CustomListenerSignUp(dialogSingUp));
    }

    class CustomListenerSignUp implements View.OnClickListener {
        private final Dialog dialog;

        public CustomListenerSignUp(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {

            class FtpTask extends AsyncTask<Void, Integer, Void> {
                EditText etEmailS = (EditText) dialogSingUp.findViewById(R.id.etEmail);
                EditText etUsernameS = (EditText) dialogSingUp.findViewById(R.id.etUsername);
                EditText etPasswordS = (EditText) dialogSingUp.findViewById(R.id.etPassword);
                String strEmail = etEmailS.getText().toString();
                String strUsername = etUsernameS.getText().toString();
                String strPassword = etPasswordS.getText().toString();

                boolean succeed = false;
                String error = "";
                ProgressDialog progressBar;
                private Context context;
                String canCreate = "unsuccessful";

                public FtpTask(Context context) { this.context = context; }

                protected void onPreExecute()
                {
                    progressBar = new ProgressDialog(context);
                    progressBar.setCancelable(false);
                    progressBar.setMessage("Connecting to server ...");
                    progressBar.show();
                }

                protected Void doInBackground(Void... args) {
                    try {
                        con = new FTPClient();
                        con.connect(InetAddress.getByName("5.9.0.183"));
                        if (con.login("windowsp", "KHaledBLack73")) {
                            con.enterLocalPassiveMode(); // important!
                            publishProgress(0);
                            if (isValidEmail(strEmail) && strUsername.length() >= 3 && strPassword.length() >= 5 && !isIllegal(strUsername)) {
                                boolean canAdd = con.makeDirectory(File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername);
                                canCreate = canAdd ? "successful" : "taken userUsername";
                                if (canAdd) {
                                    FileOutputStream outputStream;
                                    outputStream = openFileOutput("userUsername", Context.MODE_PRIVATE);
                                    outputStream.write(strUsername.getBytes());
                                    outputStream.close();

                                    outputStream = openFileOutput("userPassword", Context.MODE_PRIVATE);
                                    outputStream.write(strPassword.getBytes());
                                    outputStream.close();

                                    outputStream = openFileOutput("userEmail", Context.MODE_PRIVATE);
                                    outputStream.write(strEmail.getBytes());
                                    outputStream.close();



                                    EditorUserInfo.putString("userUsername", strUsername);
                                    EditorUserInfo.putString("userPassword", strPassword);
                                    EditorUserInfo.putString("userEmail", strEmail);
                                    EditorUserInfo.commit();

                                    FileInputStream in = openFileInput("userPassword");
                                    String remote = File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername;
                                    con.storeFile(remote + File.separator + "userPassword", in);
                                    in.close();

                                    in = openFileInput("userEmail");
                                    remote = File.separator + "MyDictionary" + File.separator + "backups" + File.separator + strUsername;
                                    con.storeFile(remote + File.separator + "userEmail", in);
                                    in.close();


                                    succeed = true;
                                }
                            }
                        } else {
//                            Toast.makeText(Backup.this, "couldn't connect to server", Toast.LENGTH_SHORT).show();
                        }

                        con.logout();
                        con.disconnect();

                    } catch (Exception e) {
                        error = e.toString();
                        e.printStackTrace();
                    }
                    return null;
                }

                protected void onPostExecute(Void result) {
                    if (isValidEmail(strEmail) && strUsername.length() >= 3 && strPassword.length() >= 5 && !isIllegal(strUsername)) {
                        if (canCreate.equals("successful")) {
                            dialogSingUp.dismiss();
                            loggedIn();
                            Toast.makeText(UpgradeActivity.this, "your account successfully created.", Toast.LENGTH_SHORT).show();
                        } else if (canCreate.equals("taken userUsername")) {
                            Toast.makeText(UpgradeActivity.this, "this userUsername is taken choose another", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!isValidEmail(strEmail)) {
                            Toast.makeText(UpgradeActivity.this, "please enter an valid email address.", Toast.LENGTH_SHORT).show();
                        } else if (strUsername.length() < 3) {
                            Toast.makeText(UpgradeActivity.this, "lowest length for username is 3", Toast.LENGTH_SHORT).show();
                        } else if (strPassword.length() < 5) {
                            Toast.makeText(UpgradeActivity.this, "lowest length for password is 5", Toast.LENGTH_SHORT).show();
                        } else if (isIllegal(strUsername)) {
                            Toast.makeText(UpgradeActivity.this, "username cant contain '/'", Toast.LENGTH_SHORT).show();
                        } else if (!succeed) {
                            Toast.makeText(UpgradeActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.dismiss();
                }

                protected void onProgressUpdate(Integer... args) {
                    if (args[0] == 0) {
                        progressBar.setMessage("Creating your account ...");
                    }


                }
            }
            new FtpTask(UpgradeActivity.this).execute();
        }
    }

    boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    boolean isIllegal (String string) {
        for (char c : string.toCharArray()) {
            if (c == '/') {
                return true;
            }
        }
        return false;
    }






    public void btnCreateBackupOnServer_Click(View view) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        final String currentDateAndTime = simpleDateFormat.format(new Date());

        final File currentDBMain = new File(getDatabasePath("items.db"), "");
        final File currentDBLeitner = new File(getDatabasePath("leitner.db"), "");


        class FtpTask extends AsyncTask<Void, Integer, Void> {
            boolean succeed = false;
            String errorS= "";
            String error = "";
            ProgressDialog progressBar;
            private Context context;

            public FtpTask(Context context) { this.context = context; }

            protected void onPreExecute()
            {
                progressBar = new ProgressDialog(context);
                progressBar.setCancelable(false);
                progressBar.setMessage("Connecting to server ...");
                progressBar.show();
            }

            protected Void doInBackground(Void... args) {
                try {
                    con = new FTPClient();
                    con.connect(InetAddress.getByName("5.9.0.183"));

                    if (con.login("windowsp", "KHaledBLack73") && currentDBMain.exists()) {
                        con.enterLocalPassiveMode(); // important!
                        con.setFileType(FTP.BINARY_FILE_TYPE);
//                        FileInputStream inMain;
                        String userPath = File.separator + "MyDictionary" + File.separator + "backups" + File.separator + userUsername;

                        publishProgress(0);

                        con.storeFile(userPath + File.separator + "items " + currentDateAndTime + ".db", new FileInputStream(currentDBMain));
                        con.storeFile(userPath + File.separator + "leitner " + currentDateAndTime + ".db", new FileInputStream(currentDBLeitner));



                        FileOutputStream outputStream;

                        outputStream = openFileOutput("lastDateServer", Context.MODE_PRIVATE);
                        outputStream.write(currentDateAndTime.getBytes());
                        outputStream.close();

                        File databasePathMain = UpgradeActivity.this.getDatabasePath("items.db");
                        outputStream = openFileOutput("pathMainServer", Context.MODE_PRIVATE);
                        outputStream.write(databasePathMain.getAbsolutePath().getBytes());
                        outputStream.close();

                        File databasePathLeitner = UpgradeActivity.this.getDatabasePath("leitner.db");
                        outputStream = openFileOutput("pathLeitnerServer", Context.MODE_PRIVATE);
                        outputStream.write(databasePathLeitner.getAbsolutePath().getBytes());
                        outputStream.close();


                        con.storeFile(userPath + File.separator + "lastDateServer", openFileInput("lastDateServer"));

                        con.storeFile(userPath + File.separator + "pathMainServer", openFileInput("pathMainServer"));
                        con.storeFile(userPath + File.separator + "pathLeitnerServer", openFileInput("pathLeitnerServer"));


                        EditorUserInfo.putString("pathMainServer", databasePathMain.getAbsolutePath());
                        EditorUserInfo.putString("pathLeitnerServer", databasePathLeitner.getAbsolutePath());
                        EditorUserInfo.putString("lastDateServer", currentDateAndTime);
                        EditorUserInfo.commit();

                        succeed = true;
                    } else if (!currentDBMain.exists()) {
                        errorS = "there is nothing in database to create backup";
                    }

                    con.logout();
                    con.disconnect();

                } catch (Exception e) {
                    error = e.toString();
//                    Toast.makeText(Backup.this, e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                Log.v("FTPTask","FTP connection complete");
                if (succeed) {
                    Toast.makeText(UpgradeActivity.this, "backup successfully created.", Toast.LENGTH_SHORT).show();
                } else if (!errorS.equals("")) {
                    Toast.makeText(UpgradeActivity.this, errorS, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpgradeActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                progressBar.dismiss();
                //Where ftpClient is a instance variable in the main activity
            }

            protected void onProgressUpdate(Integer... args) {
                if (args[0] == 0)
                    progressBar.setMessage("Uploading to server ...");
            }

        }
        new FtpTask(this).execute();
    }


    public void btnCreateLocalBackup_Click(View view) {
        String error = "";
        String errorS= "";
        try {
            File sd = Environment.getExternalStorageDirectory();
            File pathMain = getDatabasePath("items.db");
            File pathLeitner = getDatabasePath("leitner.db");
            String s = File.separator;
            String backupPath = Environment.getExternalStorageDirectory() + s + "My Dictionary" + s + "backups" + s;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
            String currentDateAndTime = simpleDateFormat.format(new Date());

            File directory = new File(backupPath);
            directory.mkdirs();

            if (sd.canWrite() && new File(pathMain, "").exists() && new File(backupPath).exists()) {
                FileChannel src = new FileInputStream(pathMain).getChannel();
                FileChannel dst = new FileOutputStream(backupPath + "items " + currentDateAndTime + ".db").getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                src = new FileInputStream(pathLeitner).getChannel();
                dst = new FileOutputStream(backupPath + "leitner " + currentDateAndTime + ".db").getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                FileOutputStream outputStream;
                outputStream = openFileOutput("lastDateLocal", Context.MODE_PRIVATE);
                outputStream.write(currentDateAndTime.getBytes());
                outputStream.close();

                outputStream = openFileOutput("pathMainLocal", Context.MODE_PRIVATE);
                outputStream.write(pathMain.getAbsolutePath().getBytes());
                outputStream.close();

                outputStream = openFileOutput("pathLeitnerLocal", Context.MODE_PRIVATE);
                outputStream.write(pathLeitner.getAbsolutePath().getBytes());
                outputStream.close();

                outputStream = new FileOutputStream(backupPath + "lastDateLocal");
                outputStream.write(currentDateAndTime.getBytes());
                outputStream.close();

                outputStream = new FileOutputStream(backupPath + "pathMainLocal");
                outputStream.write(pathMain.getAbsolutePath().getBytes());
                outputStream.close();

                outputStream = new FileOutputStream(backupPath + "pathLeitnerLocal");
                outputStream.write(pathLeitner.getAbsolutePath().getBytes());
                outputStream.close();
            } else {
                errorS = "Can't access sd card";
            }
        } catch (Exception e) {
            error = e.toString();
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        final String errorF = error;
        final String errorSF= errorS;
        final ProgressDialog progressDialog = new ProgressDialog(UpgradeActivity.this);
        progressDialog.setMessage("Creating backup ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        class WaitTime extends AsyncTask<Void, Integer, Void> {
            protected Void doInBackground(Void... args) {
                long delayInMillis = 2500;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                    }
                }, delayInMillis);
                return null;
            }

            protected void onPostExecute(Void result) {
                if (errorSF.equals("")) {
                    Toast.makeText(getBaseContext(), "the operation was completed successfully", Toast.LENGTH_SHORT).show();
                }else if (!errorSF.equals("")) {
                    Toast.makeText(getBaseContext(), errorSF, Toast.LENGTH_SHORT).show();
                } else if (!errorF.equals("")) {
                    Toast.makeText(getBaseContext(), errorF, Toast.LENGTH_SHORT).show();
                }
            }

        }
        new WaitTime().execute();
    }


}
