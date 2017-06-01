package com.stuff.bizzy.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.stuff.bizzy.Models.ConnectionClass;
import com.stuff.bizzy.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLTestActivity extends AppCompatActivity {

    private ConnectionClass connectionClass;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqltest);

        result = (TextView) findViewById(R.id.result);

        connectionClass = new ConnectionClass();

    }

    public void onButtonClick(View v) {
        TextView numField = (TextView)findViewById(R.id.id);
        if (numField.getText().toString().isEmpty()) {
            Toast.makeText(SQLTestActivity.this, "Please enter a number", Toast.LENGTH_SHORT).show();
        } else {
            int id = Integer.parseInt(numField.getText().toString());
            GetUsersInGroup query = new GetUsersInGroup();
            query.execute(id);
        }


    }

    class GetUsersInGroup extends AsyncTask<Integer, String, List<String>> {

        @Override
        protected void onPostExecute(List<String> r) {
            result.setText(r.toString());
        }

        @Override
        protected List<String> doInBackground(Integer... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    Log.e("Connection", "Couldn't connect to the database.");
                    return new ArrayList<>();
                } else {
                    String query = "EXECUTE [dbo].[uspUsersInGroup] @intGroupID = "+params[0];
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    List<String> data = new ArrayList<>();
                    while (rs.next()) {
                         data.add(rs.getString("DisplayName"));
                    }
                    return data;
                }
            } catch (SQLException e) {
                Log.e("Connection", "SQL Exception Occurred: "+e.getMessage());
                return new ArrayList<>();
            }
        }
    }
}
