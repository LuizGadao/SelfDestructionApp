package br.com.luizgadao.selfdestruction.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

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

    public static boolean isExternalStorageAvailable()
    {
        String state = Environment.getExternalStorageState();
        return state.equals( Environment.MEDIA_MOUNTED );
    }

}
