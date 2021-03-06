package com.itemstudio.luen.utils;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    private static PackageManager pm;
    private static CertificateFactory certificateFactory;

    private static long dataSize, cacheSize;

    public static double getApkSize(PackageInfo packageInfo) {
        File apkFile = new File(packageInfo.applicationInfo.publicSourceDir);
        return Double.parseDouble(formatToMegaBytes(apkFile.length()));
    }

    public static double getDataSize(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        try {
            Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(pm, packageName, new IPackageStatsObserver.Stub() {

                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    dataSize = pStats.dataSize;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Double.parseDouble(formatToMegaBytes(dataSize));
    }

    public static double getCacheSize(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        try {
            Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(pm, packageName, new IPackageStatsObserver.Stub() {

                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    cacheSize = pStats.cacheSize;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Double.parseDouble(formatToMegaBytes(cacheSize));
    }

    public static String formatToMegaBytes(long bytes) {
        long megabytes = 1024 * 1024;
        return floatForm((double)bytes / megabytes);
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }

    public static SimpleDateFormat getDateFormatter() {
        return new SimpleDateFormat("dd MMMM yyyy (HH:mm:ss)", Locale.getDefault());
    }

    public static X509Certificate[] getX509Certificates(Context context, String packageName) {
        X509Certificate[] certs = null;
        if (pm == null)
            pm = context.getApplicationContext().getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (certificateFactory == null)
                certificateFactory = CertificateFactory.getInstance("X509");
            certs = new X509Certificate[pkgInfo.signatures.length];
            for (int i = 0; i < certs.length; i++) {
                byte[] cert = pkgInfo.signatures[i].toByteArray();
                InputStream inStream = new ByteArrayInputStream(cert);
                certs[i] = (X509Certificate) certificateFactory.generateCertificate(inStream);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return certs;
    }

    public static File getDefaultAppFolder() {
        return new File(Environment.getExternalStorageDirectory() + "/Luen");
    }

    public static boolean extractAPK(PackageInfo packageInfo, Context context) {
        boolean result = false;

        Log.d("Luen", packageInfo.applicationInfo.sourceDir);

        File installLocation = new File(packageInfo.applicationInfo.sourceDir);
        File outputLocation = new File(getDefaultAppFolder().getPath() + "/" +
                packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString() + ".apk");

        try {
            FileUtils.copyFile(installLocation, outputLocation);
            result = true;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
