package com.example.hanum.skripsi.model;

public class Pengaduan {
    private String idPengaduan,idPengadu, idBarang,kerusakan,foto,lokasi,status,tanggalMasuk,tanggalSelesai, idPegawai;

    public Pengaduan() {
    }

    public Pengaduan(String idPengaduan, String idPengadu, String idBarang, String kerusakan, String foto, String lokasi, String status, String tanggalMasuk, String tanggalSelesai, String idPegawai) {
        this.idPengaduan = idPengaduan;
        this.idPengadu = idPengadu;
        this.idBarang = idBarang;
        this.kerusakan = kerusakan;
        this.foto = foto;
        this.lokasi = lokasi;
        this.status = status;
        this.tanggalMasuk = tanggalMasuk;
        this.tanggalSelesai = tanggalSelesai;
        this.idPegawai = idPegawai;
    }

    public String getIdPengaduan() {
        return idPengaduan;
    }

    public String getIdPengadu() {
        return idPengadu;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public String getKerusakan() {
        return kerusakan;
    }

    public String getFoto() {
        return foto;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getStatus() {
        return status;
    }

    public String getTanggalMasuk() {
        return tanggalMasuk;
    }

    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setIdPengaduan(String idPengaduan) {
        this.idPengaduan = idPengaduan;
    }

    public String getIdPegawai() {
        return idPegawai;
    }

    public void setIdPegawai(String idPegawai) {
        this.idPegawai = idPegawai;
    }

    public void setIdPengadu(String idPengadu) {
        this.idPengadu = idPengadu;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public void setKerusakan(String kerusakan) {
        this.kerusakan = kerusakan;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTanggalMasuk(String tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public void setTanggalSelesai(String tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }
}
