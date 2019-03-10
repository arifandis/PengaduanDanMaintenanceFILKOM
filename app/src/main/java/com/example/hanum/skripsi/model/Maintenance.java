package com.example.hanum.skripsi.model;

public class Maintenance {
    private String idMaintenance,idBarang,tanggal;

    public Maintenance(String idMaintenance, String idBarang, String tanggal) {
        this.idMaintenance = idMaintenance;
        this.idBarang = idBarang;
        this.tanggal = tanggal;
    }

    public String getIdMaintenance() {
        return idMaintenance;
    }

    public void setIdMaintenance(String idMaintenance) {
        this.idMaintenance = idMaintenance;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
