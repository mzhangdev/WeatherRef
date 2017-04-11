package melina.weatherref.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import melina.weatherref.R;
import melina.weatherref.adapters.DayAdapter;
import melina.weatherref.adapters.HourAdapter;
import melina.weatherref.model.DayData;
import melina.weatherref.model.HourData;

public class HourlyForecastActivity extends AppCompatActivity {

    private HourData[] mHourDatas;
    private String mCityName;

    @BindView(R.id.hourlyList) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHourDatas = Arrays.copyOf(parcelables, parcelables.length, HourData[].class);

        HourAdapter adapter = new HourAdapter(mHourDatas);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }

}
