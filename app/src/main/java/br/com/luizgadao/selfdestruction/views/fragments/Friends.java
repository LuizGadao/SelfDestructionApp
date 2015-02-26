package br.com.luizgadao.selfdestruction.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

import java.util.List;

import br.com.luizgadao.selfdestruction.R;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;

/**
 * Created by luizcarlos on 25/02/15.
 */
public class Friends extends ListFragment {

    private static final String TAG = Friends.class.getSimpleName();

    private List<ParseUser> mFriends;
    private ParseUser mCurrentUser;
    private ParseRelation<ParseUser> mFriendsRelation;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.fragment_friends, container, false );

        return rootView;
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
                            android.R.layout.simple_list_item_1, namesUser );

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
        //TODO open details user
        super.onListItemClick( l, v, position, id );
    }
}
