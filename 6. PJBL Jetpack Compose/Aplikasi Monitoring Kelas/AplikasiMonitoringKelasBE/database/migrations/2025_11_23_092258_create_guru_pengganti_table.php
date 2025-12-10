<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('guru_pengganti', function (Blueprint $table) {
            $table->id();
            $table->foreignId('guru_mengajar_id')->constrained('guru_mengajars')->onDelete('cascade');
            $table->foreignId('guru_asli_id')->constrained('gurus')->onDelete('cascade');
            $table->foreignId('guru_pengganti_id')->constrained('gurus')->onDelete('cascade');
            $table->date('tanggal_mulai');
            $table->date('tanggal_selesai');
            $table->string('alasan');
            $table->text('keterangan')->nullable();
            $table->enum('status', ['aktif', 'selesai'])->default('aktif');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('guru_pengganti');
    }
};
