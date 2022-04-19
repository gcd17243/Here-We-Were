package com.example.herewewere.databases;



import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FBDatabasePost {
    private DatabaseReference databaseReference;
    public FBDatabasePost()
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(FBPost.class.getSimpleName());

    }
    public Task<Void> add(FBPost post)
    {

        return databaseReference.push().setValue(post);

    }
    public Task<Void> update(String key, HashMap<String,Object>hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> delete(String key)
    {
       return databaseReference.child(key).removeValue();
    }
}
