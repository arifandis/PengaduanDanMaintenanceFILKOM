package com.example.hanum.skripsi.model;

public class Kerusakan {
    private String tanggalDiterima,tanggalSelesai,namaPegawai;

    public Kerusakan(String tanggalDiterima, String tanggalSelesai, String namaPegawai) {
        this.tanggalDiterima = tanggalDiterima;
        this.tanggalSelesai = tanggalSelesai;
        this.namaPegawai = namaPegawai;
    }

    public String getTanggalDiterima() {
        return tanggalDiterima;
    }

    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    public String getNamaPegawai() {
        return namaPegawai;
    }
}
