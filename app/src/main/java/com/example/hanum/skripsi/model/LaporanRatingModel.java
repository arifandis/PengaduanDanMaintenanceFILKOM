package com.example.hanum.skripsi.model;

public class LaporanRatingModel {
    private String nomor,nama;
    private float rating;
    private int count;

    public LaporanRatingModel(String nomor, String nama, float rating, int count) {
        this.nomor = nomor;
        this.nama = nama;
        this.rating = rating;
        this.count = count;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
