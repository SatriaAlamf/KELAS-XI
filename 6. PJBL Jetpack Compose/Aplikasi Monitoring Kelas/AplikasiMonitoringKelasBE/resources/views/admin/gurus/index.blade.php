@extends('layouts.app')

@section('title', 'Guru')
@section('page-title', 'Manajemen Guru')
@section('page-subtitle', 'Kelola data guru')

@section('content')
<div class="mb-6 flex justify-between items-center">
    <div>
        <h3 class="text-lg font-semibold text-gray-800">Daftar Guru</h3>
        <p class="text-sm text-gray-600">Total: {{ $gurus->total() }} guru</p>
    </div>
    <div class="flex gap-2">
        <a href="{{ route('admin.gurus.export') }}" class="bg-green-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-green-700 transition shadow-lg">
            <i class="fas fa-file-excel mr-2"></i>Export Excel
        </a>
        <button onclick="document.getElementById('importModal').classList.remove('hidden')" class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700 transition shadow-lg">
            <i class="fas fa-file-import mr-2"></i>Import Excel
        </button>
        <a href="{{ route('admin.gurus.create') }}" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition shadow-lg">
            <i class="fas fa-plus mr-2"></i>Tambah Guru
        </a>
    </div>
</div>

<div class="bg-white rounded-xl shadow-lg overflow-hidden">
    <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
            <tr>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">#</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Kode Guru</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Nama Guru</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Telepon</th>
                <th class="px-6 py-4 text-center text-xs font-bold text-gray-700 uppercase">Aksi</th>
            </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
            @forelse($gurus as $index => $guru)
                <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4 text-sm text-gray-900">{{ $gurus->firstItem() + $index }}</td>
                    <td class="px-6 py-4 text-sm font-semibold text-gray-900">{{ $guru->kode_guru }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ $guru->nama_guru }}</td>
                    <td class="px-6 py-4 text-sm text-gray-600">{{ $guru->telepon ?? '-' }}</td>
                    <td class="px-6 py-4 text-center">
                        <a href="{{ route('admin.gurus.edit', $guru) }}" class="text-blue-600 hover:text-blue-900 px-3 py-2 rounded-lg">
                            <i class="fas fa-edit"></i>
                        </a>
                        <form action="{{ route('admin.gurus.destroy', $guru) }}" method="POST" class="inline" onsubmit="return confirm('Yakin ingin menghapus?')">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="text-red-600 hover:text-red-900 px-3 py-2 rounded-lg">
                                <i class="fas fa-trash"></i>
                            </button>
                        </form>
                    </td>
                </tr>
            @empty
                <tr>
                    <td colspan="5" class="px-6 py-12 text-center text-gray-500">Belum ada data guru</td>
                </tr>
            @endforelse
        </tbody>
    </table>
    @if($gurus->hasPages())
        <div class="bg-gray-50 px-6 py-4">{{ $gurus->links() }}</div>
    @endif
</div>

<!-- Import Modal -->
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800">
                <i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data Guru
            </h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" class="text-gray-400 hover:text-gray-600 text-3xl font-bold">
                &times;
            </button>
        </div>
        
        <div class="mb-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
            <p class="text-sm text-blue-800"><i class="fas fa-info-circle mr-2"></i><strong>Panduan Import:</strong></p>
            <ol class="mt-2 ml-6 text-sm text-blue-700 list-decimal space-y-1">
                <li>Download template Excel terlebih dahulu</li>
                <li>Isi data sesuai format yang tersedia</li>
                <li>Upload file Excel yang sudah diisi</li>
            </ol>
        </div>
        
        <form action="{{ route('admin.gurus.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div>
                <a href="{{ route('admin.gurus.template') }}" class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-lg">
                    <i class="fas fa-file-excel mr-2"></i>Download Template Excel
                </a>
            </div>
            <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Upload File Excel</label>
                <input type="file" name="file" accept=".xlsx,.xls,.csv" required class="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 p-3">
            </div>
            <div class="flex justify-end gap-3 mt-6">
                <button type="button" onclick="document.getElementById('importModal').classList.add('hidden')" class="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition font-semibold">
                    <i class="fas fa-times mr-2"></i>Batal
                </button>
                <button type="submit" class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition shadow-lg font-semibold">
                    <i class="fas fa-upload mr-2"></i>Import Data
                </button>
            </div>
        </form>
    </div>
</div>
@endsection
