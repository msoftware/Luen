package com.itemstudio.luen.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.itemstudio.luen.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).setToolbarTitle("Report");

        ReportMaker.init(getActivity());
    }

    public static class ReportMaker {

        private static String FILE = Environment.getExternalStorageDirectory().getPath() + "/Luen/Luen Report.txt";

        public static void init(Context context) {
            Log.i("Luen", addData(context));
        }

        public static String addData(Context context) {
            String listString = "";

            for (String s : getInstalledApps(context)) {
                listString += s + "\n";
            }

            return listString;
        }

        public static ArrayList<String> getInstalledApps(Context context) {
            ArrayList<String> res = new ArrayList<>();
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);
                if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                } else {
                    res.add(p.applicationInfo.loadLabel(context.getPackageManager()).toString() + "    " + p.packageName);
                }
            }
            return res;
        }
    }


}
