package com.komputerkit.sqlitedatabase;

/**
 * Kelas Barang merepresentasikan entitas barang dalam database SQLite.
 * Kelas ini menyimpan informasi tentang barang seperti ID, nama, stok, dan harga.
 */
public class Barang {
    // Variabel instance untuk menyimpan data barang
    private String idbarang, barang, stok, harga;

    /**
     * Constructor untuk membuat objek Barang baru
     * @param idbarang ID unik untuk mengidentifikasi barang
     * @param barang Nama atau deskripsi barang
     * @param stok Jumlah stok barang yang tersedia
     * @param harga Harga barang
     */
    public Barang(String idbarang, String barang, String stok, String harga) {
        this.idbarang = idbarang;
        this.barang = barang;
        this.stok = stok;
        this.harga = harga;
    }

    /**
     * Mengambil nama atau deskripsi barang
     * @return String nama barang
     */
    public String getBarang() {
        return barang;
    }

    /**
     * Mengatur nama atau deskripsi barang
     * @param barang nama barang yang baru
     */
    public void setBarang(String barang) {
        this.barang = barang;
    }

    /**
     * Mengambil harga barang
     * @return String harga barang
     */
    public String getHarga() {
        return harga;
    }

    /**
     * Mengatur harga barang
     * @param harga harga barang yang baru
     */
    public void setHarga(String harga) {
        this.harga = harga;
    }

    /**
     * Mengambil ID barang
     * @return String ID barang
     */
    public String getIdbarang() {
        return idbarang;
    }

    /**
     * Mengatur ID barang
     * @param idbarang ID barang yang baru
     */
    public void setIdbarang(String idbarang) {
        this.idbarang = idbarang;
    }

    /**
     * Mengambil jumlah stok barang
     * @return String jumlah stok barang
     */
    public String getStok() {
        return stok;
    }

    /**
     * Mengatur jumlah stok barang
     * @param stok jumlah stok barang yang baru
     */
    public void setStok(String stok) {
        this.stok = stok;
    }
}