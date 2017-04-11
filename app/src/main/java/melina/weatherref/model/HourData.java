package melina.weatherref.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by melina on 4/7/17.
 */

public class HourData implements Parcelable {
    private long mTime;
    private String mSummary;
    private double mTemperature;
    private String mIcon;
    private String mTimeZone;

    public HourData() {}

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date datetime = new Date(getTime() * 1000);
        String timeString = formatter.format(datetime);

        return timeString;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperature() {
        return (int)Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperature);
        dest.writeString(mIcon);
        dest.writeString(mTimeZone);
    }

    private HourData(Parcel in) {
        mTime = in.readLong();
        mSummary = in.readString();
        mTemperature = in.readDouble();
        mIcon = in.readString();
        mTimeZone = in.readString();
    }

    public static final Creator<HourData> CREATOR = new Creator<HourData>() {
        @Override
        public HourData createFromParcel(Parcel source) {
            return new HourData(source);
        }

        @Override
        public HourData[] newArray(int size) {
            return new HourData[size];
        }
    };
}
