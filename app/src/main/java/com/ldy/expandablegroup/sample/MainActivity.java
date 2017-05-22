package com.ldy.expandablegroup.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ldy.expandablegroup.ExpandableGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpandableGroup expandableGroup = (ExpandableGroup) findViewById(R.id.expandGroup);
        expandableGroup.setAdapter(new MainAdapter(this));
    }
}
