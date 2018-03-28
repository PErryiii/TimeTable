package com.example.timetable_1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import tableList.Table;
import noteList.Note;

/**
 * Created by PErry on 2018/2/11.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitle = new String[]{"课程表","笔记"};
    private Context context;
    private List<android.support.v4.app.Fragment> fragmentList;

    public ViewPagerAdapter(android.support.v4.app.FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        this.context = context;
        initFragmentTab();
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return tabTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }

    private void initFragmentTab() {
        Note note = new Note();
        Table table = new Table();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(table);
        fragmentList.add(note);
    }
}
