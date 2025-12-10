@extends('layouts.app')
@section('title', 'Edit Jadwal')
@section('page-title', 'Edit Jadwal')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.jadwals') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.jadwals.update', $jadwal) }}" method="POST" class="space-y-6">
            @csrf @method('PUT')
            <div>
                <label class="block text-sm font-semibold mb-2">Kelas</label>
                <select name="kelas_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach($kelas as $k)
                        <option value="{{ $k->id }}" {{ old('kelas_id', $jadwal->kelas_id) == $k->id ? 'selected' : '' }}>{{ $k->nama_kelas }}</option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Mata Pelajaran</label>
                <select name="mapel_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach($mapels as $mapel)
                        <option value="{{ $mapel->id }}" {{ old('mapel_id', $jadwal->mapel_id) == $mapel->id ? 'selected' : '' }}>{{ $mapel->nama_mapel }}</option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Guru</label>
                <select name="guru_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ old('guru_id', $jadwal->guru_id) == $guru->id ? 'selected' : '' }}>{{ $guru->nama_guru }}</option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Tahun Ajaran</label>
                <select name="tahun_ajaran_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach($tahunAjarans as $ta)
                        <option value="{{ $ta->id }}" {{ old('tahun_ajaran_id', $jadwal->tahun_ajaran_id) == $ta->id ? 'selected' : '' }}>{{ $ta->tahun }}</option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Hari</label>
                <select name="hari" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach(['Senin','Selasa','Rabu','Kamis','Jumat','Sabtu'] as $h)
                        <option value="{{ $h }}" {{ old('hari', $jadwal->hari) == $h ? 'selected' : '' }}>{{ $h }}</option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Jam Ke</label>
                <input type="text" name="jam_ke" value="{{ old('jam_ke', $jadwal->jam_ke) }}" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Update</button>
                <a href="{{ route('admin.jadwals') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
