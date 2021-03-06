package com.smallpigex.eat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.smallpigex.eat.com.eating.util.Consts;
import com.smallpigex.eat.com.eating.util.Convertor;
import com.smallpigex.eat.com.whatwouldyoulike.model.Model;
import com.smallpigex.eat.com.whatwouldyoulike.model.Restaurant;
import com.smallpigex.eat.com.whatwouldyoulike.model.SlotMachine;
import com.smallpigex.eat.dummy.TestingViewerActivity;

import java.util.Map;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationFragment.OnFragmentInteractionListener,
        RestaurantFragment.OnFragmentInteractionListener, AddRestaurantFragment.SaveRestaurantInformation, SlotMachineFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static Model model;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (model == null) {
            model = new Model(getSharedPreferences(Consts.PREFS_NAME, 0));
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if(count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        android.app.Fragment fragment;
        // update the main content by replacing fragments
       switch (position) {
           case 0:
               fragment = FragmentFactory.newInstance(position);
               addFragment(fragment, Consts.LOCATION_FRAGMENT);
               break;
           case 1:
               fragment = FragmentFactory.newInstance(position);
               addFragment(fragment, Consts.LOCATION_FRAGMENT);
               break;

           default:
               Intent intent = new Intent(this, RestaurantViewPager.class);
               startActivity(intent);
               break;
       }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doPositiveClick(String location) {
        // Do stuff here.
        model.saveLocation(location);
        android.app.Fragment locationFragment = getFragmentManager().findFragmentByTag(Consts.LOCATION_FRAGMENT);
        locationFragment.onResume();
        Log.i("Location Fragment", locationFragment.toString());
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    @Override
    public void onFragmentInteraction(String id, boolean isSlotMachineState) {
        Log.i("click Item ", id);
        //new restaurant list page
        if(!isSlotMachineState) {
            android.app.Fragment restaurantFragment = RestaurantFragment.newInstance(id);
            addFragment(restaurantFragment, Consts.RESTAURANT_FRAGMENT);
        } else {
            android.app.Fragment slotMachineFragment = SlotMachineFragment.newInstance(id);
            addFragment(slotMachineFragment, Consts.SLOTMACHINE_FRAGMENT);
        }
    }

    @Override
    public void showRestaurantDetail(Map<String, Object> objectMap) {
        Restaurant restaurantInfo = Convertor.convertMapToRestaurantObject(objectMap);
        startRestaurantInfoActivity(restaurantInfo);
    }

    @Override
    public void showAddRestaurantFragment(String location) {
        android.app.Fragment addRestaurantFragment = AddRestaurantFragment.newInstance(location);
        addFragment(addRestaurantFragment, Consts.ADD_RESTAURANT_FRAGMENT);
    }

    @Override
    public void saveRestaurantInformation(Restaurant restaurant) {
        model.saveRestaurant(restaurant);
        getFragmentManager().popBackStackImmediate();
    }

    private void addFragment(android.app.Fragment fragment, String fragmentTag) {
        android.app.FragmentTransaction fragmentTransaction= getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragmentTag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void showRestaurantInfo(Uri uri) {
        String region = uri.toString();
        Restaurant restaurantInfo = model.putSlotMachineButton(region);
        startRestaurantInfoActivity(restaurantInfo);
    }

    public void startRestaurantInfoActivity(Restaurant restaurantInfo) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(Consts.RESTAURANT_INFORMATION, restaurantInfo);
        startActivity(intent);
    }
}
