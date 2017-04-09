package melina.weatherref.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import melina.weatherref.R;
import melina.weatherref.adapters.DayAdapter;
import melina.weatherref.model.DayData;

public class DailyForecastActivity extends ListActivity {
    private DayData[] mDayDatas;
    private String mCityName;

    @BindView(R.id.locationLabel) TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDayDatas = Arrays.copyOf(parcelables, parcelables.length, DayData[].class);

        DayAdapter adapter = new DayAdapter(this, mDayDatas);
        setListAdapter(adapter);

        mCityName = intent.getStringExtra(MainActivity.CURRENT_LOCATOIN);
        Log.i("IN Daily Activity:", mCityName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UpdateCityName();
            }
        });
    }

    private void UpdateCityName() {
        if (mCityName != null) mLocationLabel.setText(mCityName);
    }
}
