package br.com.luizgadao.selfdestruction.utils;

import android.util.Log;

/**
 * Created by luizcarlos on 24/02/15.
 */
public class LogUtils {


    public static void logInfo(String TAG, String info)
    {
        Log.i( TAG, info );
    }

    public static void logInfo(String info)
    {
        Log.i( "", info );
    }

    public static void logError(String TAG, String error)
    {
        Log.i( TAG, error );
    }


}
