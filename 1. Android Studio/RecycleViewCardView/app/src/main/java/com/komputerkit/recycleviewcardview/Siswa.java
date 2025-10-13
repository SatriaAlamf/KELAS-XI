package com.komputerkit.recycleviewcardview;

// Model data Mahasiswa
public class Siswa {

    private String namaLengkap;
    private String kotaAsal;

    public Siswa(String namaLengkap, String kotaAsal) {
        this.kotaAsal = kotaAsal;
        this.namaLengkap = namaLengkap;
    }

    public String getKotaAsal() {
        return kotaAsal;
    }

    public void setKotaAsal(String kotaAsal) {
        this.kotaAsal = kotaAsal;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }
}
