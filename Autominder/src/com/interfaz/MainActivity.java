package com.interfaz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.autominder.ConexionCliente;
import com.autominder.LocationBroadcastReceiver;
import com.autominder.NotificationService;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Reminder;

public class MainActivity extends Activity implements ActionBar.TabListener{


	public final static long frecuenciaModoCarro = 6000;

	private Principal instancia; 
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	@SuppressWarnings("unused")
	private CharSequence mTitle;


	private NavDrawerListAdapter adapter;
	private SensorManager mySensorManager;
	AlarmManager alarmManager;

	boolean modoCarro = false;
	private LocationBroadcastReceiver lbr;
	private static double kmCount=0;

	private boolean mostrado = false;


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	ConexionCliente c;

	//shake motion detection code respectfully taken from: http://androidcookbook.com/Recipe.seam?recipeId=529
	private final SensorEventListener mySensorEventListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			updateAccelParameters(se.values[0], se.values[1], se.values[2]);   // (1)
			if ((!shakeInitiated) && isAccelerationChanged()) {                                      // (2) 
				shakeInitiated = true; 
			} else if ((shakeInitiated) && isAccelerationChanged()) {                              // (3)
				executeShakeAction();
			} else if ((shakeInitiated) && (!isAccelerationChanged())) {                           // (4)
				shakeInitiated = false;
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			/* can be ignored in this example */
		}
	};

	/* Here we store the current values of acceleration, one for each axis */
	private float xAccel;
	private float yAccel;
	private float zAccel;

	/* And here the previous ones */
	private float xPreviousAccel;
	private float yPreviousAccel;
	private float zPreviousAccel;

	/* Used to suppress the first shaking */
	private boolean firstUpdate = true;

	/*What acceleration difference would we assume as a rapid movement? */
	private final float shakeThreshold = 3.3f;

	/* Has a shaking motion been started (one direction) */
	private boolean shakeInitiated = false;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instancia = Principal.darInstancia(this);
		c = new ConexionCliente(this);

		if (instancia.getUsername() == null) {//primera vez que abre la app
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, 111);
		}else{
			hacertodo();
		}
	}

	private void hacertodo(){

		alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		if (actionBar.getNavigationItemCount() == 0) {
			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title defined by
				// the adapter. Also specify this Activity object, which implements
				// the TabListener interface, as the callback (listener) for when
				// this tab is selected.
				actionBar.addTab(actionBar.newTab()
						.setText(mSectionsPagerAdapter.getPageTitle(i))
						.setTabListener(this)
						);
			}
		}	

		crearNotificationService();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items


		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		View empty = findViewById(android.R.id.empty);
		mDrawerList.setEmptyView(empty);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(this, this,
				instancia.getVehiculos());
		mDrawerList.setAdapter(adapter);



		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				){
			public void onDrawerClosed(View view) {
				if (instancia.getSelected() != null) {
					getActionBar().setTitle(instancia.getSelected().getName());
				}else{
					getActionBar().setTitle(mDrawerTitle);
				}

				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (instancia.getSelected()==null) {
			getActionBar().setTitle(R.string.app_name);
		}
		else
		{
			getActionBar().setTitle(instancia.getSelected().getName());
		}

		// enabling action bar app icon and behaving it as toggle button
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		//if (savedInstanceState == null) {
		// on first time display view for first nav item
		//displayView(0);
		//}

		forzarRefresh(1);

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // (1)
		mySensorManager.registerListener(mySensorEventListener, mySensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL); // (2)

	}

	/**
	 * Se debe llamar este metodo cuando se recalculen los reminders de un carro,
	 * es decir, cuando se crea un carro, cuando se modifica un weeklyKM, o cuando
	 * se modifica un currentKmCount.<br><br>
	 * 
	 * Si es pertinente, se deben borrar todas las alertas creadas anteriormente, a
	 * traves del metodo:<br><br>
	 * 
	 * <b>alarmManager.cancel(PendingIntent.getService(this, 0, myIntent, 0))</b>
	 */
	public void crearNotificationService(){
		Intent myIntent = new Intent(this , NotificationService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

		ArrayList<Reminder> allReminders = instancia.obtenerReminders();

		/**
		 * Se busca el reminder mas atrasado y se crea una unica alarma
		 */
		Reminder earliest = null;
		for (int i = 0; i < allReminders.size(); i++) {
			Reminder r = allReminders.get(i);
			if(earliest == null || r.getFecha().getTime()<earliest.getFecha().getTime()){
				earliest = r;
			}

		}
		if(earliest != null){
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, earliest.getFecha().getTime(), pendingIntent);
		}

	}

	public static void actualizarKmCount(double km){
		kmCount = km;
		System.out.println("kmCount: "+kmCount);
	}

	public void tryIniciarModoCarro(){
		System.out.println("INTENTANDO INICIAR MODO CARRO DESDE MAIN_ACTIVITY");

		LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		String provider = null;
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else { 		
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), null);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Modo Vehiculo requiere servicios de localizacion\nDesea activarlos?").setPositiveButton("Si", dialogClickListener)
			.setNegativeButton("No", dialogClickListener).show();
		}

		if (provider!=null){
			mySensorManager.unregisterListener(mySensorEventListener);
			//			Intent intent = new Intent (this, LocationBroadcastReceiver.class);
			//			intent.putExtra("provider", provider);
			//			PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
			//			am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			//			am.set(AlarmManager.RTC_WAKEUP, (new Date()).getTime(), pIntent);
			lbr = new LocationBroadcastReceiver(provider);
			lbr.stahrt(getApplicationContext());
			kmCount=0;
			modoCarro=true;
			invalidateOptionsMenu();
		}



	}

	public void desactivarModoCarro(){
		lbr.stahp();
		lbr = null;

		//if (km>0){  //lo dejo comentado para pruebas
		instancia.getSelected().modifyCurrentKmCount(instancia.getSelected().getCurrentKmCount()+(int)kmCount);
		forzarRefresh(1);

		Toast.makeText(getApplicationContext(), "Se aumentó el odometro en "+(int)kmCount+" km \n Había "+kmCount+" de recorrido", Toast.LENGTH_LONG).show();
		//}


		modoCarro = false;
		kmCount=0;

		System.out.println("modoCarro: "+modoCarro);
		invalidateOptionsMenu();

	}

	public void pushCambios(){
		c.datosPush(instancia.getUsername(), instancia.getPassword());		
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(requestCode == 666){//vuelve de AddVehicleActivity
			if(resultCode == RESULT_OK){
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... p)
					{
						pushCambios();
						return null;
					}
				}.execute();

				hacertodo();

				adapter.notifyDataSetChanged();
				instancia.setSelected(instancia.getVehiculos().get(instancia.getVehiculos().size()-1));
				getActionBar().setTitle(instancia.getSelected().getName());
				crearNotificationService();
				forzarRefresh(1);

			}
		}else if(requestCode == 999){//vuelve de PendingRemindersActivity
			if(resultCode == RESULT_OK){
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... p)
					{
						pushCambios();
						return null;
					}
					@Override
					protected void onPostExecute(Void result)
					{
						getActionBar().setTitle(instancia.getSelected().getName());
						crearNotificationService();
					}
				}.execute();
			}
		}
		else if(requestCode == 111){//vuelve de Login
			if(instancia.getUsername() == null){//significa que desea seguir offline, sin hacer login
				System.out.println("llega de avanzar sin login");
				Intent i = new Intent(this, AddVehicleActivity.class);
				startActivityForResult(i, 666);
			}else if(instancia.getVehiculos().isEmpty()){//significa que registro un nuevo usuario
				System.out.println("llega de registrar usuario");
				Intent i = new Intent(this, AddVehicleActivity.class);
				startActivityForResult(i, 666);
			}else{//significa que hizo login y pull de sus datos
				hacertodo();

				adapter.notifyDataSetChanged();
				instancia.setSelected(instancia.getVehiculos().get(instancia.getVehiculos().size()-1));
				getActionBar().setTitle(instancia.getSelected().getName());
				crearNotificationService();

			}
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			instancia.setSelected(instancia.getVehiculos().get(position)); //sets selected vehicle
			getActionBar().setTitle(instancia.getSelected().getName());
			forzarRefresh(1);
			mDrawerLayout.closeDrawers();
		}
	}

	public void forzarRefresh(int i){
		mViewPager.setAdapter(mSectionsPagerAdapter);
		getActionBar().setSelectedNavigationItem(i);
		mViewPager.setCurrentItem(i);
	}

	public void refreshDrawer(){
		adapter.notifyDataSetChanged();
	}

	//	private boolean isServiceRunning(Class<?> serviceClass){
	//		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	//		for(RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
	//			if(serviceClass.getName().equals(service.service.getClassName())){
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){

		if(!instancia.getUsername().equalsIgnoreCase("offline")){
			menu.findItem(R.id.login_option).setVisible(false);
		}else{
			menu.findItem(R.id.login_option).setVisible(true);
		}

		if(!modoCarro){
			menu.findItem(R.id.disable_tracking).setVisible(false);
		}else{
			menu.findItem(R.id.disable_tracking).setVisible(true);
		}

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case(R.id.add_vehicle):
			Intent i = new Intent(this, AddVehicleActivity.class);
		startActivityForResult(i, 666);
		return true;
		case(R.id.pending_reminders):
			Intent i2 = new Intent(this, PendingRemindersActivity.class);
		startActivityForResult(i2, 999);
		return true;
		case(R.id.login_option):
			Intent i3 = new Intent(this, LoginActivity.class);
		startActivityForResult(i3, 222);
		return true;
		case(R.id.disable_tracking):
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					desactivarModoCarro();
					//volver a escuchar shakes
					mySensorManager.registerListener(mySensorEventListener, mySensorManager
							.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL); // (2)

					break;

				case DialogInterface.BUTTON_NEGATIVE:
					//No button clicked
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Desea desactivar el modo Vehículo?").setPositiveButton("Si", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();

		return true;
		case android.R.id.home:
			if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			}
			else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> fragments;

		public SectionsPagerAdapter(FragmentManager fm, MainActivity mainActivity) {
			super(fm);
			fragments = new ArrayList<Fragment>();
			fragments.add( new FragmentoRecords());
			fragments.add( new FragmentoInfoVehiculo());
			fragments.add( new FragmentoReminders());
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).

			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	//SHAKE METHODS

	/* Store the acceleration values given by the sensor */
	private void updateAccelParameters(float xNewAccel, float yNewAccel,
			float zNewAccel) {
		/* we have to suppress the first change of acceleration, it results from first values being initialized with 0 */
		if (firstUpdate) {  
			xPreviousAccel = xNewAccel;
			yPreviousAccel = yNewAccel;
			zPreviousAccel = zNewAccel;
			firstUpdate = false;
		} else {
			xPreviousAccel = xAccel;
			yPreviousAccel = yAccel;
			zPreviousAccel = zAccel;
		}
		xAccel = xNewAccel;
		yAccel = yNewAccel;
		zAccel = zNewAccel;
	}

	/* If the values of acceleration have changed on at least two axises, we are probably in a shake motion */
	private boolean isAccelerationChanged() {
		float deltaX = Math.abs(xPreviousAccel - xAccel);
		float deltaY = Math.abs(yPreviousAccel - yAccel);
		float deltaZ = Math.abs(zPreviousAccel - zAccel);
		//		return (deltaX > shakeThreshold && deltaY > shakeThreshold)
		//				|| (deltaX > shakeThreshold && deltaZ > shakeThreshold)
		//				|| (deltaY > shakeThreshold && deltaZ > shakeThreshold);
		return (deltaY > shakeThreshold || deltaZ > shakeThreshold || deltaX > shakeThreshold);
	}

	private void executeShakeAction() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					tryIniciarModoCarro();
					mostrado = false;
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					//No button clicked
					mostrado = false;
					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		if(!mostrado){
			builder.setMessage("Desea activar el modo Vehículo?").setPositiveButton("Si", dialogClickListener)
			.setNegativeButton("No", dialogClickListener).show();
			mostrado =true;
		}
	}

}
