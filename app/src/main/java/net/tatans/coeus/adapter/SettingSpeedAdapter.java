package net.tatans.coeus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.service.R;


public class SettingSpeedAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Bitmap choice_icon;
    private Bitmap transparent_icon;
    private String[] speedArray;
    private String playSpeed;

    public SettingSpeedAdapter(Context context, String[] speedArray, String playSpeed) {
        mInflater = LayoutInflater.from(context);
        this.speedArray = speedArray;
        this.choice_icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.choice_icon);
        this.transparent_icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.transparent_icon);
        this.playSpeed = playSpeed;

    }

    public int getCount() {
        return speedArray.length;
    }

    public Object getItem(int position) {
        return speedArray[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tts_main_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView
                    .findViewById(R.id.tv_item_name);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String speed = speedArray[position];
        if (playSpeed.equals(speed)) {
            holder.icon.setImageBitmap(choice_icon);
            holder.text.setText(speed);
            holder.text.setContentDescription("。");
            holder.text.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_HOVER_ENTER:
//                            TatansApplication.speech("语速+"+speed+"，已选中"
//                                    , mPlaySpeed, new TatansSpeakerCallback() {
//                                        @Override
//                                        public void onCompleted() {
//                                            super.onCompleted();
//                                        }
//                                    });
                            break;
                    }
                    return false;
                }
            });
        } else {
            holder.icon.setImageBitmap(transparent_icon);
            holder.text.setText(speed);
            holder.text.setContentDescription("。");
            holder.text.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_HOVER_ENTER:
//                            TatansApplication.speech("语速" + speed + "，未选中，点按可切换", mPlaySpeed, new TatansSpeakerCallback() {
//                                @Override
//                                public void onCompleted() {
//                                    super.onCompleted();
//                                }
//                            });
                            break;
                    }
                    return false;
                }
            });
        }

        return convertView;
    }

    private class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
