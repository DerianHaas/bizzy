package com.stuff.bizzy.Activities;




import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.stuff.bizzy.R;

/**
 * Created by jakan on 12/20/2016.
 */

public class LoginScreenActivity extends AppCompatActivity {

    Button signUp;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen_activty);


        signUp = (Button)findViewById(R.id.signUpSegueButton);
        signIn = (Button)findViewById(R.id.signInSegueBitton);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(i);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(j);
            }
        });
    }

}
