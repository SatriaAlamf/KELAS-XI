@extends('layouts.app')
@section('title', 'Tahun Ajaran')
@section('page-title', 'Manajemen Tahun Ajaran')

@section('content')
<div class="mb-6 flex justify-between items-center">
    <div><h3 class="text-lg font-semibold">Daftar Tahun Ajaran</h3><p class="text-sm text-gray-600">Total: {{ $tahunAjarans->total() }}</p></div>
    <div class="flex gap-2">
        <a href="{{ route('admin.tahun-ajarans.export') }}" class="bg-green-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-green-700"><i class="fas fa-file-excel mr-2"></i>Export Excel</a>
        <button onclick="document.getElementById('importModal').classList.remove('hidden')" class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700"><i class="fas fa-file-import mr-2"></i>Import Excel</button>
        <a href="{{ route('admin.tahun-ajarans.create') }}" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700"><i class="fas fa-plus mr-2"></i>Tambah Tahun Ajaran</a>
    </div>
</div>
<div class="bg-white rounded-xl shadow-lg overflow-hidden">
    <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
            <tr>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">#</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Tahun</th>
                <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase">Status</th>
                <th class="px-6 py-4 text-center text-xs font-bold text-gray-700 uppercase">Aksi</th>
            </tr>
        </thead>
        <tbody class="bg-white divide-y">
            @forelse($tahunAjarans as $index => $ta)
                <tr class="hover:bg-gray-50">
                    <td class="px-6 py-4">{{ $tahunAjarans->firstItem() + $index }}</td>
                    <td class="px-6 py-4 font-semibold">{{ $ta->tahun }}</td>
                    <td class="px-6 py-4">
                        <span class="px-3 py-1 rounded-full text-xs font-semibold {{ $ta->flag ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800' }}">
                            {{ $ta->flag ? 'Aktif' : 'Tidak Aktif' }}
                        </span>
                    </td>
                    <td class="px-6 py-4 text-center">
                        <a href="{{ route('admin.tahun-ajarans.edit', $ta) }}" class="text-blue-600 hover:text-blue-900 px-3 py-2"><i class="fas fa-edit"></i></a>
                        <form action="{{ route('admin.tahun-ajarans.destroy', $ta) }}" method="POST" class="inline" onsubmit="return confirm('Yakin?')">
                            @csrf @method('DELETE')
                            <button type="submit" class="text-red-600 hover:text-red-900 px-3 py-2"><i class="fas fa-trash"></i></button>
                        </form>
                    </td>
                </tr>
            @empty
                <tr><td colspan="4" class="px-6 py-12 text-center text-gray-500">Belum ada data</td></tr>
            @endforelse
        </tbody>
    </table>
    @if($tahunAjarans->hasPages())<div class="bg-gray-50 px-6 py-4">{{ $tahunAjarans->links() }}</div>@endif
</div>

<!-- Import Modal -->
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800"><i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data Tahun Ajaran</h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" class="text-gray-400 hover:text-gray-600 text-3xl font-bold">&times;</button>
        </div>
        <form action="{{ route('admin.tahun-ajarans.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div><a href="{{ route('admin.tahun-ajarans.template') }}" class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-lg"><i class="fas fa-file-excel mr-2"></i>Download Template</a></div>
            <div><input type="file" name="file" accept=".xlsx,.xls,.csv" required class="block w-full text-sm border rounded-lg p-3"></div>
            <div class="flex justify-end gap-3">
                <button type="button" onclick="document.getElementById('importModal').classList.add('hidden')" class="px-6 py-3 bg-gray-300 rounded-lg hover:bg-gray-400">Batal</button>
                <button type="submit" class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700">Import</button>
            </div>
        </form>
    </div>
</div>
@endsection
