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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import br.com.luizgadao.selfdestruction.utils.ParseConstants;
import br.com.luizgadao.selfdestruction.utils.Utils;


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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SignUpFragment extends Fragment {

        protected EditText etLogin, etPassword, etEmail, etFirstName, etLastName, etHometown, etWebsite;
        protected Button btSignUp;

        public SignUpFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_sign_up, container, false );

            etLogin = ( EditText ) rootView.findViewById( R.id.et_login );
            etFirstName = ( EditText ) rootView.findViewById( R.id.et_first_name );
            etLastName = ( EditText ) rootView.findViewById( R.id.et_last_name );
            etHometown = ( EditText ) rootView.findViewById( R.id.et_hometown );
            etWebsite = ( EditText ) rootView.findViewById( R.id.et_web );
            etPassword = ( EditText ) rootView.findViewById( R.id.et_password );
            etEmail = ( EditText ) rootView.findViewById( R.id.et_email );
            btSignUp = ( Button ) rootView.findViewById( R.id.bt_signup );


            btSignUp.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    String login = etLogin.getText().toString();
                    String firstName = etFirstName.getText().toString();
                    String lastName = etLastName.getText().toString();
                    String hometown = etHometown.getText().toString();
                    String site = etWebsite.getText().toString();
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

                        final ProgressDialog progressDialog = Utils.createGenericProgressDialog( getActivity(),
                                getString( R.string.signup ), getString( R.string.signup_dialog_message ) );
                        progressDialog.show();

                        //create new user
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername( login );
                        newUser.setPassword( password );
                        newUser.setEmail( email );
                        newUser.put( ParseConstants.KEY_FIRST_NAME, firstName );
                        newUser.put( ParseConstants.KEY_LAST_NAME, lastName );
                        newUser.put( ParseConstants.KEY_HOMETOWN, hometown );
                        newUser.put( ParseConstants.KEY_WEB_SITE, site );

                        newUser.signUpInBackground( new SignUpCallback() {
                            @Override
                            public void done( ParseException e ) {

                                progressDialog.dismiss();

                                //success
                                if ( e == null )
                                {
                                    Intent intent = new Intent( getActivity(), MainActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );

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
