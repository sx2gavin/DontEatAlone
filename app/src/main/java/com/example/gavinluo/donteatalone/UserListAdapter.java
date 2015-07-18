package com.example.gavinluo.donteatalone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by joyce on 2015-07-17.
 */
public abstract class UserListAdapter extends BaseExpandableListAdapter {
    private ArrayList<User> userList;

    public UserListAdapter(){
        super();
        this.userList = new ArrayList<User>();
    }

    public void addUser(User user){
        this.userList.add(user);
    }

    public void setUserList(ArrayList<User> matchList){
//        this.userList = matchList;
//        mList.clear(); mList.addAll(newDataList);
        this.userList.clear();
        this.userList.addAll(matchList);
    }

    public ArrayList<User> getUserList(){
        return this.userList;
    }

    public void clearList(){
        // clear the user list
//        this.userList = new ArrayList<User>();
        this.userList.clear();
    }

    @Override
    public int getGroupCount() {
        return this.userList.size();
    }

    @Override
    public int getChildrenCount(int index){
        if(index >=0 && index < this.userList.size()){
            return 1;
        }
        return 0;
    }

    @Override
    public User getGroup(int i){
        return this.userList.get(i);
    }

    @Override
    public User getChild(int i, int j){
        return this.userList.get(i);
    }

    @Override
    public long getGroupId(int i){
        return i;
    }

    @Override
    public long getChildId(int i, int j){
        return j;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
