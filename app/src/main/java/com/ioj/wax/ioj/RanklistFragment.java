package com.ioj.wax.ioj;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RanklistFragment extends Fragment {
    View view;

    private TabLayout tabs;
    private ViewPager viewPager;
    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();
    private FragmentManager Fm;
    MyAdapter adapter;
    @Override
    public void onStop() {
        for(int i=0;i<mFragment.size();i++) {
            Fm.beginTransaction().remove(mFragment.get(i));
        }
        mFragment.clear();
        super.onStop();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.ranklist_fragment, container, false);
        initView();
        Fm=getChildFragmentManager();
        adapter = new MyAdapter(Fm, mTitle, mFragment);
        viewPager.setAdapter(adapter);
        //为TabLayout设置ViewPager
        tabs.setupWithViewPager(viewPager);
        //使用ViewPager的适配器
        return view;
    }
    private void initView() {

        tabs = (TabLayout)view.findViewById(R.id.rl_tablayout);
        viewPager = (ViewPager)view.findViewById(R.id.id_Rl_ViewPager);
        mTitle.add("All");
        mTitle.add("Day");
        mFragment.add(new Ranklist_all());
        mFragment.add(new Ranklist_day());

    }
}