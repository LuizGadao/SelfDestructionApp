package br.com.luizgadao.selfdestruction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.luizgadao.selfdestruction.utils.FileHelper;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;


public class ChooseRecipientsActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_choose_recipients );
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new ChooseRecipientsFragment() )
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ChooseRecipientsFragment extends ListFragment {

        private static final String TAG = ChooseRecipientsFragment.class.getSimpleName();

        private List<ParseUser> mFriends;
        private ParseUser mCurrentUser;
        private ParseRelation<ParseUser> mFriendsRelation;
        private Uri mediaUri;
        private String fileType = "";

        protected MenuItem menuItemSend;
        private ProgressDialog progressDialog;

        public ChooseRecipientsFragment() {
        }

        @Override
        public void onCreate( Bundle savedInstanceState ) {
            super.onCreate( savedInstanceState );

            mediaUri = getActivity().getIntent().getData();
            fileType = getActivity().getIntent().getStringExtra( ParseConstants.KEY_FILE_TYPE );
            setHasOptionsMenu( true );
        }

        @Override
        public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
            super.onCreateOptionsMenu( menu, inflater );
            inflater.inflate( R.menu.menu_choose_recipients, menu );

            menuItemSend = menu.getItem( 0 );
        }

        @Override
        public boolean onOptionsItemSelected( MenuItem item ) {

            int menuId = item.getItemId();
            switch ( menuId )
            {
                case R.id.action_send:

                    progressDialog = Utils.createGenericProgressDialog( getActivity(),
                            getString( R.string.app_name ), "Sending file..." );
                    progressDialog.show();

                    ParseObject message = createMessage();
                    if ( message == null )
                    {
                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                        builder.setTitle( R.string.error_select_file_title )
                                .setMessage( R.string.error_to_send_file )
                                .setPositiveButton( android.R.string.ok, null );
                        builder.create().show();
                    }
                    else {
                        send( message );
                    }

                    return true;

            }

            return super.onOptionsItemSelected( item );
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_choose_recipients, container, false );
            return rootView;
        }

        @Override
        public void onViewCreated( View view, Bundle savedInstanceState ) {
            getListView().setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
            super.onViewCreated( view, savedInstanceState );
        }

        @Override
        public void onResume() {
            super.onResume();

            final ProgressDialog progressDialog = Utils.createGenericProgressDialog( getActivity(),
                    getString( R.string.title_loading_user ), getString( R.string.loading ) );
            progressDialog.show();

            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser.getRelation( ParseConstants.KEY_FRIENDS_RELATION );

            ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
            query.orderByAscending( ParseConstants.KEY_USERNAME );
            query.setLimit( 1000 );

            query.findInBackground( new FindCallback<ParseUser>() {
                @Override
                public void done( List<ParseUser> parseUsers, ParseException e ) {

                    if ( e == null ) {
                        //success
                        mFriends = parseUsers;
                        int len = mFriends.size();
                        int i = 0;
                        String[] namesUser = new String[ len ];

                        for ( ParseUser user : mFriends ) {
                            // not show current user in the list
                            //boolean isCurrentUser = mCurrentUser.getObjectId().equals( user.getObjectId() );

                            namesUser[ i ] = user.getUsername();

                            i++;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(),
                                android.R.layout.simple_list_item_checked, namesUser );

                        setListAdapter( adapter );
                        progressDialog.dismiss();
                    } else {
                        // some error
                        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                        builder.setTitle( R.string.error_title )
                                .setMessage( e.getMessage() )
                                .setPositiveButton( android.R.string.ok, null );

                        builder.create().show();
                    }
                }
            } );
        }

        @Override
        public void onListItemClick( ListView l, View v, int position, long id ) {
            super.onListItemClick( l, v, position, id );

            menuItemSend.setVisible( l.getCheckedItemCount() > 0 );
        }

        private ParseObject createMessage()
        {
            ParseUser currentUser = ParseUser.getCurrentUser();

            ParseObject message = new ParseObject( ParseConstants.CLASS_MESSAGE );
            message.put( ParseConstants.KEY_SENDER_ID, currentUser.getObjectId() );
            message.put( ParseConstants.KEY_SENDER_NAME, currentUser.getUsername() );
            message.put( ParseConstants.KEY_FRIENDS_RELATION, getRecipientsIds() );
            message.put( ParseConstants.KEY_FILE_TYPE, fileType );

            byte[] fileBytes = FileHelper.getByteArrayFromFile( getActivity(), mediaUri );
            if ( fileBytes == null )
                return null;
            else
            {
                if ( fileType.equals( ParseConstants.KEY_FILE_IMAGE ) )
                    fileBytes = FileHelper.reduceImageForUpload( fileBytes );

                String fileName = FileHelper.getFileName( getActivity(), mediaUri, fileType );
                ParseFile parseFile = new ParseFile( fileName, fileBytes );
                message.put( ParseConstants.KEY_FILE, parseFile );

                return message;
            }
        }

        public ArrayList<String> getRecipientsIds() {
            ArrayList<String> recipientsIds = new ArrayList<>();

            for ( int i = 0; i < getListView().getCount(); i++ )
            {
                if ( getListView().isItemChecked( i ) )
                {
                    recipientsIds.add( mFriends.get( i ).getObjectId() );
                }
            }

            return recipientsIds;
        }

        private void send( ParseObject message ) {
            message.saveInBackground( new SaveCallback() {
                @Override
                public void done( ParseException e ) {
                    progressDialog.dismiss();
                    if ( e == null ) {
                        //success
                        Toast.makeText( getActivity(),
                                R.string.success_message, Toast.LENGTH_LONG ).show();

                        Timer timer = new Timer();
                        timer.schedule( new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().finish();
                            }
                        }, Toast.LENGTH_LONG );
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                        builder.setTitle( R.string.error_sending_message )
                                .setMessage( e.getMessage() )
                                .setPositiveButton( android.R.string.ok, null );

                        builder.create().show();
                    }
                }
            });
        }
    }
}
