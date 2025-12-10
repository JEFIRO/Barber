package com.example.jefiro.barber.barbearia;

import com.google.firebase.firestore.DocumentSnapshot;

public class BarbeariaComDistancia {
    private DocumentSnapshot doc;
    private double distanciaMetros;

    public BarbeariaComDistancia(DocumentSnapshot doc, double distanciaMetros) {
        this.doc = doc;
        this.distanciaMetros = distanciaMetros;
    }

    public DocumentSnapshot getDoc() {
        return doc;
    }

    public void setDoc(DocumentSnapshot doc) {
        this.doc = doc;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }
}
