package com.example.jefiro.barber.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreRepository<T> implements IFirestoreRepository<T> {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void create(String collection, String id, T data, OnCompleteListener<Void> listener) {
        db.collection(collection)
                .document(id)
                .set(data)
                .addOnCompleteListener(listener);
    }

    @Override
    public void update(String collection, String id, Map<String, Object> data, OnCompleteListener<Void> listener) {
        db.collection(collection)
                .document(id)
                .update(data)
                .addOnCompleteListener(listener);
    }


    @Override
    public void delete(String collection, String id, OnCompleteListener<Void> listener) {
        db.collection(collection)
                .document(id)
                .delete()
                .addOnCompleteListener(listener);
    }

    @Override
    public void getById(String collection, String id, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(collection)
                .document(id)
                .get()
                .addOnCompleteListener(listener);
    }

    @Override
    public void getAll(String collection, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(collection)
                .get()
                .addOnCompleteListener(listener);
    }

    @Override
    public void getSubDocument(String collection, String documentId, String subcollection, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(collection)
                .document(documentId)
                .collection(subcollection)
                .get()
                .addOnCompleteListener(listener);
    }

    @Override
    public void getSubDocument(String collection, String documentId, String subcollection, String subDocumentId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(collection)
                .document(documentId)
                .collection(subcollection)
                .document(subDocumentId)
                .get()
                .addOnCompleteListener(listener);
    }
}
