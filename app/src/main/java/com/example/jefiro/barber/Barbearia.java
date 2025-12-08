package com.example.jefiro.barber;

import androidx.annotation.NonNull;

public final class Barbearia {

    @NonNull private final String nome;
    @NonNull private final String endereco;
    @NonNull private final String servicos;
    private final int imagem;

    public Barbearia(@NonNull String nome,
                     @NonNull String endereco,
                     @NonNull String servicos,
                     int imagem) {

        this.nome = nome;
        this.endereco = endereco;
        this.servicos = servicos;
        this.imagem = imagem;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    @NonNull
    public String getEndereco() {
        return endereco;
    }

    @NonNull
    public String getServicos() {
        return servicos;
    }

    public int getImagem() {
        return imagem;
    }
}
