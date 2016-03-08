package com.delda.sensor;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.achartengine.GraphicalView;

public class SensorChart extends ActionBarActivity implements SensorEventListener {

    private SensorManager sm;
    private Sensor sensorAcc;
    private Sensor sensorLight;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] optionTitles;

    private static GraphicalView view;
    private LineGraph line = new LineGraph();
    private LinearLayout chartContainer;
    private TextView tvCurrent;
    private long starttime;
    private long timestamp;
	
	private int act_sensor = 0; 	// 0 - accelerometer , 1 - light sensor

    public int x_acc = 0;
    public int y_acc = 0;
    public int z_acc = 0;
	
	public int light = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
		
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAcc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        tvCurrent = (TextView)findViewById(R.id.tv_current);
        chartContainer = (LinearLayout) findViewById(R.id.chart_container);

		optionTitles = getResources().getStringArray(R.array.options_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_menu_item, optionTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }
    @Override
    protected void onStart() {
        super.onStart();
        view = line.getView(this);
        chartContainer.addView(view);
        starttime = System.currentTimeMillis();
        new RecordThread().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.equals(sensorAcc)) {
            x_acc = Math.round(event.values[0]*10);
            y_acc = Math.round(event.values[1]*10);
            z_acc = Math.round(event.values[2]*10);
        }
        else if (event.sensor.equals(sensorLight)) {
            light = Math.round(event.values[0] * 10);
        }
    }

    private class RecordThread extends Thread {
        public void run() {
            while(true) {
                timestamp = System.currentTimeMillis();
                double t = (double) (timestamp - starttime) / 1000.0;
				if (act_sensor == 0){
					line.addNewPoints(t, x_acc);
					line.addNewPoints_2(t, y_acc);
					line.addNewPoints_3(t, z_acc);
				}
				else if (act_sensor == 1){
					line.addNewPoints(t, light);					
				}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view = line.getView(SensorChart.this);
                        chartContainer.removeAllViews();
                        chartContainer.addView(view);
                        view.repaint();
						if (act_sensor == 0){
							tvCurrent.setText("Acceleration:   X: " + x_acc + "   Y: " + y_acc + "   Z: " + z_acc);
						}
						else if (act_sensor == 1){
							tvCurrent.setText("Light:   " + light);					
						}
                    }
                });
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {    }
	
	
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_about:
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.about, null);

                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("Névjegy")
                        .setNeutralButton("Köszönöm!", null)
                        .setIcon(R.drawable.ic_launcher)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                act_sensor = 0;
                break;
            case 1:
                act_sensor = 1;                
                break;
        }
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
