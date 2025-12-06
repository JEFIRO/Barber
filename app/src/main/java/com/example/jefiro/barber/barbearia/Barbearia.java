package com.example.jefiro.barber.barbearia;

import com.google.firebase.Timestamp;

public class Barbearia {
    private String id;
    private String nome;
    private String telefone;
    private String email;
    private String fotoUrl;
    private Timestamp criadoEm;
    private Timestamp atualizadoEm;

    public Barbearia(String nome, String telefone, String email, String fotoUrl) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.fotoUrl = fotoUrl;

        this.criadoEm = Timestamp.now();
        this.atualizadoEm = Timestamp.now();
    }

    public Barbearia(String id, String nome, String telefone, String email, String fotoUrl, Timestamp criadoEm, Timestamp atualizadoEm) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.fotoUrl = fotoUrl;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public Barbearia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Timestamp getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Timestamp atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
