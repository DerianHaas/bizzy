package com.stuff.bizzy.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stuff.bizzy.R;

/**
 * Created by jakan on 2/5/2017.
 */

public class ProfileActivity extends AppCompatActivity {

    private TextView profileText;
    private TextView yearText;

    private Button coursesButton;
    private Button editProfile;

    private ImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen_activity);

        profileText = (TextView)findViewById(R.id.profileNameText);
        yearText = (TextView)findViewById(R.id.yearText);

        coursesButton = (Button)findViewById(R.id.courseButton);
        editProfile = (Button)findViewById(R.id.editProfileButton);

        profileImage = (ImageView)findViewById(R.id.profileImageView);





    }

}