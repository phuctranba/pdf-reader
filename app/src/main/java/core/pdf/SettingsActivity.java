package core.pdf;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.core.pdf.reader.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Settings");

        SwitchCompat nightmode = findViewById(R.id.night_mode);
        nightmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MySharedPreferences.setPrefNightMode(SettingsActivity.this,isChecked);
            }
        });

        SwitchCompat horizontalscroll = findViewById(R.id.horizontal_scroll);
        horizontalscroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MySharedPreferences.setPrefHorizontalScroll(SettingsActivity.this,isChecked);
            }
        });


        boolean isNightMode = MySharedPreferences.getPrefNightMode(SettingsActivity.this);
        boolean isHorizontalScroll = MySharedPreferences.getPrefHorizontalScroll(SettingsActivity.this);

        nightmode.setChecked(isNightMode);
        horizontalscroll.setChecked(isHorizontalScroll);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}