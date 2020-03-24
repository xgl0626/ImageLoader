package com.example.imageloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.imageloader.R.drawable.image;

public class GirdAdapter extends BaseAdapter {
    List<String>imagePath;
    private LayoutInflater inflater;
    private Context context;
    public GirdAdapter(Context context)
    {
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    private boolean mIsGridViewScoll;
    public void onScollStateChanged(AbsListView view,int scollState)
    {
        if(scollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE)//SCROLL_STATE_IDLE停止滑动
        {
            mIsGridViewScoll = true;
            notifyDataSetChanged();
        }
        else{
            mIsGridViewScoll = false;
        }
    }
    @Override
    public int getCount() {
        return imagePath.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addImageUri(ArrayList<String> imagePathList) {
        imagePath.addAll(imagePathList);
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder=null;
        if(convertView==null)
        {
            viewholder=new ViewHolder();
            convertView=inflater.inflate(R.layout.imagephoto,parent,false);
            viewholder.imageView=(ImageView) convertView.findViewById(R.id.image_photo);
            convertView.setTag(viewholder);
        }
        else {
            viewholder=(ViewHolder) convertView.getTag();
        }
        ImageView imageView=viewholder.imageView;
        final String tag=(String) imageView.getTag();
        final String uri=imagePath.get(position);
        //占位图
        if(!uri.equals(tag)) {
            imageView.setImageResource(image);
        }
        //停止滑动时加载
        if(mIsGridViewScoll)
        {
            imageView.setTag(uri);
            ImageLoader.getInstance().BindBitmap(uri,imageView,400,400);
        }
        return convertView;
    }
    public class ViewHolder
    {
        ImageView imageView;
    }
}
