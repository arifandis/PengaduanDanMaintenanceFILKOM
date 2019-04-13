package com.example.hanum.skripsi.model;

public class LaporanPengaduanModel {
    private String judul,status,tanggalMulai,tanggalSelesai;

    public LaporanPengaduanModel(String judul, String status, String tanggalMulai, String tanggalSelesai) {
        this.judul = judul;
        this.status = status;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(String tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }
}
