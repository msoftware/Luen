package com.itemstudio.luen.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itemstudio.luen.R;
import com.itemstudio.luen.adapters.AppsAdapter;
import com.itemstudio.luen.adapters.AppsClicker;
import com.itemstudio.luen.model.AppItem;
import com.itemstudio.luen.ui.BaseActivity;
import com.itemstudio.luen.ui.InfoViewActivity;
import com.tumblr.remember.Remember;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {

    private AppsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).setToolbarTitle("Мои приложения");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AppsAdapter(getInstalledApps());

        mRecyclerView.addOnItemTouchListener(
                new AppsClicker(getActivity(), new AppsClicker.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Remember.putString("APP_KEY", mAdapter.getPackage(position));
                        startActivity(new Intent(getActivity(), InfoViewActivity.class));
                    }
                })
        );

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private ArrayList<AppItem> getInstalledApps() {
        ArrayList<AppItem> res = new ArrayList<>();
        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                AppItem newInfo = new AppItem();
                newInfo.appName = p.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                newInfo.packageName = p.packageName;
                newInfo.appIcon = p.applicationInfo.loadIcon(getActivity().getPackageManager());
                res.add(newInfo);
            }
        }
        return res;
    }
}
