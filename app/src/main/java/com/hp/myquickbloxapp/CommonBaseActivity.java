package com.hp.myquickbloxapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.snackbar.Snackbar;
import com.hp.myquickbloxapp.vedio_audio_call.CallActivity;
import com.hp.myquickbloxapp.vedio_audio_call.CallService;
import com.hp.myquickbloxapp.vedio_audio_call.Consts;
import com.hp.myquickbloxapp.vedio_audio_call.LoginService;
import com.hp.myquickbloxapp.vedio_audio_call.QBResRequestExecutor;
import com.hp.myquickbloxapp.vedio_audio_call.SharedPrefsHelper;
import com.hp.myquickbloxapp.vedio_audio_call.ToastUtils;
import com.hp.myquickbloxapp.vedio_audio_call.WebRtcSessionManager;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.hp.myquickbloxapp.vedio_audio_call.Consts.EXTRA_LOGIN_RESULT_CODE;


public class CommonBaseActivity extends AppCompatActivity {
    private static final int SELECTED_IMAGE = 1;
    private static final int WRITE_EXT_STORAGE = 3;
    private static final int CAMERA_DATA = 0;
    private static final int PICK_BAITRYOPTIMIZATION_REQUEST = 3;
    public static int mIndexforImages;
    static String mIMEI;
    private final int REQUEST_CAMERA = 0;
    private final int READ_EXT_STORAGE = 4;
    private final int PERMISSION_CALLBACK_CONSTANT = 100;
    private final int REQUEST_PERMISSION_SETTING = 101;
    public AppPreference prefs;
    public boolean isErrorResponse;
    public String imagePath_one = null;
    public Uri outputFileUri;
    String getDates;
    String dateTime;
    int permissionREAD_CONTACTS;
    AlertDialog.Builder alertDialogBuilder;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE

    };
    private SharedPreferences permissionStatus;
    private Bitmap bitmap = null;
    private String[] path;



    private SharedPrefsHelper sharedPrefsHelper;
    private String TAG = MainActivity.class.getSimpleName();
    private QBUser userForSave;
    protected QBResRequestExecutor requestExecutor;
    private ProgressDialog progressDialog;


    public static Dialog noInternetConnection(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to Network");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
        return alertDialog;
    }

    public static Dialog noGpsEnabledDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);

            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }

    public static String getIMEI(Context mContext) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
            // mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

            mIMEI = deviceId;
        } catch (Exception e1) {
            System.out.println("exception " + e1);
        }
        return mIMEI;
    }

    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressWarnings("deprecation")
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        //try {
        ExifInterface ei = getPictureData(context, selectedImage);
        if (ei == null)
            return img;
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static ExifInterface getPictureData(Context context, Uri uri) throws IOException {
        String[] uriParts = uri.toString().split(":");
        String path;

        if (uriParts[0].equals("content")) {
            String col = MediaStore.Images.ImageColumns.DATA;
            Cursor c = context.getContentResolver().query(uri,
                    new String[]{col},
                    null, null, null);
            if (c != null && c.moveToFirst()) {
                path = c.getString(c.getColumnIndex(col));
                c.close();
                return new ExifInterface(path);
            }

        } else if (uriParts[0].equals("file")) {
            path = uri.getEncodedPath();
            return new ExifInterface(path);
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        prefs = new AppPreference(getApplicationContext());
        isErrorResponse = false;
    }

    public <T> void processResponse(T result) {
        isErrorResponse = false;
        if (result == null) {
            isErrorResponse = true;
            return;
        }
    }
    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    captureImage();

                } else {
                    grantPermissionsByDialogue();
                }
                break;

            case READ_EXT_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent j = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(j, SELECTED_IMAGE);
                    alertDialogBuilder.setCancelable(true);
                } else {
                    grantPermissionsByDialogue();
                }

                break;

            case WRITE_EXT_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onPickImage();
                } else {
                    grantPermissionsByDialogue();
                }
                break;
            case PERMISSION_CALLBACK_CONSTANT:
                boolean allgranted = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        allgranted = true;
                    } else {
                        allgranted = false;
                        break;
                    }
                }
                if (allgranted) {
                   // proceedAfterPermission();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[3])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[4])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[5])
                        || ActivityCompat.shouldShowRequestPermissionRationale(CommonBaseActivity.this, permissionsRequired[6])
                ) {
                    //  //txtPermissions.setText("Permissions Required");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CommonBaseActivity.this);
                    builder.setTitle("Need Multiple Permissions");
                    builder.setMessage("This app needs Camera and Location permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(CommonBaseActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }

                break;


            default:
                break;

        }

    }

    private void grantPermissionsByDialogue() {
        new AlertDialog.Builder(CommonBaseActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Grant Permissions")
                .setCancelable(false)
                .setMessage("Please click Grant Button to allow permission")
                .setPositiveButton("Grant Permissions", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    public void onPickImage() {

        if (permissionREAD_CONTACTS != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CommonBaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_STORAGE);
        } else {
            captureImageDialog(0);
        }


    }

    private void captureImageDialog(int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("select_image");
        builder.setItems(
                new CharSequence[]{"CAMERA", "GALLERY"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                int permissionCheck = ContextCompat.checkSelfPermission(CommonBaseActivity.this, Manifest.permission.CAMERA);
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(CommonBaseActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                } else {
                                    captureImage();
                                }
                                break;
                            case 1:
                                int permissionCheck_receve = ContextCompat.checkSelfPermission(CommonBaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                                if (permissionCheck_receve != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(CommonBaseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXT_STORAGE);
                                    //TODO
                                } else {
                                    Intent j = new Intent(
                                            Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(j, SELECTED_IMAGE);
                                    builder.setCancelable(true);
                                }

                                break;

                        }
                    }
                });
        builder.create().show();
        mIndexforImages = index;
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            outputFileUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_DATA);
        } else {
            Toast.makeText(this, "error_no_camera", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Uri uri;
        switch (requestCode) {

            case EXTRA_LOGIN_RESULT_CODE:
                hideProgressDialog();
                boolean isLoginSuccess = intent.getBooleanExtra(Consts.EXTRA_LOGIN_RESULT, false);
                String errorMessage = intent.getStringExtra(Consts.EXTRA_LOGIN_ERROR_MESSAGE);

                if (isLoginSuccess) {
                    saveUserData(userForSave);
                    signInCreatedUser(userForSave);
                } else {
                    ToastUtils.longToast(getString(R.string.login_chat_login_error) + errorMessage);

                }
                break;


        }
    }





    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline(context)) {
                Toast.makeText(context, "on", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "off", Toast.LENGTH_SHORT).show();
            }
        }


        public boolean isOnline(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        }

    }





    /*vedio and call view code start from hear*/



    public   void  getVedioCalllogin(){
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        requestExecutor = MyApplication.getInstance().getQbResRequestExecutor();
        if (sharedPrefsHelper.hasQbUser()) {
            LoginService.start(CommonBaseActivity.this, sharedPrefsHelper.getQbUser());

        } else {
            // LoginActivity.start(MainActivity.this);
            userForSave = createUserWithEnteredData();
            startSignUpNewUser(userForSave);
        }
    }


    private void startSignUpNewUser(final QBUser newUser) {
        Log.d(TAG, "SignUp New User");
        showProgressDialog(R.string.dlg_creating_new_user);
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d(TAG, "SignUp Successful");
                        saveUserData(newUser);
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.d(TAG, "Error SignUp" + e.getMessage());
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser);
                        } else {
                            hideProgressDialog();
                            ToastUtils.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }

    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(MyApplication.USER_DEFAULT_PASSWORD);
        userForSave = qbUser;
        startLoginService(qbUser);
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData("vinaysingh0038793",
                "singh003893");
    }



    private QBUser createQBUserWithCurrentData(String userLogin, String userFullName) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userLogin) && !TextUtils.isEmpty(userFullName)) {
            qbUser = new QBUser();
            qbUser.setLogin(userLogin);
            qbUser.setFullName(userFullName);
            qbUser.setPassword(MyApplication.USER_DEFAULT_PASSWORD);
        }
        return qbUser;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private void signInCreatedUser(final QBUser qbUser) {
        Log.d(TAG, "SignIn Started");
        requestExecutor.signInUser(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle params) {
                Log.d(TAG, "SignIn Successful");
                sharedPrefsHelper.saveQbUser(userForSave);
                updateUserOnServer(qbUser);
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.d(TAG, "Error SignIn" + responseException.getMessage());
                hideProgressDialog();
                ToastUtils.longToast(R.string.sign_in_error);
            }
        });
    }

    private void updateUserOnServer(QBUser user) {
        user.setPassword(null);
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                // OpponentsActivity.start(LoginActivity.this);
               // finish();
            }
            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                ToastUtils.longToast(R.string.update_user_error);
            }
        });
    }

    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }

    public void startCallnew(boolean isVideoCall) {
        ArrayList<Integer> opponentsIds = new ArrayList<>();
        //opponentsIds.add(97603390);
        //97756670
         // opponentsIds.add(97756670);
         opponentsIds.add(97754585);
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        Log.d(TAG, "conferenceType = " + conferenceType);
        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsIds, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);
        CallActivity.start(this, false);
    }


    void showProgressDialog(@StringRes int messageId) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
            progressDialog.setOnKeyListener(keyListener);
        }
        progressDialog.setMessage(getString(messageId));
        progressDialog.show();
    }

    void hideProgressDialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException ignored) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isIncomingCall = SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_INCOMING_CALL, false);
        if (isCallServiceRunning(CallService.class)) {
            Log.d(TAG, "CallService is running now");
            CallActivity.start(this, isIncomingCall);
        }
        clearAppNotifications();

    }

    private boolean isCallServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void clearAppNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    /*vedio and call view code start from hear*/


}
