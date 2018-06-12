package com.ynfante.crimer.FirebaseHelpers;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private FirebaseDatabase database;

    private DatabaseHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public static DatabaseHelper instance() {
        if (instance != null)
            return instance;

        instance = new DatabaseHelper();
        return instance;
    }

    public void storeUser(FirebaseUser user, String name) {

    }
}
