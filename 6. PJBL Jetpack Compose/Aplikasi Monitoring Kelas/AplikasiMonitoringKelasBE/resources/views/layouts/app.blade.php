<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'Dashboard') - Admin Panel Monitoring Sekolah</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
        }
        body {
            font-family: 'Inter', sans-serif;
            margin: 0;
            padding: 0;
            overflow-x: hidden;
        }
        .sidebar-link.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        /* Layout CSS */
        .layout-container {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .main-wrapper {
            display: flex;
            flex: 1;
        }
        .sidebar {
            width: 256px;
            min-height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000;
            transition: transform 0.3s ease;
        }
        .content-wrapper {
            flex: 1;
            margin-left: 256px;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            width: calc(100vw - 256px);
        }
        .navbar {
            position: relative;
            top: 0;
            z-index: 999;
            height: auto;
            min-height: 80px;
        }
        .main-content {
            flex: 1;
            padding: 1.5rem;
            background-color: #f9fafb;
            min-height: calc(100vh - 120px);
        }
        @media (max-width: 768px) {
            .sidebar {
                transform: translateX(-100%);
            }
            .content-wrapper {
                margin-left: 0;
                width: 100vw;
            }
            .sidebar.mobile-open {
                transform: translateX(0);
            }
            .main-content {
                padding: 1rem;
            }
        }
    </style>
</head>
<body class="bg-gray-50">
    <div class="layout-container">
        <!-- Sidebar -->
        <aside class="sidebar bg-gradient-to-b from-gray-900 to-gray-800 text-white">
            <div class="p-6">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-gradient-to-br from-blue-400 to-purple-600 rounded-lg flex items-center justify-center">
                        <i class="fas fa-school text-xl"></i>
                    </div>
                    <div>
                        <h1 class="text-lg font-bold">Admin Panel</h1>
                        <p class="text-xs text-gray-400">Monitoring Sekolah</p>
                    </div>
                </div>
            </div>
            
            <nav class="mt-6 px-4">
                <a href="{{ route('admin.dashboard') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.dashboard') ? 'active' : '' }}">
                    <i class="fas fa-home w-5"></i>
                    <span>Dashboard</span>
                </a>
                
                <div class="mt-6 mb-2 px-4 text-xs font-semibold text-gray-400 uppercase tracking-wider">
                    Master Data
                </div>
                
                <a href="{{ route('admin.users') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.users*') ? 'active' : '' }}">
                    <i class="fas fa-users w-5"></i>
                    <span>Users</span>
                </a>
                
                <a href="{{ route('admin.gurus') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.gurus*') ? 'active' : '' }}">
                    <i class="fas fa-chalkboard-teacher w-5"></i>
                    <span>Guru</span>
                </a>
                
                <a href="{{ route('admin.mapels') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.mapels*') ? 'active' : '' }}">
                    <i class="fas fa-book w-5"></i>
                    <span>Mata Pelajaran</span>
                </a>
                
                <a href="{{ route('admin.kelas') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.kelas*') ? 'active' : '' }}">
                    <i class="fas fa-door-open w-5"></i>
                    <span>Kelas</span>
                </a>
                
                <a href="{{ route('admin.tahun-ajarans') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.tahun-ajarans*') ? 'active' : '' }}">
                    <i class="fas fa-calendar-alt w-5"></i>
                    <span>Tahun Ajaran</span>
                </a>
                
                <div class="mt-6 mb-2 px-4 text-xs font-semibold text-gray-400 uppercase tracking-wider">
                    Akademik
                </div>
                
                <a href="{{ route('admin.jadwals') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.jadwals*') ? 'active' : '' }}">
                    <i class="fas fa-clock w-5"></i>
                    <span>Jadwal</span>
                </a>
                
                <a href="{{ route('admin.guru-mengajars') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.guru-mengajars*') ? 'active' : '' }}">
                    <i class="fas fa-user-check w-5"></i>
                    <span>Guru Mengajar</span>
                </a>
                
                <a href="{{ route('admin.guru-penggantis') }}" class="sidebar-link flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 hover:bg-gray-700 transition {{ request()->routeIs('admin.guru-penggantis*') ? 'active' : '' }}">
                    <i class="fas fa-user-friends w-5"></i>
                    <span>Guru Pengganti</span>
                </a>
            </nav>
        </aside>

        <!-- Content Wrapper -->
        <div class="content-wrapper">
            <!-- Top Navbar -->
            <header class="navbar bg-white shadow-sm">
                <div class="flex items-center justify-between px-6 py-5">
                    <!-- Mobile Menu Button -->
                    <button id="mobileMenuToggle" class="md:hidden text-gray-600 hover:text-gray-900">
                        <i class="fas fa-bars text-xl"></i>
                    </button>
                    
                    <div>
                        <h2 class="text-2xl font-bold text-gray-800">@yield('page-title', 'Dashboard')</h2>
                        <p class="text-sm text-gray-500">@yield('page-subtitle', 'Selamat datang di admin panel')</p>
                    </div>
                    
                    <div class="flex items-center space-x-4">
                        <div class="text-right mr-3">
                            <p class="text-sm font-semibold text-gray-700">{{ auth()->user()->name }}</p>
                            <p class="text-xs text-gray-500">{{ ucfirst(auth()->user()->role) }}</p>
                        </div>
                        <div class="w-10 h-10 bg-gradient-to-br from-purple-400 to-pink-600 rounded-full flex items-center justify-center text-white font-bold">
                            {{ substr(auth()->user()->name, 0, 1) }}
                        </div>
                        <form action="{{ route('logout') }}" method="POST">
                            @csrf
                            <button type="submit" class="text-gray-600 hover:text-red-600 transition">
                                <i class="fas fa-sign-out-alt text-xl"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </header>

            <!-- Page Content -->
            <main class="main-content">
                @if(session('success'))
                    <div class="mb-6 bg-green-50 border-l-4 border-green-500 p-4 rounded-r-lg">
                        <div class="flex items-center">
                            <i class="fas fa-check-circle text-green-500 mr-3"></i>
                            <p class="text-green-700">{{ session('success') }}</p>
                        </div>
                    </div>
                @endif

                @if(session('error'))
                    <div class="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded-r-lg">
                        <div class="flex items-center">
                            <i class="fas fa-exclamation-circle text-red-500 mr-3"></i>
                            <p class="text-red-700">{{ session('error') }}</p>
                        </div>
                    </div>
                @endif

                @yield('content')
            </main>
        </div>
    </div>

    <script>
        // Auto-hide alerts after 5 seconds
        setTimeout(() => {
            const alerts = document.querySelectorAll('[class*="bg-green-50"], [class*="bg-red-50"]');
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);

        // Mobile menu toggle
        document.addEventListener('DOMContentLoaded', function() {
            const mobileMenuToggle = document.getElementById('mobileMenuToggle');
            const sidebar = document.querySelector('.sidebar');
            
            if (mobileMenuToggle && sidebar) {
                mobileMenuToggle.addEventListener('click', function() {
                    sidebar.classList.toggle('mobile-open');
                });
                
                // Close sidebar when clicking outside on mobile
                document.addEventListener('click', function(e) {
                    if (window.innerWidth <= 768 && 
                        !sidebar.contains(e.target) && 
                        !mobileMenuToggle.contains(e.target)) {
                        sidebar.classList.remove('mobile-open');
                    }
                });
            }
        });
    </script>
    @stack('scripts')
</body>
</html>
