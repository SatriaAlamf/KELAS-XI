<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Kelas extends Model
{
    /** @use HasFactory<\Database\Factories\KelasFactory> */
    use HasFactory;

    protected $fillable = [
        'nama_kelas'
    ];

    // Relationship: Kelas has many Jadwal
    public function jadwals()
    {
        return $this->hasMany(Jadwal::class);
    }

    // Relationship: Kelas has many Users (Siswa)
    public function users()
    {
        return $this->hasMany(User::class);
    }

    // Relationship: Kelas has many Siswa (Users with role 'siswa')
    public function siswa()
    {
        return $this->hasMany(User::class)->where('role', 'siswa');
    }
}
