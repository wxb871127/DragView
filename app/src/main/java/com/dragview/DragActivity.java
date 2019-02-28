package com.dragview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import dragview.com.dragview.R;

public class DragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragview);
        DragView dragView = (DragView) findViewById(R.id.dragview);
        List<String> selectList = new ArrayList<>();
        selectList.add("林浩1");
        selectList.add("林浩2");
        selectList.add("林浩3");
        selectList.add("林浩4");
        selectList.add("林浩5");
        List<String> unselectList = new ArrayList<>();
        unselectList.add("王俊俊1");
        unselectList.add("王俊俊2");
        unselectList.add("王俊俊3");
        unselectList.add("王俊俊4");
        unselectList.add("王俊俊5");
        unselectList.add("王俊俊6");
        unselectList.add("王俊俊7");
        unselectList.add("王俊俊8");
        unselectList.add("王俊俊9");
        DragAdapter dragAdapter = new DragAdapter(this,selectList ,unselectList);
        dragView.setAdapter(dragAdapter);

    }
}
