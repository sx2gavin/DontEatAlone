package com.example.gavinluo.donteatalone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import 	android.support.design.widget.TabLayout;
import android.view.View;

// Reference : http://developer.android.com/training/implementing-navigation/lateral.html#tabs

public class MatchesActivity extends ActionBarActivity
        implements NoMatchFragment.OnFragmentInteractionListener,
        MatchListFragment.OnFragmentInteractionListener,
        RequestListFragment.OnFragmentInteractionListener,
        PreferenceFragment.OnFragmentInteractionListener,
        ActionBar.TabListener
{
    public static final int NUM_PAGES = 3;
    public static final int PAGE_SEARCH = 0;
    public static final int PAGE_MATCHES = 1;
    public static final int PAGE_REQUESTS = 2;

    static MatchesPagerAdapter mMatchesPageAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    MatchesActivity activity ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        activity = this;

        // ViewPager and its adapter use support library
        // fragments, so use getSupportFragmentManager.
        mMatchesPageAdapter = new MatchesPagerAdapter(getSupportFragmentManager(), this);
        mTabLayout = (TabLayout)findViewById(R.id.matches_tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.matches_pager);

        mViewPager.setAdapter(mMatchesPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout){
            @Override
            public void onPageSelected(int position){
                activity.setService(position);
            }
        });

        // Set a toolbar to replace the action bar.
//        Toolbar toolbar = (Toolbar) findViewById(R.id.matches_toolbar);
//        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matches, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent (this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
        setService(-1);     // stop all services
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    public void setService( int position ){
        // get the pages
        PreferenceFragment searchPage = (PreferenceFragment) mMatchesPageAdapter.getItem(PAGE_SEARCH);
        MatchListFragment matchesPage = (MatchListFragment) mMatchesPageAdapter.getItem(PAGE_MATCHES);
        RequestListFragment requestPage = (RequestListFragment) mMatchesPageAdapter.getItem(PAGE_REQUESTS);

        // TODO: stop all thread in all fragments
        matchesPage.stopUpdate();
        requestPage.stopUpdateRequests();

        // start/stop server call thread
        if(position == PAGE_SEARCH) {

        } else if (position == PAGE_MATCHES) {
            matchesPage.startUpdate();
        } else if (position == PAGE_REQUESTS) {
            requestPage.startUpdateRequests();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        // do nothing for now
    }

    public class MatchesPagerAdapter extends FragmentPagerAdapter {
        PreferenceFragment pFrag;
        MatchListFragment mFrag;
        RequestListFragment rFrag;

        public MatchesPagerAdapter(FragmentManager fm, Context context ){
            super(fm);
            this.context = context;

            pFrag = new PreferenceFragment();
            mFrag = new MatchListFragment();
            rFrag = new RequestListFragment();
        }

        @Override
        public Fragment getItem(int position) {
            if(position == PAGE_SEARCH){
                return pFrag;
            } else if (position == PAGE_MATCHES){
                return mFrag;
            } else if (position == PAGE_REQUESTS){
                return rFrag;
            }

            return new NoMatchFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        private int[] imageResId = {
                R.drawable.mag_icon_no_bg,
                R.drawable.list_icon_no_bg,
                R.drawable.exclaim_icon_yellow_no_bg,
                R.drawable.exclaim_icon_red_no_bg
        };

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = ContextCompat.getDrawable(this.context, imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth()-65, image.getIntrinsicHeight()-50);
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            String retVal ;
//            switch(position){
//                case 0:
//                    retVal = "Preference";
//                    break;
//                case 1:
//                    retVal = "Match List";
//                    break;
//                case 2:
//                    retVal = "Request List";
//                    break;
//                default:
//                    retVal = "position: " + (position+1);
//                    break;
//            }
//
//            return retVal;
//        }
    }
}

