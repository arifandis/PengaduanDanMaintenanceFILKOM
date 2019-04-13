package com.example.hanum.skripsi.model;

public class LaporanMaintenanceModel {
    private String noInventaris,skala,waktuPelaksanaan;

    public LaporanMaintenanceModel(String noInventaris, String skala, String waktuPelaksanaan) {
        this.noInventaris = noInventaris;
        this.skala = skala;
        this.waktuPelaksanaan = waktuPelaksanaan;
    }

    public String getNoInventaris() {
        return noInventaris;
    }

    public void setNoInventaris(String noInventaris) {
        this.noInventaris = noInventaris;
    }

    public String getSkala() {
        return skala;
    }

    public void setSkala(String skala) {
        this.skala = skala;
    }

    public String getWaktuPelaksanaan() {
        return waktuPelaksanaan;
    }

    public void setWaktuPelaksanaan(String waktuPelaksanaan) {
        this.waktuPelaksanaan = waktuPelaksanaan;
    }
}
