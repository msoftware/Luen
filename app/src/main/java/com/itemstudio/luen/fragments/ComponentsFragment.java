package com.itemstudio.luen.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itemstudio.luen.R;

import butterknife.ButterKnife;

public class ComponentsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_components, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
