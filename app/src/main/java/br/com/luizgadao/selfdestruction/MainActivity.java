package br.com.luizgadao.selfdestruction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.luizgadao.selfdestruction.utils.LogUtils;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;
import br.com.luizgadao.selfdestruction.views.android.SlidingTabLayout;
import br.com.luizgadao.selfdestruction.views.fragments.Friends;
import br.com.luizgadao.selfdestruction.views.fragments.Inbox;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO = 0;
    public static final int TAKE_VIDEO = 1;
    public static final int PICK_PHOTO = 2;
    public static final int PICK_VIDEO = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; //10MB

    Uri mediaUri;

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

    DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener(){
        @Override
        public void onClick( DialogInterface dialog, int which ) {
            switch ( which )
            {
                case 0:
                    //take picture
                    Intent takePhotoIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                    mediaUri = getOutputMediaFileUri( MEDIA_TYPE_IMAGE );
                    if ( mediaUri == null ) {
                        Toast.makeText( getApplicationContext(),
                                R.string.none_external_storage, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        takePhotoIntent.putExtra( MediaStore.EXTRA_OUTPUT, mediaUri );
                        startActivityForResult( takePhotoIntent, TAKE_PHOTO );
                    }
                    break;

                case 1:
                    //take video
                    Intent takeVideoIntent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE );
                    mediaUri = getOutputMediaFileUri( MEDIA_TYPE_VIDEO );
                    if ( mediaUri == null ) {
                        Toast.makeText( getApplicationContext(),
                                R.string.none_external_storage, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        int seconds = 10;
                        int quality = 0; //0 to low and 1 to hight;
                        takeVideoIntent.putExtra( MediaStore.EXTRA_OUTPUT, mediaUri );
                        takeVideoIntent.putExtra( MediaStore.EXTRA_DURATION_LIMIT, seconds );
                        takeVideoIntent.putExtra( MediaStore.EXTRA_VIDEO_QUALITY, quality );
                        startActivityForResult( takeVideoIntent, TAKE_VIDEO );
                    }
                    break;

                case 2:
                    //choose picture
                    Intent intentChoosePicture = new Intent( Intent.ACTION_GET_CONTENT );
                    intentChoosePicture.setType( "image/*" );
                    startActivityForResult( intentChoosePicture, PICK_PHOTO );
                    break;

                case 3:
                    //choose video
                    Intent intentChooseVideo = new Intent( Intent.ACTION_GET_CONTENT );
                    intentChooseVideo.setType( "video/*" );
                    Toast.makeText( getApplicationContext(), R.string.select_video, Toast.LENGTH_LONG ).show();
                    startActivityForResult( intentChooseVideo, PICK_VIDEO );
                    break;
            }
        }
    };

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
        slidingTabLayout.setDividerColors( R.color.primary_color );
        slidingTabLayout.setSelectedIndicatorColors( getResources().getColor( R.color.primary_color ) );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        if ( resultCode == RESULT_OK )
        {
            if ( requestCode == PICK_PHOTO || requestCode == PICK_VIDEO )
            {
                if ( data == null ) {
                    Toast.makeText( this, R.string.general_error, Toast.LENGTH_LONG ).show();
                    return;
                }

                mediaUri = data.getData();

                LogUtils.logInfo( TAG, "Media URI: " + mediaUri );
                if ( requestCode == PICK_VIDEO )
                {
                    //make sure the file is less than 10MB
                    int fileSize = 0;

                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream( mediaUri );
                        fileSize = inputStream.available();
                    } catch ( FileNotFoundException e ) {
                        Toast.makeText( this, R.string.select_file_error, Toast.LENGTH_LONG ).show();
                        return;
                    } catch ( IOException e ) {
                        Toast.makeText( this, R.string.select_file_error, Toast.LENGTH_LONG ).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch ( IOException e ) {
                            e.printStackTrace();
                        }
                    }

                    if ( fileSize >= FILE_SIZE_LIMIT ) {
                        Toast.makeText( this, R.string.error_file_size_too_large, Toast.LENGTH_LONG ).show();
                        return;
                    }

                }
            }
            else
            {
                //share media
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
                mediaScanIntent.setData( mediaUri );
                sendBroadcast( mediaScanIntent );
            }

            //open intent with friends to send data
            Intent intentRecipients = new Intent( this, ChooseRecipientsActivity.class );
            intentRecipients.setData( mediaUri );

            String fileType = "";
            if ( requestCode == PICK_PHOTO || requestCode == TAKE_PHOTO )
                fileType = ParseConstants.KEY_FILE_IMAGE;
            else if ( requestCode == PICK_VIDEO || requestCode == TAKE_VIDEO )
                fileType = ParseConstants.KEY_FILE_VIDEO;

            intentRecipients.putExtra( ParseConstants.KEY_FILE_TYPE, fileType );
            startActivity( intentRecipients );
        }
        else if ( resultCode == RESULT_CANCELED ) {
            //Toast.makeText( this, R.string.general_error, Toast.LENGTH_LONG ).show();
        }
    }

    private Uri getOutputMediaFileUri( int mediaType ) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if ( Utils.isExternalStorageAvailable() )
        {
            // 1 - Get external storage director
            String appName = getString( R.string.app_name );
            File mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES ), appName);

            // 2 - create our subdirectory
            if ( ! mediaStorageDir.exists() ) {
                if ( ! mediaStorageDir.mkdirs() ) {
                    LogUtils.logError( TAG, "error to create directory" );
                    return null;
                }
            }

            // 3 - create a file name
            // 4 - create file
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat( "yyyyMMdd_HHmmss", Locale.US ).format( now );
            String path = mediaStorageDir.getPath() + File.separator;

            if ( mediaType == MEDIA_TYPE_IMAGE )
            {
                mediaFile = new File( path + "IMG_" + timestamp + ".jpg" );
            }
            else if ( mediaType == MEDIA_TYPE_VIDEO )
            {
                mediaFile = new File( path + "VID_" + timestamp + ".mp4" );
            }
            else
                return null;

            LogUtils.logError( TAG, Uri.fromFile( mediaFile ).toString() );

            // 5 - return the file Uri
            return Uri.fromFile( mediaFile );
        }

        return null;
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
        switch ( id ) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;

            case R.id.action_edit_friends:
                startActivity( new Intent( this, EditFriendsActivity.class ) );
                break;

            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder( this );
                builder.setTitle( getString( R.string.app_name ) );
                builder.setItems( R.array.camera_choose, dialogInterface );
                builder.create().show();
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
