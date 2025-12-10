package com.example.jefiro.barber.model;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class Cliente {
    private String uuidCliente;
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private String fotoUrl;
    private Timestamp criadoEm;
    private Timestamp atualizadoEm;

    public Cliente() {
        this.uuidCliente = UUID.randomUUID().toString();
        this.criadoEm = Timestamp.now();
        this.atualizadoEm = Timestamp.now();
    }

    public Cliente(String nome, String telefone, String email, String senha, String fotoUrl) {
        this.uuidCliente = UUID.randomUUID().toString();
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.fotoUrl = fotoUrl;
        this.criadoEm = Timestamp.now();
        this.atualizadoEm = Timestamp.now();
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public Timestamp getAtualizadoEm() {
        return atualizadoEm;
    }

    public String getUuidCliente() {
        return uuidCliente;
    }

    public void setUuidCliente(String uuidCliente) {
        this.uuidCliente = uuidCliente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setAtualizadoEm(Timestamp atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

}
