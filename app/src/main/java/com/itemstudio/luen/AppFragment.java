package com.itemstudio.luen;


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

import com.itemstudio.luen.model.AppItem;
import com.tumblr.remember.Remember;

import java.util.ArrayList;
import java.util.List;

public class AppFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AppAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((BaseActivity) getActivity()).setToolbarTitle("Мои приложения");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_base, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AppAdapter(getInstalledApps());

        mRecyclerView.addOnItemTouchListener(
                new AppClicker(getActivity(), new AppClicker.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Remember.putString("APP_KEY", mAdapter.getPackage(position));
                        startActivity(new Intent(getActivity(), ViewActivity.class));
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
