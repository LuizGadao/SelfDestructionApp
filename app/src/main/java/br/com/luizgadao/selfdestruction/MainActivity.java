package br.com.luizgadao.selfdestruction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.util.Locale;

import br.com.luizgadao.selfdestruction.utils.LogUtils;
import br.com.luizgadao.selfdestruction.views.android.SlidingTabLayout;
import br.com.luizgadao.selfdestruction.views.fragments.Friends;
import br.com.luizgadao.selfdestruction.views.fragments.Inbox;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    SlidingTabLayout slidingTabLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ParseAnalytics.trackAppOpenedInBackground( getIntent() );

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ( currentUser == null ) {
            navigateToLogin();
        }
        else
        {
            LogUtils.logInfo( TAG, "current user: " + currentUser.getUsername() );
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        // Set up the ViewPager with the sections adapter.
        mViewPager = ( ViewPager ) findViewById( R.id.view_pager );
        mViewPager.setAdapter( mSectionsPagerAdapter );

        slidingTabLayout = ( SlidingTabLayout ) findViewById( R.id.sliding_tabs );
        slidingTabLayout.setViewPager( mViewPager );
    }

    private void navigateToLogin() {
        Intent intent = new Intent( this, LoginActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity( intent );
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //action logout
        if ( id == R.id.action_logout ) {
            ParseUser.logOut();
            navigateToLogin();
        }

        return super.onOptionsItemSelected( item );
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter( FragmentManager fm ) {
            super( fm );
        }

        @Override
        public Fragment getItem( int position ) {

            switch ( position )
            {
                case 0: return new Inbox();
                case 1: return new Friends();
            }

            return null;

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle( int position ) {
            Locale l = Locale.getDefault();
            switch ( position ) {
                case 0:
                    return "Inbox";
                case 1:
                    return "Friends";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance( int sectionNumber ) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt( ARG_SECTION_NUMBER, sectionNumber );
            fragment.setArguments( args );
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_main, container, false );

            int sectionNumber = getArguments().getInt( ARG_SECTION_NUMBER );

            TextView textView = ( TextView ) rootView.findViewById( R.id.section_label );
            textView.setText( "Section-" + sectionNumber );

            return rootView;
        }
    }

}
