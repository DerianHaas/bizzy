package com.stuff.bizzy;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            List<Integer> indices = searchName(query);
//            List<Group> filtered = new ArrayList<>();
//            Group[] groups = GroupList.getGroupArray();
//            for (int i = 0; i < indices.size(); i++){
//                filtered.add(groups[indices.get(i)]);
//            }
//            MapScreen.adapter.clear();
//            MapScreen.adapter.addAll(filtered);
//            MapScreen.adapter.notifyDataSetChanged();
            new AlertDialog.Builder(this).setTitle("Error").setMessage("Sorry!  Searching is not yet enabled.").setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        }
    }



    private List<Integer> searchName(String name) {
        Group[] groups = GroupList.getGroupArray();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].getName().contains(name)) {
                indices.add(i);
            }
        }
        return indices;
    }


}
