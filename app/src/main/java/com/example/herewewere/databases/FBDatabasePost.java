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
        databaseReference = db.getReference(FBComment.class.getSimpleName());
        databaseReference = db.getReference(FBReport.class.getSimpleName());


    }
    public Task<Void> add(FBPost post)
    {

        return databaseReference.push().setValue(post);

    }
    public Task<Void> update(String key, HashMap<String,Object>hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> add(FBComment comment)
    {

        return databaseReference.push().setValue(comment);

    }
    public Task<Void> add(FBReport report)
    {

        return databaseReference.push().setValue(report);

    }
    public Task<Void> delete(String key)
    {
       return databaseReference.child(key).removeValue();
    }
}
