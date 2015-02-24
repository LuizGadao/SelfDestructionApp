package br.com.luizgadao.selfdestruction;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends ActionBarActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );
        if ( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new SignUpFragment() )
                    .commit();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_sign_up, menu );
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
    public static class SignUpFragment extends Fragment {

        protected EditText etLogin, etPassword, etEmail;
        protected Button btSignUp;

        public SignUpFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_sign_up, container, false );

            etLogin = ( EditText ) rootView.findViewById( R.id.et_login );
            etPassword = ( EditText ) rootView.findViewById( R.id.et_password );
            etEmail = ( EditText ) rootView.findViewById( R.id.et_email );
            btSignUp = ( Button ) rootView.findViewById( R.id.bt_signup );


            btSignUp.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    String login = etLogin.getText().toString();
                    String password = etPassword.getText().toString();
                    String email = etEmail.getText().toString();

                    login = login.trim();
                    password = password.trim();
                    email = email.trim();

                    if ( login.isEmpty() | email.isEmpty() | email.isEmpty() )
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle( R.string.signup_error_title )
                                .setMessage( R.string.signup_error_message )
                                .setCancelable( true )
                                .setPositiveButton( android.R.string.ok, null );

                        builder.create().show();
                    }else{
                        //create new user
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername( login );
                        newUser.setPassword( password );
                        newUser.setEmail( email );

                        newUser.signUpInBackground( new SignUpCallback() {
                            @Override
                            public void done( ParseException e ) {

                                //success
                                if ( e == null )
                                {
                                    Intent intent = new Intent( getActivity(), MainActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

                                    startActivity( intent );
                                }
                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                                    builder.setTitle( R.string.signup_error_title )
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
