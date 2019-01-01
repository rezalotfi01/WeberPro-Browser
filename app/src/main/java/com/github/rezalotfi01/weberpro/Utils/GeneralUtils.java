package com.github.rezalotfi01.weberpro.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created  on 09/22/2016.
 */
public class GeneralUtils {

    public GeneralUtils() {
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static int getRandomIntNumber(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public static boolean isRooted(){
        boolean isRoot = false;
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    // TODO Code to run on success
                    isRoot = true;
                }
                else {
                    // TODO Code to run on unsuccessful
                    isRoot = false;
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                isRoot = false;
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            isRoot = false;
        }

        return isRoot;
    }

//    public static void changeMobileDataConnection(Context context , boolean enable) {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //only for root users
//                StringBuilder command = new StringBuilder();
//                command.append("su -c ");
//                command.append("service call phone ");
//                command.append(getTransactionCode(context) + " ");
//                if (Build.VERSION.SDK_INT >= 22) {
//                    SubscriptionManager manager = SubscriptionManager.from(context);
//                    int id = 0;
//                    if (manager.getActiveSubscriptionInfoCount() > 0)
//                        id = manager.getActiveSubscriptionInfoList().get(0).getSubscriptionId();
//                    command.append("i32 ");
//                    command.append(String.valueOf(id) + " ");
//                }
//                command.append("i32 ");
//                command.append(enable ? "1" : "0");
//                command.append("\n");
//                Runtime.getRuntime().exec(command.toString());
//
//            }else {
//                final ConnectivityManager conman = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
//                dataMtd.setAccessible(enable);
//                dataMtd.invoke(conman, enable);
//            }
//
//        }catch(Exception e){
//                Log.e("Weber TAG", "changeMobileDataConnection Exception : " + e.toString());
//        }
//    }
//    private static String getTransactionCode(Context context) {
//        try {
//            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            Class telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
//            Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
//            getITelephonyMethod.setAccessible(true);
//            Object ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
//            Class ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
//
//            Class stub = ITelephonyClass.getDeclaringClass();
//            Field field = stub.getDeclaredField("TRANSACTION_setDataEnabled");
//            field.setAccessible(true);
//            return String.valueOf(field.getInt(null));
//        } catch (Exception e) {
//            if (Build.VERSION.SDK_INT >= 22)
//                return "86";
//            else if (Build.VERSION.SDK_INT == 21)
//                return "83";
//        }
//        return "";
//    }

    public static void changeWiFiConnection(Context context, boolean enable){
        try {
            WifiManager wManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wManager.setWifiEnabled(enable);
        }catch (Exception e){
            Log.e("Weber TAG", "change WiFi Connection Exception : " + e.toString());
        }
    }

    public static void openFile(AppCompatActivity context, String filename) {
        File file = new File(filename);
        Log.e("Weber Tag", "openFile File Address : "+filename);
        // Get URI and MIME type of file
        //String packageString = context.getApplication().getPackageName() + ".fileprovider";
        //Uri uri = FileProvider.getUriForFile(context,packageString,file);
        //Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        //String mime = context.getContentResolver().getType(uri);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(filename);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

}