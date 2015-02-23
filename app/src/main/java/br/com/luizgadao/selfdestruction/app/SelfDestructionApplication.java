package br.com.luizgadao.selfdestruction.app;

import android.app.Application;

/**
 * Created by luizcarlos on 23/02/15.
 */
public class SelfDestructionApplication extends Application {

    private Application app;


    public Application GetInstance(){
        if ( app == null )
            new SelfDestructionApplication();

        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //Parse.initialize(this, "R1C0CHL3n9KS47AxRF89H3k4EoSABnAfXP4nUlXM", "8rEFQOgXtXBiZipxDxswF7DUYhQAMI26oj6iWNZj");
    }
}
