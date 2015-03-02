package br.com.luizgadao.selfdestruction.views.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.List;

import br.com.luizgadao.selfdestruction.R;
import br.com.luizgadao.selfdestruction.adapter.MessageAdapter;
import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.views.ViewImageActivity;

/**
 * Created by luizcarlos on 25/02/15.
 */
public class Inbox extends ListFragment {

    private List<ParseObject> mMessages;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.fragment_inbox, container, false );
        
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>( ParseConstants.CLASS_MESSAGE );
        query.whereEqualTo( ParseConstants.KEY_FRIENDS_RELATION, ParseUser.getCurrentUser().getObjectId() );
        query.orderByAscending( ParseConstants.KEY_CREATED_AT );
        query.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done( List<ParseObject> messages, ParseException e ) {
                if ( e == null )
                {
                    //found messages
                    mMessages = messages;
                    String[] userName = new String[mMessages.size()];
                    int i = 0;
                    for ( ParseObject message : messages )
                    {
                        userName[i] = message.getString( ParseConstants.KEY_SENDER_NAME );
                        i++;
                    }

                    Collections.reverse( mMessages );
                    MessageAdapter adapter = new MessageAdapter( getActivity(), mMessages );
                    setListAdapter(adapter);
                }
            }
        } );
    }

    @Override
    public void onListItemClick( ListView l, View v, int position, long id ) {
        super.onListItemClick( l, v, position, id );

        ParseObject message = mMessages.get( position );
        String messageType = message.getString( ParseConstants.KEY_FILE_TYPE );
        ParseFile file = message.getParseFile( ParseConstants.KEY_FILE );
        Uri uri = Uri.parse( file.getUrl() );

        if ( messageType.equals( ParseConstants.KEY_FILE_IMAGE ) )
        {
            //image
            Intent intent = new Intent( getActivity(), ViewImageActivity.class );
            intent.setData( uri );
            startActivity( intent );
        }
        else
        {
            //video
            Intent intent = new Intent( Intent.ACTION_VIEW, uri );
            intent.setDataAndType( uri, "video/*" );
            startActivity( intent );
        }
    }
}
