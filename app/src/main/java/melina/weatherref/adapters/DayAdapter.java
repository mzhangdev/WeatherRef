package melina.weatherref.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import melina.weatherref.R;
import melina.weatherref.model.DayData;

/**
 * Created by melina on 4/8/17.
 */

public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private DayData[] mDayDatas;

    public DayAdapter(Context context, DayData[] dayDatas) {
        mContext = context;
        mDayDatas = dayDatas;
    }

    @Override
    public int getCount() {
        return mDayDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return mDayDatas[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;  // Not used;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);
            holder.circleImageView = (ImageView) convertView.findViewById(R.id.circleImageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DayData day = mDayDatas[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");
        holder.dayLabel.setText(day.getDayOfTheWeek());
        holder.circleImageView.setImageResource(R.drawable.bg_temperature);

        if (position == 0) {
            holder.dayLabel.setText("Today");
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        ImageView circleImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
