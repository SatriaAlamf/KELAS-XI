<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        $this->call([
            CompleteSchoolSeeder::class,
        ]);
        
        $this->command->info('');
        $this->command->info('✅ Database seeding selesai! Siap digunakan.');
    }
}
