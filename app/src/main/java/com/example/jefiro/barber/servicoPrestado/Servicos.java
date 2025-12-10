package com.example.jefiro.barber.servicoPrestado;

import com.google.firebase.Timestamp;


public class Servicos {
    private String barbearia_id;
    private String nome;
    private String duracao;
    private Double preco;
    private boolean ativo;
    private Timestamp criadoEm;
    private Timestamp atualizadoEm;

    public Servicos() {
    }

    public Servicos(String barbearia_id, String nome, String duracao, Double preco) {
        this.barbearia_id = barbearia_id;
        this.nome = nome;
        this.duracao = duracao;
        this.preco = preco;

        this.ativo = true;
        this.criadoEm = Timestamp.now();
        this.atualizadoEm = Timestamp.now();
    }

    public Servicos(String barbearia_id, String nome, String duracao, Double preco, boolean ativo, Timestamp criadoEm, Timestamp atualizadoEm) {
        this.barbearia_id = barbearia_id;
        this.nome = nome;
        this.duracao = duracao;
        this.preco = preco;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public String getBarbearia_id() {
        return barbearia_id;
    }

    public void setBarbearia_id(String barbearia_id) {
        this.barbearia_id = barbearia_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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
