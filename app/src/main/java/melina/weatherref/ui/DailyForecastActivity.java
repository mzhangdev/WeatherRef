package melina.weatherref.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import melina.weatherref.R;
import melina.weatherref.adapters.DayAdapter;
import melina.weatherref.model.DayData;

public class DailyForecastActivity extends ListActivity {
    private DayData[] mDayDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        DayAdapter adapter = new DayAdapter(this, mDayDatas);
    }

}
