package com.example.administrator.choosePhotoAndCommit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.common.commonUtils.Utils;
import com.example.administrator.newappforutils.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yxb on 2017/10/19.
 * 展示照片+ 相机
 */

public class TablePhotoGridAdapter extends BaseAdapter {

    private int maxSize = 6;

    private List<TablePhoto> tablePhotos;
    private Context context;

    private ChoosePhotoListener listener;

    public TablePhotoGridAdapter(Context context, ChoosePhotoListener listener) {
        this.context = context;
        this.listener = listener;
        tablePhotos = new LinkedList<>();
        tablePhotos.add(new TablePhoto(1, null));
    }

    public int getRealPhotoCount() {
        int size = 0;
        for (TablePhoto photo : tablePhotos) {
            if (photo.getType() == 0) {
                size++;
            }
        }
        return size;
    }

    public List<String> getPhotoPaths(){
        List<String> paths = new ArrayList<>();
        for (TablePhoto photo : tablePhotos) {
            if (photo.getType() == 0) {
                paths.add(photo.getPath());
            }
        }
        return paths;
    }

    public void addPhoto(TablePhoto tablePhoto) {
        if (getRealPhotoCount() >= maxSize) {
            Toast.makeText(context, "已达到最大数量", Toast.LENGTH_SHORT).show();
        } else {
            tablePhotos.add(tablePhotos.size() - 1, tablePhoto);//在相机前面添加照片
        }
        notifyDataSetChanged();
    }

    public void addPhotos(List<TablePhoto> photos) {
        for (TablePhoto photo : photos) {
            if (getRealPhotoCount() < maxSize) {
                tablePhotos.add(tablePhotos.size() - 1, photo);
            } else {
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tablePhotos.size();
    }

    @Override
    public TablePhoto getItem(int position) {
        return tablePhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        if (tablePhotos.size() == maxSize + 1) {    //照片达到最大数量，要隐藏相机
            tablePhotos.remove(maxSize);
        }
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_photo, null);
            convertView.setLayoutParams(new GridView.LayoutParams(dp2px(Utils.getApp(), 74), dp2px(Utils.getApp(), 74)));
            viewHolder.photo = convertView.findViewById(R.id.photo);
            viewHolder.delete = convertView.findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (parent.getChildCount() == position) {
            if (tablePhotos.get(position).getType() == 1) { //照相机
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.photo.setImageResource(R.mipmap.ic_add_photo);
            } else if (tablePhotos.get(position).getType() == 0) { //普通照片
                viewHolder.delete.setVisibility(View.VISIBLE);
//                ImageLoader.loadImageWithUrl(viewHolder.photo, tablePhotos.get(position).getPath()); //FIXME
            }
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tablePhotos.remove(position);
                    if (getRealPhotoCount() == maxSize - 1) {   //最大数量少去一张，要显示相机
                        tablePhotos.add(new TablePhoto(1, null));
                    }
                    notifyDataSetChanged();
                }
            });
            viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tablePhotos.get(position).getType() == 1) {
                        if (listener != null) {
                            listener.choosePhoto();
                        }

                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        ImageView photo;
        ImageView delete;
    }

    public interface ChoosePhotoListener {
        void choosePhoto();
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
