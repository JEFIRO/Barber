package com.example.jefiro.barber.service;

public interface OnDistanceCallback {
    void onSucefull(double distanceKm);
    void onError(String error);

}
