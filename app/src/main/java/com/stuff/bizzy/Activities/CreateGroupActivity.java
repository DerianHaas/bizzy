package com.stuff.bizzy.Activities;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.stuff.bizzy.Models.Database;
import com.stuff.bizzy.Models.Group;
import com.stuff.bizzy.Models.User;
import com.stuff.bizzy.R;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText nameText, detailsText;
    private String buildingName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        buildingName = getIntent().getStringExtra("building");
        TextView label = (TextView)findViewById(R.id.label);
        label.setText(label.getText() + " in " + buildingName);

        nameText = (EditText) findViewById(R.id.groupName);
        detailsText = (EditText) findViewById(R.id.details);
    }

    public void onCreateClicked(View v) {
//        Group g = new Group(buildingName, nameText.getText().toString().trim(), detailsText.getText().toString());
//        DatabaseReference ref = Database.getReference("groups");
//        String key = ref.push().getKey();
//        ref.child(key).setValue(g.toMap());
//        ref.child(key).child("users").push().setValue(Database.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    DatabaseReference userref = Database.getReference("users/"+Database.currentUser.getUid());
//                    userref.child("inGroup").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            finish();
//                        }
//                    });
//                } else {
//                    Toast.makeText(getApplicationContext(), "Error creating group.", Toast.LENGTH_LONG);
//                }
//            }
//        });
    }
}
