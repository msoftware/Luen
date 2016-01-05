package com.itemstudio.luen;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tumblr.remember.Remember;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {
    PackageInfo packageInfo;
    ApplicationInfo applicationInfo;

    //region INITIALIZATION VIEWS
    @Bind(R.id.info_app_name)
    TextView appName;
    @Bind(R.id.info_version)
    TextView appVersion;
    @Bind(R.id.info_icon)
    ImageView appIcon;

    @Bind(R.id.info_package)
    InfoView appPackage;
    @Bind(R.id.info_sdk)
    InfoView appSDK;
    @Bind(R.id.info_data_folder)
    InfoView appDataFolder;
    @Bind(R.id.info_uid)
    InfoView appUID;
    @Bind(R.id.info_install_date)
    InfoView appInstallDate;
    @Bind(R.id.info_update_date)
    InfoView appUpdateDate;

    @Bind(R.id.info_key_type)
    InfoView keyType;
    @Bind(R.id.info_key_sign)
    InfoView keySign;
    @Bind(R.id.info_key_version)
    InfoView keyVersion;
    @Bind(R.id.info_key_serial)
    InfoView keySerial;
    @Bind(R.id.info_key_created)
    InfoView keyCreated;
    @Bind(R.id.info_key_expires)
    InfoView keyExpires;

    @Bind(R.id.info_permissions)
    TextView appPermissions;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);

        toolbar.setTitle("О приложении");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_vector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        StringBuffer permissions = new StringBuffer();

        try {
            packageInfo = getPackageManager().getPackageInfo(Remember.getString("APP_KEY", ""), PackageManager.GET_PERMISSIONS);
            PackageManager pm = getPackageManager();
            applicationInfo = pm.getApplicationInfo(Remember.getString("APP_KEY", ""), PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        }

        String[] requestedPermissions = packageInfo.requestedPermissions;
        if (requestedPermissions != null) {
            for (String permission : requestedPermissions)
                permissions.append(permission + "\n");

            appPermissions.setText(permissions);
        }

        appName.setText(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
        appVersion.setText(getString(R.string.info_version) + String.valueOf(packageInfo.versionName));
        appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));

        appPackage.setContent(packageInfo.packageName);
        appSDK.setContent(String.valueOf(applicationInfo.targetSdkVersion));
        appDataFolder.setContent(applicationInfo.dataDir);
        appUID.setContent(String.valueOf(applicationInfo.uid));
        appInstallDate.setContent(Utils.getDateFormatter().format(packageInfo.firstInstallTime));
        appUpdateDate.setContent(Utils.getDateFormatter().format(packageInfo.lastUpdateTime));

        showCertificateInfo(this, Remember.getString("APP_KEY", ""));
    }

    public void showCertificateInfo(Activity activity, String packageName) {
        X509Certificate[] certs = Utils.getX509Certificates(activity, packageName);
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
    }
}
