package melina.weatherref.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import melina.weatherref.R;
import melina.weatherref.model.CurrentWeather;
import melina.weatherref.model.DayData;
import melina.weatherref.model.Forecast;
import melina.weatherref.model.HourData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String CURRENT_LOCATOIN = "CURRENT_LOCATOIN";

    private Forecast mForecast;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest = null;
    private Location location = null;
    private String cityName = null;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static String[] LOCATION_PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private static int LOCATION_REQUEST_CODE = 1;

    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.progressBar) ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressbar.setVisibility(View.INVISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Log.d(TAG, "Main UI code is running!");

    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }

    private void getForecast() {
        if (isNetworkAvailable()) {
            toggleRefresh();

            if (location != null) {
                double latitude = location.getLatitude();
                double longtitude = location.getLongitude();

                getCityName();

                String apiKey = "5e6341360efc908f10e68e3009aab0ec";
                String forecastUrl = "https://api.darksky.net/forecast/" + apiKey +
                        "/" + latitude + "," + longtitude;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(forecastUrl)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        alertUserAboutError();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toggleRefresh();
                                }
                            });

                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                mForecast = parseForecastDetails(jsonData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UpdateDisplay();
                                    }
                                });
                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception caught: ", e);
                        } catch (JSONException e) {
                            Log.e(TAG, "Exception caught: ", e);
                        }
                    }
                });
            } else {
                toggleRefresh();
            }
        } else {
            Toast.makeText(this, R.string.network_unavailable_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressbar.getVisibility() == View.INVISIBLE) {
            mProgressbar.setVisibility(View.VISIBLE);
        } else {
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void UpdateDisplay() {
        CurrentWeather currentWeather = mForecast.getCurrentWeather();
        mTemperatureLabel.setText(currentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + currentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(currentWeather.getHumidityPercentage() + "%");
        mPrecipValue.setText(currentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(currentWeather.getSummary());
        if (cityName != null) mLocationLabel.setText(cityName);

        Drawable drawable = getResources().getDrawable(currentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrentWeather(getCurrentDetails(jsonData));
        forecast.setHourDatas(getHourDetails(jsonData));
        forecast.setDayDatas(getDayDetails(jsonData));

        return forecast;
    }

    private DayData[] getDayDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray daydatas = daily.getJSONArray("data");

        DayData[] days = new DayData[daydatas.length()];
        for (int i = 0; i < daydatas.length(); i++) {
            JSONObject jsonDay = daydatas.getJSONObject(i);
            DayData dayData = new DayData();
            dayData.setSummary(jsonDay.getString("summary"));
            dayData.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            dayData.setIcon(jsonDay.getString("icon"));
            dayData.setTime(jsonDay.getLong("time"));
            dayData.setTimezone(timezone);
            days[i] = dayData;
        }

        return days;
    }

    private HourData[] getHourDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray hourdatas = hourly.getJSONArray("data");

        HourData[] hours = new HourData[hourdatas.length()];
        for (int i = 0; i < hourdatas.length(); i++) {
            JSONObject jsonHour = hourdatas.getJSONObject(i);
            HourData hourData = new HourData();
            hourData.setSummary(jsonHour.getString("summary"));
            hourData.setTemperature(jsonHour.getDouble("temperature"));
            hourData.setIcon(jsonHour.getString("icon"));
            hourData.setTime(jsonHour.getLong("time"));
            hourData.setTimeZone(timezone);
            hours[i] = hourData;
        }
        return hours;
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currenly = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currenly.getDouble("humidity"));
        currentWeather.setTime(currenly.getLong("time"));
        currentWeather.setIcon(currenly.getString("icon"));
        currentWeather.setPrecipChance(currenly.getDouble("precipProbability"));
        Log.i(TAG, currentWeather.getPrecipChance() + "");
        currentWeather.setSummary(currenly.getString("summary"));
        currentWeather.setTemperature(currenly.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog ");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        Toast.makeText(this, "Get location failed. Please re-try.", Toast.LENGTH_SHORT).show();
    }

    protected void startLocationUpdates() {
        Log.i(TAG, "In startLocationUpdates");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_REQUEST_CODE);
            return;
        } else {

            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } catch (SecurityException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (this.location != null) {
            getForecast();
        }
        Log.e(TAG, "location: " + location.toString());
    }

    private void getCityName() {
        if (location != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                cityName = address.getLocality();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mLocationRequest, this);
                } catch (SecurityException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Log.w(TAG, "Location permissions not granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnClick(R.id.dailyButton)
    public void startDailyActiity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDayDatas());
        intent.putExtra(CURRENT_LOCATOIN, cityName);

        startActivity(intent);
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyActiity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourDatas());
        intent.putExtra(CURRENT_LOCATOIN, cityName);

        startActivity(intent);
    }
}
