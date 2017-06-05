package com.stuff.bizzy.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.R;

import static com.stuff.bizzy.Models.Database.mAuth;


/**
 * Created by jakan on 12/22/2016.
 */


public class SignUpActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    EditText displayText;

    Button signUpButton;

    private static final String TAG = "SignIn";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Database.initialize();

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordtext);
        displayText = (EditText) findViewById(R.id.nameText);

        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailText.getText().toString().trim();
                if (!email.contains("@gatech.edu")) {
                    new AlertDialog.Builder(SignUpActivity.this).setTitle("Error").setMessage("Please enter a valid \"@gatech.edu\" email.")
                            .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    String password = passwordText.getText().toString().trim();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
//                                String user =  task.getResult().getUser().getUid();
//                                Connection con = new ConnectionClass().CONN();
//                                if (con == null) {
//                                    Log.e("Connection", "Couldn't connect to the database.");
//                                } else {
//                                    String update = "INSERT INTO dbo.tblUsers (UID, LastName, FirstName, DisplayName) VALUES ('"
//                                            +user+"', 'Haas', 'Derian', '"+displayText.getText().toString().trim()+"')";
//                                    try {
//                                        con.createStatement().executeUpdate(update);
                                        Intent i = new Intent(getApplicationContext(), MapScreen.class);
                                        startActivity(i);
//                                    }catch (SQLException e) {
//                                        Log.e("Connection", "SQL Exception Occurred: "+e.getMessage());
//                                    }
//                                }
                            }
                        }
                    });
                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Database.addListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        Database.removeListener();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon action bar is clicked; go to parent activity
                this.finish();
                return true;
            case R.id.activity_login:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
