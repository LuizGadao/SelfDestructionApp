package br.com.luizgadao.selfdestruction.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by luizcarlos on 24/02/15.
 */
public class Utils {

    public static ProgressDialog createGenericProgressDialog( Context context, String title, String message )
    {
        ProgressDialog progressDialog = new ProgressDialog( context );
        progressDialog.setTitle( title );
        progressDialog.setMessage( message );
        progressDialog.setCancelable( false );

        return progressDialog;
    }

}
