package com.example.hanum.skripsi.model;

public class Maintenance {
    private String idMaintenance,kategori,nomor;
    int skala;

    public Maintenance(String idMaintenance, String kategori, String nomor, int skala) {
        this.idMaintenance = idMaintenance;
        this.kategori = kategori;
        this.nomor = nomor;
        this.skala = skala;
    }

    public int getSkala() {
        return skala;
    }

    public void setSkala(int skala) {
        this.skala = skala;
    }

    public String getIdMaintenance() {
        return idMaintenance;
    }

    public void setIdMaintenance(String idMaintenance) {
        this.idMaintenance = idMaintenance;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }
}
