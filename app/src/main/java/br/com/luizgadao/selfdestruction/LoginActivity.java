package br.com.luizgadao.selfdestruction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import br.com.luizgadao.selfdestruction.utils.Utils;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new LoginFragment() )
                    .commit();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_login, menu );
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
    public static class LoginFragment extends Fragment {

        private EditText etLogin, etPassword;
        private Button btLogin;

        public LoginFragment() {
        }

        @Override
        public View onCreateView( final LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_login, container, false );

            TextView tvSignUP = ( TextView ) rootView.findViewById( R.id.tv_signup );
            tvSignUP.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    startActivity( new Intent( getActivity(), SignUpActivity.class ) );
                }
            } );

            etLogin = ( EditText ) rootView.findViewById( R.id.et_login );
            etPassword = ( EditText ) rootView.findViewById( R.id.et_password );
            btLogin = ( Button ) rootView.findViewById( R.id.bt_login );

            btLogin.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    String login = etLogin.getText().toString();
                    login = login.trim();

                    String password = etPassword.getText().toString();
                    password = password.trim();

                    if ( login.isEmpty() | password.isEmpty() )
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                        builder.setTitle( R.string.login_error_title )
                                .setMessage( R.string.login_error_message )
                                .setPositiveButton( android.R.string.ok, null );

                        builder.create().show();
                    }
                    else {
                        //show progres dialog
                        final ProgressDialog progressDialog = Utils.createGenericProgressDialog( getActivity(),
                                getString( R.string.login ), getString( R.string.login_dialog_message ) );
                        progressDialog.show();

                        //login
                        ParseUser.logInInBackground( login, password, new LogInCallback() {

                            @Override
                            public void done( ParseUser parseUser, ParseException e ) {
                                //destroy progressbar
                                progressDialog.dismiss();

                                if ( e == null ) {
                                    //sucess
                                    Intent intent = new Intent( getActivity(), MainActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    startActivity( intent );
                                } else {
                                    //error
                                    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                                    builder.setTitle( R.string.login_error_title )
                                            .setMessage( e.getMessage() )
                                            .setPositiveButton( android.R.string.ok, null );

                                    builder.create().show();
                                }
                            }
                        } );

                    }


                }
            } );

            return rootView;
        }
    }
}
