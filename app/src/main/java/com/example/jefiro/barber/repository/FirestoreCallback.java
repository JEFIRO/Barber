package com.example.jefiro.barber.repository;

public interface FirestoreCallback<T> {
    void onSuccess(T result);

    void onFailure(Exception e);
}
