package com.itemstudio.luen.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.itemstudio.luen.R;
import com.itemstudio.luen.ui.widget.InfoLinearLayout;
import com.itemstudio.luen.ui.widget.SheetFloatingButton;
import com.itemstudio.luen.utils.Utils;
import com.tumblr.remember.Remember;

import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoViewActivity extends AppCompatActivity {

    PackageInfo packageInfo;

    //region INITIALIZATION VIEWS
    @Bind(R.id.info_app_name)
    TextView appName;
    @Bind(R.id.info_version)
    TextView appVersion;
    @Bind(R.id.info_icon)
    ImageView appIcon;

    @Bind(R.id.info_package)
    InfoLinearLayout appPackage;
    @Bind(R.id.info_sdk)
    InfoLinearLayout appSDK;
    @Bind(R.id.info_data_folder)
    InfoLinearLayout appDataFolder;
    @Bind(R.id.info_uid)
    InfoLinearLayout appUID;
    @Bind(R.id.info_install_date)
    InfoLinearLayout appInstallDate;
    @Bind(R.id.info_update_date)
    InfoLinearLayout appUpdateDate;

    @Bind(R.id.info_key_type)
    InfoLinearLayout keyType;
    @Bind(R.id.info_key_sign)
    InfoLinearLayout keySign;
    @Bind(R.id.info_key_version)
    InfoLinearLayout keyVersion;
    @Bind(R.id.info_key_serial)
    InfoLinearLayout keySerial;
    @Bind(R.id.info_key_created)
    InfoLinearLayout keyCreated;
    @Bind(R.id.info_key_expires)
    InfoLinearLayout keyExpires;

    @Bind(R.id.info_memory_apk)
    TextView memoryApk;
    @Bind(R.id.info_memory_total)
    TextView memoryTotal;
    @Bind(R.id.info_memory_data)
    TextView memoryData;
    @Bind(R.id.info_memory_cache)
    TextView memoryCache;

    @Bind(R.id.info_memory_progress)
    ProgressBar memoryProgres;

    //endregion

    public String packageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_test);
        ButterKnife.bind(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.standard_toolbar);

        mToolbar.setTitle("О приложении");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_vector);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /* SheetFloatingButton fab = (SheetFloatingButton) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.white);

        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor); */

        packageName = Remember.getString("APP_KEY", "");


        StringBuffer permissions = new StringBuffer();

        try {
            packageInfo = getPackageManager().getPackageInfo(Remember.getString("APP_KEY", ""), PackageManager.GET_PERMISSIONS);
            /* PackageManager pm = getPackageManager();
            applicationInfo = pm.getApplicationInfo(Remember.getString("APP_KEY", ""), PackageManager.GET_UNINSTALLED_PACKAGES); */
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }

        String[] requestedPermissions = packageInfo.requestedPermissions;
        if (requestedPermissions != null) {
            for (String permission : requestedPermissions)
                permissions.append(permission + "\n");

            //appPermissions.setText(permissions);
        }

        appName.setText(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
        appVersion.setText(getString(R.string.info_app_version) + String.valueOf(packageInfo.versionName));
        appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));

        appPackage.setContent(packageInfo.packageName);
        appSDK.setContent(String.valueOf(packageInfo.applicationInfo.targetSdkVersion));
        appDataFolder.setContent(packageInfo.applicationInfo.dataDir);
        appUID.setContent(String.valueOf(packageInfo.applicationInfo.uid));
        appInstallDate.setContent(Utils.getDateFormatter().format(packageInfo.firstInstallTime));
        appUpdateDate.setContent(Utils.getDateFormatter().format(packageInfo.lastUpdateTime));

        double totalMemory = Utils.getApkSize(packageInfo) + Utils.getDataSize(this,packageName) + Utils.getCacheSize(this,packageName);

        memoryApk.setText(Utils.getApkSize(packageInfo) + " MB");
        memoryTotal.setText(totalMemory + " MB");
        memoryData.setText(Utils.getDataSize(this, packageName) + " MB");
        memoryCache.setText(Utils.getCacheSize(this, packageName) + " MB");

        memoryProgres.setMax((int) totalMemory);
        memoryProgres.setProgress((int) Utils.getApkSize(packageInfo));

        showCertificateInfo(packageName);


        Log.d("Luen", Utils.getApkSize(packageInfo) + "");
    }


    public void showCertificateInfo(String packageName) {
        X509Certificate[] certs = Utils.getX509Certificates(this, packageName);
        if (certs == null || certs.length < 1)
            return;
        /*
         * for now, just support the first cert since that is far and away the
         * most common
         */
        X509Certificate cert = certs[0];

        PublicKey publickey = cert.getPublicKey();
        int size;
        if (publickey.getAlgorithm().equals("RSA"))
            size = ((RSAPublicKey) publickey).getModulus().bitLength();
        else
            size = publickey.getEncoded().length * 7; // bad estimate

        keyType.setContent(publickey.getAlgorithm() + " " + String.valueOf(size) + " bit");
        keySign.setContent(cert.getSigAlgName());
        keyVersion.setContent(String.valueOf(cert.getVersion()));
        keySerial.setContent(cert.getSerialNumber().toString(16));
        keyCreated.setContent(Utils.getDateFormatter().format(cert.getNotBefore()));
        keyExpires.setContent(Utils.getDateFormatter().format(cert.getNotAfter()));

        Log.d("Luen", packageInfo.applicationInfo.sourceDir);
    }

    /* @OnClick(R.id.info_sheet_open)
    public void openButton() {
        Intent i = getPackageManager().getLaunchIntentForPackage(Remember.getString("APP_KEY", ""));
        startActivity(i);

        materialSheetFab.hideSheet();
    }

    @OnClick(R.id.info_sheet_delete)
    public void deleteButton() {
        Intent deleteIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        deleteIntent.setData(Uri.parse("package:" + Remember.getString("APP_KEY", "")));
        deleteIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(deleteIntent, 1);

        materialSheetFab.hideSheet();
    }

    @OnClick(R.id.info_sheet_extract)
    public void extractButton() {
        Utils.extractAPK(packageInfo, this);
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Log.d("TAG", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("TAG", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("TAG", "onActivityResult: failed to (un)install");
            }
        }
    }
}
