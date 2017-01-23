package com.stuff.bizzy.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stuff.bizzy.R;


public class SignInActivity extends AppCompatActivity {

    EditText username, password;

    TextView login;

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = (EditText)findViewById(R.id.userNameText);
        password = (EditText)findViewById(R.id.passwordText);

        loginButton = (Button)findViewById(R.id.loginButton);

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
        Intent j = new Intent(getApplicationContext(), MapScreen.class);
        startActivity(j);
    }
}
