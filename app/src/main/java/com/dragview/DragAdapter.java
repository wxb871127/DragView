package com.dragview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dragview.com.dragview.R;

public class DragAdapter{
    private Context context;
    private List<String> selectList;
    private List<String> unselectList;

    public DragAdapter(Context context, List<String> selectList, List<String> unselectList){
        this.context = context;
        this.selectList = selectList;
        this.unselectList = unselectList;
    }

    public View getSelectView(int position, ViewGroup viewGroup){
        View view = LayoutInflater.from(context).inflate(R.layout.drag_item, viewGroup, false);
        ((TextView)view.findViewById(R.id.text)).setText(selectList.get(position));
        return view;
    }

    public View getUnselectView(int position, ViewGroup viewGroup){
        View view = LayoutInflater.from(context).inflate(R.layout.drag_item, viewGroup, false);
        ((TextView)view.findViewById(R.id.text)).setText(unselectList.get(position));
        return view;
    }

    public int getSelectCount(){
        return selectList == null ? 0 : selectList.size();
    }

    public int getUnselectCount(){
        return unselectList == null ? 0 : unselectList.size();
    }

    public String removeSelect(int position){
        if(selectList == null)
            return null;
        return selectList.remove(position);
    }

    public String removeUnselect(int position){
        if(unselectList == null)
            return null;
        return unselectList.remove(position);
    }

    public void addSelect(int position, String item){
        if(selectList == null)
            selectList = new ArrayList<>();
        selectList.add(position, item);
    }

    public void addSelect(String item){
        if(selectList == null)
            selectList = new ArrayList<>();
        selectList.add(item);
    }

    public void addUnselect(int position, String item){
        if(unselectList == null)
            unselectList = new ArrayList<>();
        unselectList.add(position, item);
    }

    public void addUnselect(String item){
        if(unselectList == null)
            unselectList = new ArrayList<>();
        unselectList.add(item);
    }

    public void changeSelectPosition(int position1, int position2){
        selectList.set(position1, selectList.set(position2, selectList.get(position1)));
    }

    public List<String> getSelectList(){
        return selectList;
    }
}
