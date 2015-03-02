package br.com.luizgadao.selfdestruction.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.com.luizgadao.selfdestruction.R;

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

    public static String getTimeCreated( Date date, Resources res )
    {
        Date now = new Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime( now );

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime( date );
        Date past = cal2.getTime();

        long diff = now.getTime() - past.getTime();
        long minute = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hour = TimeUnit.MILLISECONDS.toHours(diff);
        long day = TimeUnit.MILLISECONDS.toDays(diff);
        long week = ( long ) Math.floor( day / 7 );
        long month = ( long ) Math.floor( day / 30 );

        String plurals = res.getQuantityString( R.plurals.month, ( int ) month );
        if ( month >= 1 ) return month + " " + plurals;

        plurals = res.getQuantityString( R.plurals.week, ( int ) week );
        if ( week >= 1 ) return week + " " + plurals;

        plurals = res.getQuantityString( R.plurals.day, ( int ) day );
        if ( day >= 1 ) return day + " " + plurals;

        plurals = res.getQuantityString( R.plurals.hour, ( int ) hour );
        if ( hour >= 1 ) return hour + " " + plurals;

        plurals = res.getQuantityString( R.plurals.minute, ( int ) day );
        return minute + " " + plurals;
    }

}
