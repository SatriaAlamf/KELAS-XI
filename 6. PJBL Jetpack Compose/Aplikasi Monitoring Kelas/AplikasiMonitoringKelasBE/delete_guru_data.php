<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Illuminate\Support\Facades\DB;

echo "Menghapus data guru_pengganti...\n";
$deletedPengganti = DB::table('guru_pengganti')->delete();
echo "Berhasil menghapus {$deletedPengganti} data guru_pengganti\n";

echo "Menghapus data guru_mengajars...\n";
$deletedMengajar = DB::table('guru_mengajars')->delete();
echo "Berhasil menghapus {$deletedMengajar} data guru_mengajars\n";

echo "\nSelesai! Total data dihapus: " . ($deletedPengganti + $deletedMengajar) . "\n";
