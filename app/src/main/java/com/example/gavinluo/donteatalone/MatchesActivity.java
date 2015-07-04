package com.example.gavinluo.donteatalone;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

// Reference : http://developer.android.com/training/implementing-navigation/lateral.html#tabs

public class MatchesActivity extends ActionBarActivity
        implements NoMatchFragment.OnFragmentInteractionListener,
        MatchListFragment.OnFragmentInteractionListener
{



    MatchesPagerAdapter mMatchesPageAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        // ViewPager and its adapter use support library
        // fragments, so use getSupportFragmentManager.
        mMatchesPageAdapter = new MatchesPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.matches_pager);
        mViewPager.setAdapter(mMatchesPageAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        // do nothing for now
    }

    public class MatchesPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 3;
        private static final int PAGE_SEARCH = 0;
        private static final int PAGE_MATCHES = 1;
        private static final int PAGE_REQUESTS = 2;

        public MatchesPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // TODO: change the fragment to be the correct ones
            if(position == PAGE_SEARCH) {
                return new NoMatchFragment();
            } else if (position == PAGE_MATCHES) {
                return new MatchListFragment();
            } else if (position == PAGE_REQUESTS){
                return new MatchListFragment();
            }

            return new NoMatchFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "position: " + (position+1);
        }
    }
}

