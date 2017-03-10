package com.stuff.bizzy.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.Models.User;
import com.stuff.bizzy.R;


public class SignInActivity extends AppCompatActivity {

    EditText userText, passText;

    TextView login;

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        userText = (EditText)findViewById(R.id.userNameText);
        passText = (EditText)findViewById(R.id.passwordText);
        userText.requestFocus();
        loginButton = (Button)findViewById(R.id.loginButton);

        Database.initialize();

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

    public void onLoginClicked(View v) {
        String email = userText.getText().toString().trim();
        String password = passText.getText().toString().trim();
        Database.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("EmailPassword", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("SignIn", "signInWithEmail", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent j = new Intent(getApplicationContext(), MapScreen.class);
                            startActivity(j);
                        }
                    }
                });
    }


}
