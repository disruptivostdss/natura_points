package com.fiap.felipevieira.mapsexemplo;

import java.io.Serializable;

/**
 * Created by felipemv on 11/09/17.
 */

public class Usuario implements Serializable{

    private long id;
    private String nome;
    private long pontos;

    public Usuario(long id, String nome, long pontos) {
        this.id = id;
        this.nome = nome;
        this.pontos = pontos;
    }

    public Usuario() {
    }

    @Override
    public String toString() {
        return nome + " - "+ pontos + " pontos";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getPontos() {
        return pontos;
    }

    public void setPontos(long pontos) {
        this.pontos = pontos;
    }
}
