package com.ioj.wax.ioj;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProblemsFragment extends Fragment {
    private UserInfo mUserInfo;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mUserInfo = ((MainActivity)getActivity()).mUserInfo;
        return inflater.inflate(R.layout.problems_fragment, container, false);
    }
}