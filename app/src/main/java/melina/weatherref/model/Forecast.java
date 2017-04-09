package melina.weatherref.model;

import melina.weatherref.R;

/**
 * Created by melina on 4/7/17.
 */

public class Forecast {
    private CurrentWeather mCurrentWeather;
    private HourData[] mHourDatas;
    private DayData[] mDayDatas;

    public CurrentWeather getCurrentWeather() {
        return mCurrentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        mCurrentWeather = currentWeather;
    }

    public HourData[] getHourDatas() {
        return mHourDatas;
    }

    public void setHourDatas(HourData[] hourDatas) {
        mHourDatas = hourDatas;
    }

    public DayData[] getDayDatas() {
        return mDayDatas;
    }

    public void setDayDatas(DayData[] dayDatas) {
        mDayDatas = dayDatas;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.clear_day;

        if (iconString.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        } else if (iconString.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        } else if (iconString.equals("rain")) {
            iconId = R.drawable.rain;
        } else if (iconString.equals("snow")) {
            iconId = R.drawable.snow;
        } else if (iconString.equals("sleet")) {
            iconId = R.drawable.sleet;
        } else if (iconString.equals("wind")) {
            iconId = R.drawable.wind;
        } else if (iconString.equals("fog")) {
            iconId = R.drawable.fog;
        } else if (iconString.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        } else if (iconString.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        } else if (iconString.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }

        return iconId;
    }
}
