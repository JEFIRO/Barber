package com.example.jefiro.barber.repository;

public interface OnCallback<T> {
    void onSuccess(T result);

    void onFailure(String e);
}
