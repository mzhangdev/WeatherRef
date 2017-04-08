package melina.weatherref.model;

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
}
