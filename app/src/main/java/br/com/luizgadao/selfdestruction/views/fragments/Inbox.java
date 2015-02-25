package br.com.luizgadao.selfdestruction.views.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.luizgadao.selfdestruction.R;

/**
 * Created by luizcarlos on 25/02/15.
 */
public class Inbox extends ListFragment {

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View rootView = inflater.inflate( R.layout.fragment_inbox, container, false );
        
        return rootView;
    }
}
