package com.example.imageloader;

import android.os.Bundle;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    GirdAdapter girdAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridView=findViewById(R.id.girdview);
        girdAdapter=new GirdAdapter(this);
        gridView.setAdapter(girdAdapter);
    }
}
