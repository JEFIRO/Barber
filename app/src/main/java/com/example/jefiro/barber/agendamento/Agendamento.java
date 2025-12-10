package com.example.jefiro.barber.agendamento;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Agendamento {
    private String id;
    private String idBarbearia;
    private String idBarbeiro;
    private String idServico;
    private String idCliente;
    private Timestamp data_agendada;
    private boolean confirmado;
    private Timestamp criadoem;
    private Boolean status;

    public Agendamento() {
    }

    public Agendamento(String idBarbearia, String idBarbeiro, String idServico, String idCliente, Timestamp data_agendada) {
        this.idBarbearia = idBarbearia;
        this.idBarbeiro = idBarbeiro;
        this.idServico = idServico;
        this.idCliente = idCliente;
        this.data_agendada = data_agendada;
        this.confirmado = false;
        this.criadoem = Timestamp.now();
    }

    public String getIdBarbearia() {
        return idBarbearia;
    }

    public void setIdBarbearia(String idBarbearia) {
        this.idBarbearia = idBarbearia;
    }

    public String getIdBarbeiro() {
        return idBarbeiro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setIdBarbeiro(String idBarbeiro) {
        this.idBarbeiro = idBarbeiro;
    }

    public String getIdServico() {
        return idServico;
    }

    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Timestamp getData_agendada() {
        return data_agendada;
    }

    public void setData_agendada(Timestamp data_agendada) {
        this.data_agendada = data_agendada;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public Timestamp getCriadoem() {
        return criadoem;
    }

    public void setCriadoem(Timestamp criadoem) {
        this.criadoem = criadoem;
    }
}
