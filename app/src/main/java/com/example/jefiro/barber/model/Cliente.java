package com.example.jefiro.barber.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Cliente {
    private String uuidCliente;
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private String fotoUrl;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public Cliente() {
        this.uuidCliente = UUID.randomUUID().toString();
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Cliente(String nome, String telefone, String email, String senha, String fotoUrl) {
        this.uuidCliente = UUID.randomUUID().toString();
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.fotoUrl = fotoUrl;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
