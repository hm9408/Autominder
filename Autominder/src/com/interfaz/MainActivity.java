package com.interfaz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.autominder.NotificationService;
import com.autominder.Principal;
import com.autominder.R;
import com.autominder.Reminder;
import com.autominder.Vehicle;

public class MainActivity extends Activity implements ActionBar.TabListener, OnClickListener{


	private Principal instancia; 
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;


	private NavDrawerListAdapter adapter;

	AlarmManager alarmManager;

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
	private Button butPendingMaint;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instancia = Principal.darInstancia(this);
		if (instancia.getVehiculos().isEmpty()) {
			Intent i = new Intent(this, AddVehicleActivity.class);
			startActivityForResult(i, 666);
		}
		alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		butPendingMaint = (Button) findViewById(R.id.butNavDrawer);
		butPendingMaint.setOnClickListener(this);
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


		crearNotificationService();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items


		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		View empty = findViewById(android.R.id.empty);
		mDrawerList.setEmptyView(empty);

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());


		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				instancia.getVehiculos());
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, //nav menu toggle icon
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
		//if (savedInstanceState == null) {
		// on first time display view for first nav item
		//displayView(0);
		//}
		
		Notification noti = new Notification.Builder(this)
		.setContentTitle("Notificacion test")
		.setContentText("test")
		.setSmallIcon(R.drawable.ic_launcher)
		.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti);

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
		myIntent.putExtra("allReminders", allReminders);
		/**
		 * se crea un arreglo de fechas y solo se genera una alarma si la fecha del 
		 * recordatorio respectivo no esta ya en el arreglo, finalmente se agrega la
		 * fecha. Esto con el fin de evitar mas de una alarma/notificacion en el mismo dia.
		 */
//		ArrayList<Date> reminderDates = new ArrayList<Date>();
//		for (int i = 0; i < allReminders.size(); i++) {
//			Reminder r = allReminders.get(i);
//			if(!reminderDates.contains(r.getFecha())){
//				alarmManager.set(AlarmManager.RTC_WAKEUP, r.getFecha().getTime(), pendingIntent);
//				reminderDates.add(r.getFecha());
//			}
//		}
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
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, earliest.getFecha().getTime(), pendingIntent);
		Toast.makeText(this, "alarma creada", Toast.LENGTH_SHORT).show();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if(requestCode == 666){
			if(resultCode == RESULT_OK){
				adapter.notifyDataSetChanged();
				instancia.setSelected(instancia.getVehiculos().get(instancia.getVehiculos().size()-1));
				getActionBar().setTitle(instancia.getSelected().getName());
			}
		}else if(requestCode == 777){
			if(resultCode == RESULT_OK){
				getActionBar().setTitle(instancia.getSelected().getName());
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
			mViewPager.setAdapter(mSectionsPagerAdapter);
			getActionBar().setSelectedNavigationItem(0);
			mDrawerLayout.closeDrawers();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
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

		public SectionsPagerAdapter(FragmentManager fm) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.butNavDrawer:
			Intent i = new Intent(this, PendingRemindersActivity.class);
			startActivity(i);
			break;
		}
		
	}
}
