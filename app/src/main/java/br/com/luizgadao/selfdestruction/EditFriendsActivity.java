package br.com.luizgadao.selfdestruction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import br.com.luizgadao.selfdestruction.utils.LogUtils;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;


public class EditFriendsActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_friends );
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new EditFriendsFragment() )
                    .commit();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_edit_friends, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
    */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EditFriendsFragment extends ListFragment {

        private static final String TAG = EditFriendsFragment.class.getSimpleName();

        private List<ParseUser> mUsers;
        private ParseUser mCurrentUser;
        private ParseRelation<ParseUser> mFriendsRelation;

        public EditFriendsFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_edit_friends, container, false );
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

            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser.getRelation( ParseConstants.KEY_FRIENDS_RELATION );

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.orderByAscending( ParseConstants.KEY_USERNAME );
            query.setLimit( 1000 );

            final ProgressDialog progressDialog = Utils.createGenericProgressDialog( getActivity(),
                    getString( R.string.title_loading_user ), getString( R.string.loading ) );
            progressDialog.show();

            query.findInBackground( new FindCallback<ParseUser>() {
                @Override
                public void done( List<ParseUser> parseUsers, ParseException e ) {

                    if ( e == null )
                    {
                        //success
                        mUsers = parseUsers;
                        int len = mUsers.size();
                        int i = 0;
                        String[] namesUser = new String[len];

                        for ( ParseUser user : mUsers ) {
                            // not show current user in the list
                            //boolean isCurrentUser = mCurrentUser.getObjectId().equals( user.getObjectId() );

                            namesUser[ i ] = user.getUsername();

                            i++;
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(),
                                android.R.layout.simple_list_item_checked, namesUser );

                        setListAdapter( adapter );
                        progressDialog.dismiss();
                        addFriendCheckMark();
                    }
                    else
                    {
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

            if ( getListView().isItemChecked( position ) )
                //add
                mFriendsRelation.add( mUsers.get( position ) );
            else //remove
                mFriendsRelation.remove( mUsers.get( position ) );

            mCurrentUser.saveInBackground( new SaveCallback() {
                @Override
                public void done( ParseException e ) {
                    if ( e != null )
                        LogUtils.logError( TAG, e.getMessage() );
                }
            } );
        }

        private void addFriendCheckMark() {
            mFriendsRelation.getQuery().findInBackground( new FindCallback<ParseUser>() {
                @Override
                public void done( List<ParseUser> friends, ParseException e ) {

                    if ( e == null )
                    {
                        for ( ParseUser friend : friends )
                        {
                            int len = mUsers.size();
                            for ( int i = 0; i < len; i++ )
                            {
                                if ( friend.getObjectId().equals( mUsers.get( i ).getObjectId() ) )
                                    getListView().setItemChecked( i, true );
                            }
                        }
                    }
                    else
                    {
                        LogUtils.logError( TAG, e.getMessage() );
                    }

                }
            } );
        }
    }
}
