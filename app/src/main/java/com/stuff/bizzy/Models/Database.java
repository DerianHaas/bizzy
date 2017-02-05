package com.stuff.bizzy.Models;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.R.attr.id;

/**
 * Created by Derian on 1/29/2017.
 */

public class Database {

    private String authToken;

    public Database(String authToken){
        this.authToken = authToken;
    }

    public List<Group> getGroups() throws IOException {
        try {
            InputStream response = httpGet("45.55.60.233/studysession/");
            JsonReader reader = new JsonReader(new InputStreamReader(response, "UTF-8"));
            return readGroupArray(reader);
        } catch (IOException e) {
            Log.e("Database", "Unable to get groups from server!");
            throw new IOException();
        }
    }

    private static List<Group> readGroupArray(JsonReader reader) throws IOException {
        List<Group> groups = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            groups.add(readGroup(reader));
        }
        reader.endArray();
        return groups;
    }

    private static Group readGroup(JsonReader reader) throws IOException {
        String groupName = null;
        String buildingName = null;
        String details = null;
        Set<User> people = new HashSet<>();
        int groupSize;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("entities")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    people.add(readUser(reader));
                }
            } else if (name.equals("building")) {
                buildingName = reader.nextString();
            } else if (name.equals("group name")) {
                groupName = reader.nextString();
            } else if (name.equals("details")) {
                details = reader.nextString();
            } else if (name.equals("group size")) {
                groupSize = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Group g = new Group(buildingName, groupName, details);
        for (User u : people) {
            g.addToGroup(u);
        }
        return g;
    }

    private static User readUser(JsonReader reader) throws IOException {
        String username = "";
        String email = "";
        String password = "";
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("username")) {
                username = reader.nextString();
            } else if (name.equals("password")) {
                password = reader.nextString();
            } else if (name.equals("email")) {
                email = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        return new User(email, username, password);
    }



    private static InputStream httpGet(String urlStr) throws IOException {
        URL url = new URL("http://"+urlStr);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        InputStream str = conn.getInputStream();

        conn.disconnect();
        return str;
    }




}
