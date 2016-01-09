package com.itemstudio.luen.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.itemstudio.luen.R;
import com.itemstudio.luen.ui.widget.InfoLinearLayout;
import com.itemstudio.luen.ui.widget.SheetFloatingButton;
import com.itemstudio.luen.utils.Utils;
import com.tumblr.remember.Remember;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GeneralFragment extends Fragment {

    PackageInfo packageInfo;
    ApplicationInfo applicationInfo;
    MaterialSheetFab materialSheetFab;

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

    @Bind(R.id.info_permissions)
    TextView appPermissions;

    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        ButterKnife.bind(this, view);

        SheetFloatingButton fab = (SheetFloatingButton) view.findViewById(R.id.fab);
        View sheetView = view.findViewById(R.id.fab_sheet);
        View overlay = view.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.white);

        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);


        StringBuffer permissions = new StringBuffer();

        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(Remember.getString("APP_KEY", ""), PackageManager.GET_PERMISSIONS);
            PackageManager pm = getActivity().getPackageManager();
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

        appName.setText(packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString());
        appVersion.setText(getString(R.string.info_version) + String.valueOf(packageInfo.versionName));
        appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager()));

        appPackage.setContent(packageInfo.packageName);
        appSDK.setContent(String.valueOf(applicationInfo.targetSdkVersion));
        appDataFolder.setContent(applicationInfo.dataDir);
        appUID.setContent(String.valueOf(applicationInfo.uid));
        appInstallDate.setContent(Utils.getDateFormatter().format(packageInfo.firstInstallTime));
        appUpdateDate.setContent(Utils.getDateFormatter().format(packageInfo.lastUpdateTime));

        showCertificateInfo(Remember.getString("APP_KEY", ""));

        return view;
    }

    public void showCertificateInfo(String packageName) {
        X509Certificate[] certs = Utils.getX509Certificates(getActivity(), packageName);
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

    @OnClick(R.id.info_sheet_open)
    public void openButton() {
        Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(Remember.getString("APP_KEY", ""));
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
        Utils.extractAPK(packageInfo, getActivity());
    }
}
