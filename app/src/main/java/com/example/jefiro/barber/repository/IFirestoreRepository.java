package com.example.jefiro.barber.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public interface IFirestoreRepository<T> {
    void create(String collection, String id, T data, OnCompleteListener<Void> listener);

    void update(String collection, String id, Map<String, Object> data, OnCompleteListener<Void> listener);


    void delete(String collection, String id, OnCompleteListener<Void> listener);

    void getById(String collection, String id, OnCompleteListener<DocumentSnapshot> listener);

    void getAll(String collection, OnCompleteListener<QuerySnapshot> listener);

    void getSubDocument(String collection, String documentId, String subcollection, OnCompleteListener<QuerySnapshot> listener);
    void getSubDocument(String collection, String documentId, String subcollection, String subDocumentId, OnCompleteListener<DocumentSnapshot> listener);


}
