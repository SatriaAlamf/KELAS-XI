@extends('layouts.app')
@section('title', 'Jadwal')
@section('page-title', 'Manajemen Jadwal')

@section('content')
<div class="mb-6 flex justify-between items-center">
    <div><h3 class="text-lg font-semibold">Daftar Jadwal</h3><p class="text-sm text-gray-600">Total: {{ $jadwals->total() }}</p></div>
    <div class="flex gap-2">
        <a href="{{ route('admin.jadwals.export') }}" class="bg-green-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-green-700"><i class="fas fa-file-excel mr-2"></i>Export Excel</a>
        <button onclick="document.getElementById('importModal').classList.remove('hidden')" class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700"><i class="fas fa-file-import mr-2"></i>Import Excel</button>
        <a href="{{ route('admin.jadwals.create') }}" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700"><i class="fas fa-plus mr-2"></i>Tambah Jadwal</a>
    </div>
</div>
<div class="bg-white rounded-xl shadow-lg overflow-hidden">
    <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
            <tr>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">#</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Hari</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Jam Ke</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Kelas</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Mapel</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Guru</th>
                <th class="px-6 py-4 text-center text-xs font-bold text-gray-700 uppercase">Aksi</th>
            </tr>
        </thead>
        <tbody class="bg-white divide-y">
            @forelse($jadwals as $index => $jadwal)
                <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">{{ $jadwals->firstItem() + $index }}</td>
                    <td class="px-6 py-4"><span class="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs font-semibold">{{ $jadwal->hari }}</span></td>
                    <td class="px-6 py-4 font-semibold">{{ $jadwal->jam_ke }}</td>
                    <td class="px-6 py-4">{{ $jadwal->kelas->nama_kelas }}</td>
                    <td class="px-6 py-4">{{ $jadwal->mapel->nama_mapel }}</td>
                    <td class="px-6 py-4">{{ $jadwal->guru->nama_guru }}</td>
                    <td class="px-6 py-4 text-center">
                        <a href="{{ route('admin.jadwals.edit', $jadwal) }}" class="text-blue-600 hover:text-blue-900 px-3 py-2"><i class="fas fa-edit"></i></a>
                        <form action="{{ route('admin.jadwals.destroy', $jadwal) }}" method="POST" class="inline" onsubmit="return confirm('Yakin?')">
                            @csrf @method('DELETE')
                            <button type="submit" class="text-red-600 hover:text-red-900 px-3 py-2"><i class="fas fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
            @empty
                <tr><td colspan="7" class="px-6 py-12 text-center text-gray-500">Belum ada data</td></tr>
            @endforelse
        </tbody>
    </table>
    @if($jadwals->hasPages())<div class="bg-gray-50 px-6 py-4">{{ $jadwals->links() }}</div>@endif
</div>

<!-- Import Modal -->
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800"><i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data Jadwal</h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" class="text-gray-400 hover:text-gray-600 text-3xl font-bold">&times;</button>
        </div>
        <div class="mb-4 p-4 bg-yellow-50 border-l-4 border-yellow-500 rounded">
            <p class="text-sm text-yellow-800"><i class="fas fa-exclamation-triangle mr-2"></i>Pastikan data Guru, Mapel, Tahun Ajaran, dan Kelas sudah ada sebelum import!</p>
        </div>
        <form action="{{ route('admin.jadwals.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div><a href="{{ route('admin.jadwals.template') }}" class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-lg"><i class="fas fa-file-excel mr-2"></i>Download Template</a></div>
            <div><input type="file" name="file" accept=".xlsx,.xls,.csv" required class="block w-full text-sm border rounded-lg p-3"></div>
            <div class="flex justify-end gap-3">
                <button type="button" onclick="document.getElementById('importModal').classList.add('hidden')" class="px-6 py-3 bg-gray-300 rounded-lg hover:bg-gray-400">Batal</button>
                <button type="submit" class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700">Import</button>
            </div>
        </form>
    </div>
</div>
@endsection
