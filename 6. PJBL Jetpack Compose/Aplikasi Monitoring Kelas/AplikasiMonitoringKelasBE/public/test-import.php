<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Import Excel - Web Panel</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            max-width: 900px;
            margin: 50px auto;
            padding: 20px;
            background: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 3px solid #4472C4;
            padding-bottom: 10px;
        }
        .status-box {
            padding: 15px;
            border-radius: 5px;
            margin: 15px 0;
        }
        .success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        .error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        .info {
            background: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
        }
        .test-item {
            padding: 10px;
            margin: 10px 0;
            border-left: 4px solid #4472C4;
            background: #f8f9fa;
        }
        .check {
            color: #28a745;
            font-weight: bold;
        }
        .cross {
            color: #dc3545;
            font-weight: bold;
        }
        ul {
            line-height: 1.8;
        }
        code {
            background: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            color: #e83e8c;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background: #4472C4;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin: 10px 5px;
        }
        .btn:hover {
            background: #365a9a;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ Import Excel - Web Panel Admin</h1>

        <?php
        // Check ZipArchive
        $zipAvailable = class_exists('ZipArchive');
        $phpspreadsheetAvailable = class_exists('PhpOffice\PhpSpreadsheet\IOFactory');
        
        if ($zipAvailable && $phpspreadsheetAvailable) {
            echo '<div class="status-box success">';
            echo '<h3>✅ Sistem Siap Digunakan</h3>';
            echo '<p>Semua dependency untuk import Excel sudah terpenuhi.</p>';
            echo '</div>';
        } else {
            echo '<div class="status-box error">';
            echo '<h3>❌ Error: Dependency Tidak Lengkap</h3>';
            if (!$zipAvailable) {
                echo '<p>• ZipArchive class tidak ditemukan. Aktifkan extension=zip di php.ini</p>';
            }
            if (!$phpspreadsheetAvailable) {
                echo '<p>• PhpSpreadsheet tidak terinstall. Run: composer require phpoffice/phpspreadsheet</p>';
            }
            echo '</div>';
        }
        ?>

        <div class="test-item">
            <h3>Status Dependency:</h3>
            <ul>
                <li>
                    <?php echo $zipAvailable ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    ZipArchive Class
                </li>
                <li>
                    <?php echo extension_loaded('zip') ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    PHP Zip Extension
                </li>
                <li>
                    <?php echo extension_loaded('xml') ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    PHP XML Extension
                </li>
                <li>
                    <?php echo extension_loaded('xmlwriter') ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    PHP XMLWriter Extension
                </li>
                <li>
                    <?php echo extension_loaded('xmlreader') ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    PHP XMLReader Extension
                </li>
                <li>
                    <?php echo $phpspreadsheetAvailable ? '<span class="check">✓</span>' : '<span class="cross">✗</span>'; ?>
                    PhpSpreadsheet Library
                </li>
            </ul>
        </div>

        <div class="test-item">
            <h3>Konfigurasi PHP:</h3>
            <ul>
                <li><strong>Upload Max Filesize:</strong> <?php echo ini_get('upload_max_filesize'); ?></li>
                <li><strong>Post Max Size:</strong> <?php echo ini_get('post_max_size'); ?></li>
                <li><strong>Memory Limit:</strong> <?php echo ini_get('memory_limit'); ?></li>
                <li><strong>Max Execution Time:</strong> <?php echo ini_get('max_execution_time'); ?>s</li>
                <li><strong>PHP Version:</strong> <?php echo phpversion(); ?></li>
                <li><strong>php.ini Location:</strong> <?php echo php_ini_loaded_file(); ?></li>
            </ul>
        </div>

        <div class="info status-box">
            <h3>📋 Cara Menggunakan Import Excel:</h3>
            <ol>
                <li>Login ke admin panel: <code>http://localhost:8000/admin</code></li>
                <li>Pilih menu data (Users, Guru, Mapel, Kelas, dll)</li>
                <li>Klik tombol <strong>"Download Template"</strong> untuk mendapatkan template Excel</li>
                <li>Isi data sesuai format di template</li>
                <li>Klik tombol <strong>"Import"</strong> dan upload file Excel</li>
                <li>Sistem akan validasi dan import data secara otomatis</li>
            </ol>
        </div>

        <div class="test-item">
            <h3>📊 Modul Import Yang Tersedia:</h3>
            <ul>
                <li><strong>Users:</strong> Nama, Email, Password, Role</li>
                <li><strong>Guru:</strong> Kode Guru, Nama, NIP, Email</li>
                <li><strong>Mata Pelajaran:</strong> Kode Mapel, Nama</li>
                <li><strong>Kelas:</strong> Nama Kelas, Tingkat, Jurusan</li>
                <li><strong>Tahun Ajaran:</strong> Tahun Ajaran, Status</li>
                <li><strong>Jadwal:</strong> Kode Kelas, Kode Mapel, Kode Guru, Hari, Jam Ke</li>
                <li><strong>Guru Mengajar:</strong> Kode Guru, Kode Kelas, Kode Mapel, Hari, Jam Ke, Status</li>
            </ul>
        </div>

        <div style="text-align: center; margin-top: 30px;">
            <a href="/admin" class="btn">🔐 Login Admin Panel</a>
            <a href="/admin/users" class="btn">👥 Import Users</a>
            <a href="/admin/gurus" class="btn">👨‍🏫 Import Guru</a>
        </div>

        <div style="margin-top: 30px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
            <h4>💡 Tips:</h4>
            <ul>
                <li>Gunakan format Excel (.xlsx) untuk hasil terbaik</li>
                <li>Jangan ubah nama kolom di template</li>
                <li>Pastikan tidak ada baris kosong di tengah data</li>
                <li>Untuk update data, gunakan kode/email yang sama</li>
                <li>File maksimal <?php echo ini_get('upload_max_filesize'); ?></li>
            </ul>
        </div>
    </div>
</body>
</html>
