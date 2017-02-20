package com.stuff.bizzy.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.Models.User;
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

    private FirebaseUser user;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileText = (TextView)findViewById(R.id.profileNameText);
        yearText = (TextView)findViewById(R.id.yearText);

        coursesButton = (Button)findViewById(R.id.courseButton);
        editProfile = (Button)findViewById(R.id.editProfileButton);

        profileImage = (ImageView)findViewById(R.id.profileImageView);

        user = Database.currentUser;
        if (user != null) {
            ref = Database.getReference("users/"+user.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    if (u.getDisplayName() == null || u.getDisplayName().isEmpty()) {
                        profileText.setText(u.getEmail());
                    } else {
                        profileText.setText(u.getDisplayName());
                    }
                    profileImage.setImageURI(u.getImageURL());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }

    public void onDisplayNameClick(View v) {
        final Context c = v.getContext();
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View editView = inflater.inflate(R.layout.edit_display_name_layout, null);
        final AlertDialog popup = new AlertDialog.Builder(v.getContext()).setView(editView).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
        popup.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = ((EditText)editView.findViewById(R.id.newName)).getText().toString().trim();
                if (!newName.isEmpty()) {
                    ref.child("displayName").setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(c, "Display name updated", Toast.LENGTH_SHORT).show();
                                popup.dismiss();
                            } else {
                                Toast.makeText(c, "Error updating display name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(c, "Please enter a new display name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}