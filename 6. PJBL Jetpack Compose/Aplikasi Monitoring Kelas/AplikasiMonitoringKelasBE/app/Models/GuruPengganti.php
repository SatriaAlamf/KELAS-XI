<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class GuruPengganti extends Model
{
    use HasFactory;

    protected $table = 'guru_pengganti';

    protected $fillable = [
        'guru_mengajar_id',
        'guru_asli_id',
        'guru_pengganti_id',
        'tanggal_mulai',
        'tanggal_selesai',
        'alasan',
        'keterangan',
        'status'
    ];

    protected $casts = [
        'tanggal_mulai' => 'date',
        'tanggal_selesai' => 'date',
    ];

    // Relationship: GuruPengganti belongs to GuruMengajar
    public function guruMengajar()
    {
        return $this->belongsTo(GuruMengajar::class);
    }

    // Relationship: Guru Asli (guru yang tidak masuk)
    public function guruAsli()
    {
        return $this->belongsTo(Guru::class, 'guru_asli_id');
    }

    // Relationship: Guru Pengganti (guru yang menggantikan)
    public function guruPengganti()
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id');
    }
}
