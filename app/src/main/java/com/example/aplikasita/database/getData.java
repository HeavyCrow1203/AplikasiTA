package com.example.aplikasita.database;

public class getData {
    String Suhu, Kelembaban_Tanah, Lama_Siram;
    String keterangan, Tanggal, Waktu, key;

    public String getSuhu() {
        return Suhu;
    }

    public void setSuhu(String suhu) {
        Suhu = suhu;
    }

    public String getKelembaban_Tanah() {
        return Kelembaban_Tanah;
    }

    public void setKelembaban_Tanah(String kelembaban_Tanah) {
        Kelembaban_Tanah = kelembaban_Tanah;
    }

    public String getLama_Siram() {
        return Lama_Siram;
    }

    public void setLama_Siram(String lama_Siram) {
        Lama_Siram = lama_Siram;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getWaktu() {
        return Waktu;
    }

    public void setWaktu(String waktu) {
        Waktu = waktu;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
