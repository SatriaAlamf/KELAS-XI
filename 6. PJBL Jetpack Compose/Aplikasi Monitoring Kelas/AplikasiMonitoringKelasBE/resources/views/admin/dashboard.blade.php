@extends('layouts.app')

@section('title', 'Dashboard')
@section('page-title', 'Dashboard')
@section('page-subtitle', 'Overview dan statistik sistem')

@section('content')
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
    <!-- Stats Cards -->
    <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-blue-100 text-sm font-semibold uppercase">Total Users</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_users'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-users text-3xl"></i>
            </div>
        </div>
    </div>

    <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-green-100 text-sm font-semibold uppercase">Total Guru</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_guru'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-chalkboard-teacher text-3xl"></i>
            </div>
        </div>
    </div>

    <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-purple-100 text-sm font-semibold uppercase">Total Siswa</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_siswa'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-user-graduate text-3xl"></i>
            </div>
        </div>
    </div>

    <div class="bg-gradient-to-br from-orange-500 to-orange-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-orange-100 text-sm font-semibold uppercase">Total Kelas</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_kelas'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-door-open text-3xl"></i>
            </div>
        </div>
    </div>

    <div class="bg-gradient-to-br from-pink-500 to-pink-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-pink-100 text-sm font-semibold uppercase">Mata Pelajaran</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_mapel'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-book text-3xl"></i>
            </div>
        </div>
    </div>

    <div class="bg-gradient-to-br from-indigo-500 to-indigo-600 rounded-xl shadow-lg p-6 text-white transform hover:scale-105 transition duration-200">
        <div class="flex items-center justify-between">
            <div>
                <p class="text-indigo-100 text-sm font-semibold uppercase">Total Jadwal</p>
                <p class="text-4xl font-bold mt-2">{{ $stats['total_jadwal'] }}</p>
            </div>
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <i class="fas fa-clock text-3xl"></i>
            </div>
        </div>
    </div>
</div>

<div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
    <!-- Recent Users -->
    <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between mb-6">
            <h3 class="text-xl font-bold text-gray-800">
                <i class="fas fa-users text-blue-600 mr-2"></i>User Terbaru
            </h3>
            <a href="{{ route('admin.users') }}" class="text-sm text-blue-600 hover:text-blue-800 font-semibold">
                Lihat Semua <i class="fas fa-arrow-right ml-1"></i>
            </a>
        </div>
        <div class="space-y-4">
            @forelse($recent_users as $user)
                <div class="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                    <div class="flex items-center space-x-3">
                        <div class="w-10 h-10 bg-gradient-to-br from-blue-400 to-purple-600 rounded-full flex items-center justify-center text-white font-bold">
                            {{ substr($user->name, 0, 1) }}
                        </div>
                        <div>
                            <p class="font-semibold text-gray-800">{{ $user->name }}</p>
                            <p class="text-sm text-gray-500">{{ $user->email }}</p>
                        </div>
                    </div>
                    <span class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-semibold rounded-full">
                        {{ ucfirst($user->role) }}
                    </span>
                </div>
            @empty
                <p class="text-gray-500 text-center py-4">Belum ada user terdaftar</p>
            @endforelse
        </div>
    </div>

    <!-- Recent Jadwal -->
    <div class="bg-white rounded-xl shadow-lg p-6">
        <div class="flex items-center justify-between mb-6">
            <h3 class="text-xl font-bold text-gray-800">
                <i class="fas fa-clock text-indigo-600 mr-2"></i>Jadwal Terbaru
            </h3>
            <a href="{{ route('admin.jadwals') }}" class="text-sm text-indigo-600 hover:text-indigo-800 font-semibold">
                Lihat Semua <i class="fas fa-arrow-right ml-1"></i>
            </a>
        </div>
        <div class="space-y-4">
            @forelse($recent_jadwal as $jadwal)
                <div class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                    <div class="flex items-center justify-between mb-2">
                        <p class="font-semibold text-gray-800">{{ $jadwal->mapel->nama_mapel }}</p>
                        <span class="px-2 py-1 bg-indigo-100 text-indigo-800 text-xs font-semibold rounded">
                            {{ $jadwal->hari }}
                        </span>
                    </div>
                    <div class="text-sm text-gray-600">
                        <p><i class="fas fa-door-open mr-2"></i>{{ $jadwal->kelas->nama_kelas }}</p>
                        <p><i class="fas fa-user mr-2"></i>{{ $jadwal->guru->nama }}</p>
                        <p><i class="fas fa-clock mr-2"></i>{{ $jadwal->jam_mulai }} - {{ $jadwal->jam_selesai }}</p>
                    </div>
                </div>
            @empty
                <p class="text-gray-500 text-center py-4">Belum ada jadwal tersedia</p>
            @endforelse
        </div>
    </div>
</div>
@endsection
