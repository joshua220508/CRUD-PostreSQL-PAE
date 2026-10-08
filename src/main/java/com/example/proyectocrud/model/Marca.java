package com.example.proyectocrud.model;

public class Marca {

    private int idMarca;
    private String nombre;
    private String paisOrigen;
    private int estado;

    public Marca() {
    }

    public Marca(int idMarca, String nombre, String paisOrigen, int estado) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.estado = estado;
    }

    public Marca(String nombre, String paisOrigen, int estado) {
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.estado = estado;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}