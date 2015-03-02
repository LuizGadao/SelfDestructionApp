package br.com.luizgadao.selfdestruction.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.luizgadao.selfdestruction.R;

public class ViewImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_image );
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new ViewImageFragment() )
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ViewImageFragment extends Fragment {

        public ViewImageFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_view_image, container, false );
            ImageView imageView = ( ImageView ) rootView.findViewById( R.id.image_view );

            Uri uri = getActivity().getIntent().getData();

            Picasso.with( getActivity() ).load( uri.toString() ).into( imageView );

            return rootView;
        }
    }
}
