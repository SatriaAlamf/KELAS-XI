<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Auth;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8',
            'kelas_id' => 'nullable|exists:kelas,id',
        ]);

        // Auto-assign role based on kelas_id
        $role = 'siswa'; // Default role for users with kelas_id
        if (!$request->kelas_id) {
            $role = 'admin'; // Users without kelas_id are admin/staff
        }

        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'role' => $role,
            'kelas_id' => $request->kelas_id,
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        // Load kelas relationship for response
        $user->load('kelas');

        $responseData = [
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'role' => $user->role,
                'kelas_id' => $user->kelas_id,
                'kelas' => $user->kelas ? [
                    'id' => $user->kelas->id,
                    'nama_kelas' => $user->kelas->nama_kelas
                ] : null
            ],
            'token' => $token,
        ];

        return response()->json([
            'success' => true,
            'message' => 'User registered successfully',
            'data' => $responseData,
        ], 201);
    }

    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $user = User::with('kelas')->where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['The provided credentials are incorrect.'],
            ]);
        }

        $token = $user->createToken('auth_token')->plainTextToken;

        // Prepare response data with kelas information
        $responseData = [
            'user' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'role' => $user->role,
                'kelas_id' => $user->kelas_id,
                'kelas' => $user->kelas ? [
                    'id' => $user->kelas->id,
                    'nama_kelas' => $user->kelas->nama_kelas
                ] : null
            ],
            'token' => $token,
        ];

        return response()->json([
            'success' => true,
            'message' => 'Login successful',
            'data' => $responseData,
        ], 200);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'success' => true,
            'message' => 'Logged out successfully',
        ], 200);
    }
    
    // Web Login Methods
    public function showLoginForm()
    {
        return view('auth.login');
    }
    
    public function webLogin(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $user = User::where('email', $request->email)->first();

        if (!$user || !Hash::check($request->password, $user->password)) {
            return back()->withErrors([
                'email' => 'Email atau password salah.',
            ])->withInput();
        }

        Auth::login($user);

        return redirect()->route('admin.dashboard');
    }
    
    public function webLogout(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('login');
    }
}
