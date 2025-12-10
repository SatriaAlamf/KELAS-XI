<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AdminController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\Admin\GuruPenggantiPanelController;

// Redirect root to login
Route::get('/', function () {
    return redirect()->route('login');
});

// Authentication Routes
Route::get('/login', [AuthController::class, 'showLoginForm'])->name('login');
Route::post('/login', [AuthController::class, 'webLogin'])->name('login.post');
Route::post('/logout', [AuthController::class, 'webLogout'])->name('logout');

// Admin Panel Routes (Protected by auth middleware)
Route::middleware(['auth'])->prefix('admin')->name('admin.')->group(function () {
    // Dashboard
    Route::get('/dashboard', [AdminController::class, 'dashboard'])->name('dashboard');
    
    // User Management
    Route::get('/users', [AdminController::class, 'users'])->name('users');
    Route::get('/users/create', [AdminController::class, 'createUser'])->name('users.create');
    Route::post('/users', [AdminController::class, 'storeUser'])->name('users.store');
    Route::get('/users/{user}/edit', [AdminController::class, 'editUser'])->name('users.edit');
    Route::put('/users/{user}', [AdminController::class, 'updateUser'])->name('users.update');
    Route::delete('/users/{user}', [AdminController::class, 'destroyUser'])->name('users.destroy');
    Route::get('/users/export/excel', [AdminController::class, 'exportUsers'])->name('users.export');
    Route::get('/users/template/download', [AdminController::class, 'downloadTemplateUsers'])->name('users.template');
    Route::post('/users/import', [AdminController::class, 'importUsers'])->name('users.import');
    
    // Guru Management
    Route::get('/gurus', [AdminController::class, 'gurus'])->name('gurus');
    Route::get('/gurus/create', [AdminController::class, 'createGuru'])->name('gurus.create');
    Route::post('/gurus', [AdminController::class, 'storeGuru'])->name('gurus.store');
    Route::get('/gurus/{guru}/edit', [AdminController::class, 'editGuru'])->name('gurus.edit');
    Route::put('/gurus/{guru}', [AdminController::class, 'updateGuru'])->name('gurus.update');
    Route::delete('/gurus/{guru}', [AdminController::class, 'destroyGuru'])->name('gurus.destroy');
    Route::get('/gurus/export/excel', [AdminController::class, 'exportGurus'])->name('gurus.export');
    Route::get('/gurus/template/download', [AdminController::class, 'downloadTemplateGurus'])->name('gurus.template');
    Route::post('/gurus/import', [AdminController::class, 'importGurus'])->name('gurus.import');
    
    // Mapel Management
    Route::get('/mapels', [AdminController::class, 'mapels'])->name('mapels');
    Route::get('/mapels/create', [AdminController::class, 'createMapel'])->name('mapels.create');
    Route::post('/mapels', [AdminController::class, 'storeMapel'])->name('mapels.store');
    Route::get('/mapels/{mapel}/edit', [AdminController::class, 'editMapel'])->name('mapels.edit');
    Route::put('/mapels/{mapel}', [AdminController::class, 'updateMapel'])->name('mapels.update');
    Route::delete('/mapels/{mapel}', [AdminController::class, 'destroyMapel'])->name('mapels.destroy');
    Route::get('/mapels/export/excel', [AdminController::class, 'exportMapels'])->name('mapels.export');
    Route::get('/mapels/template/download', [AdminController::class, 'downloadTemplateMapels'])->name('mapels.template');
    Route::post('/mapels/import', [AdminController::class, 'importMapels'])->name('mapels.import');
    
    // Kelas Management
    Route::get('/kelas', [AdminController::class, 'kelas'])->name('kelas');
    Route::get('/kelas/create', [AdminController::class, 'createKelas'])->name('kelas.create');
    Route::post('/kelas', [AdminController::class, 'storeKelas'])->name('kelas.store');
    Route::get('/kelas/{kela}/edit', [AdminController::class, 'editKelas'])->name('kelas.edit');
    Route::put('/kelas/{kela}', [AdminController::class, 'updateKelas'])->name('kelas.update');
    Route::delete('/kelas/{kela}', [AdminController::class, 'destroyKelas'])->name('kelas.destroy');
    Route::get('/kelas/export/excel', [AdminController::class, 'exportKelas'])->name('kelas.export');
    Route::get('/kelas/template/download', [AdminController::class, 'downloadTemplateKelas'])->name('kelas.template');
    Route::post('/kelas/import', [AdminController::class, 'importKelas'])->name('kelas.import');
    
    // Tahun Ajaran Management
    Route::get('/tahun-ajarans', [AdminController::class, 'tahunAjarans'])->name('tahun-ajarans');
    Route::get('/tahun-ajarans/create', [AdminController::class, 'createTahunAjaran'])->name('tahun-ajarans.create');
    Route::post('/tahun-ajarans', [AdminController::class, 'storeTahunAjaran'])->name('tahun-ajarans.store');
    Route::get('/tahun-ajarans/{tahunAjaran}/edit', [AdminController::class, 'editTahunAjaran'])->name('tahun-ajarans.edit');
    Route::put('/tahun-ajarans/{tahunAjaran}', [AdminController::class, 'updateTahunAjaran'])->name('tahun-ajarans.update');
    Route::delete('/tahun-ajarans/{tahunAjaran}', [AdminController::class, 'destroyTahunAjaran'])->name('tahun-ajarans.destroy');
    Route::get('/tahun-ajarans/export/excel', [AdminController::class, 'exportTahunAjarans'])->name('tahun-ajarans.export');
    Route::get('/tahun-ajarans/template/download', [AdminController::class, 'downloadTemplateTahunAjarans'])->name('tahun-ajarans.template');
    Route::post('/tahun-ajarans/import', [AdminController::class, 'importTahunAjarans'])->name('tahun-ajarans.import');
    
    // Jadwal Management
    Route::get('/jadwals', [AdminController::class, 'jadwals'])->name('jadwals');
    Route::get('/jadwals/create', [AdminController::class, 'createJadwal'])->name('jadwals.create');
    Route::post('/jadwals', [AdminController::class, 'storeJadwal'])->name('jadwals.store');
    Route::get('/jadwals/{jadwal}/edit', [AdminController::class, 'editJadwal'])->name('jadwals.edit');
    Route::put('/jadwals/{jadwal}', [AdminController::class, 'updateJadwal'])->name('jadwals.update');
    Route::delete('/jadwals/{jadwal}', [AdminController::class, 'destroyJadwal'])->name('jadwals.destroy');
    Route::get('/jadwals/export/excel', [AdminController::class, 'exportJadwals'])->name('jadwals.export');
    Route::get('/jadwals/template/download', [AdminController::class, 'downloadTemplateJadwals'])->name('jadwals.template');
    Route::post('/jadwals/import', [AdminController::class, 'importJadwals'])->name('jadwals.import');
    
    // Guru Mengajar Management
    Route::get('/guru-mengajars', [AdminController::class, 'guruMengajars'])->name('guru-mengajars');
    Route::get('/guru-mengajars/create', [AdminController::class, 'createGuruMengajar'])->name('guru-mengajars.create');
    Route::post('/guru-mengajars', [AdminController::class, 'storeGuruMengajar'])->name('guru-mengajars.store');
    Route::get('/guru-mengajars/{guruMengajar}/edit', [AdminController::class, 'editGuruMengajar'])->name('guru-mengajars.edit');
    Route::put('/guru-mengajars/{guruMengajar}', [AdminController::class, 'updateGuruMengajar'])->name('guru-mengajars.update');
    Route::delete('/guru-mengajars/{guruMengajar}', [AdminController::class, 'destroyGuruMengajar'])->name('guru-mengajars.destroy');
    Route::get('/guru-mengajars/export/excel', [AdminController::class, 'exportGuruMengajars'])->name('guru-mengajars.export');
    Route::get('/guru-mengajars/template/download', [AdminController::class, 'downloadTemplateGuruMengajars'])->name('guru-mengajars.template');
    Route::post('/guru-mengajars/import', [AdminController::class, 'importGuruMengajars'])->name('guru-mengajars.import');
    
    // Guru Pengganti Management
    Route::get('/guru-penggantis', [GuruPenggantiPanelController::class, 'index'])->name('guru-penggantis');
    Route::get('/guru-penggantis/create', [GuruPenggantiPanelController::class, 'create'])->name('guru-penggantis.create');
    Route::post('/guru-penggantis', [GuruPenggantiPanelController::class, 'store'])->name('guru-penggantis.store');
    Route::get('/guru-penggantis/{guruPengganti}/edit', [GuruPenggantiPanelController::class, 'edit'])->name('guru-penggantis.edit');
    Route::put('/guru-penggantis/{guruPengganti}', [GuruPenggantiPanelController::class, 'update'])->name('guru-penggantis.update');
    Route::put('/guru-penggantis/{guruPengganti}/update-durasi', [GuruPenggantiPanelController::class, 'updateDurasi'])->name('guru-penggantis.update-durasi');
    Route::delete('/guru-penggantis/{guruPengganti}', [GuruPenggantiPanelController::class, 'destroy'])->name('guru-penggantis.destroy');
    Route::get('/guru-penggantis/export/excel', [GuruPenggantiPanelController::class, 'export'])->name('guru-penggantis.export');
    Route::get('/guru-penggantis/template/download', [GuruPenggantiPanelController::class, 'template'])->name('guru-penggantis.template');
    Route::post('/guru-penggantis/import', [GuruPenggantiPanelController::class, 'import'])->name('guru-penggantis.import');
});
