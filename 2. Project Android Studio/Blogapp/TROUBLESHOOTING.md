# 🔧 TROUBLESHOOTING GUIDE - BLOG APP

## 🚨 COMMON ERRORS & SOLUTIONS

---

### 1. **Firebase Initialization Error**

#### ❌ Error Message:
```
Default FirebaseApp is not initialized
```

#### ✅ Solutions:
1. **Check google-services.json**
   ```
   ✓ File ada di: app/google-services.json
   ✓ Bukan di root project atau folder lain
   ```

2. **Check build.gradle.kts (app level)**
   ```kotlin
   plugins {
       alias(libs.plugins.google.services)  // ✓ Ada?
   }
   ```

3. **Sync & Rebuild**
   ```
   File > Sync Project with Gradle Files
   Build > Clean Project
   Build > Rebuild Project
   ```

---

### 2. **Permission Denied (Firestore/Storage)**

#### ❌ Error Message:
```
PERMISSION_DENIED: Missing or insufficient permissions
```

#### ✅ Solutions:

**Firestore Rules (Test Mode):**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

**Storage Rules (Test Mode):**
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

**Cara Update Rules:**
1. Buka Firebase Console
2. Firestore Database > Rules
3. Copy-paste rules di atas
4. Publish

---

### 3. **Authentication Error**

#### ❌ Error Message:
```
Authentication failed
User not found
Invalid credentials
```

#### ✅ Solutions:

1. **Check Firebase Console**
   ```
   ✓ Authentication > Sign-in method
   ✓ Email/Password is ENABLED
   ```

2. **Check Email Format**
   ```kotlin
   // Email harus valid format
   john@example.com  ✓
   john@example      ✗
   john              ✗
   ```

3. **Check Password Length**
   ```kotlin
   // Password min 6 karakter
   123456    ✓
   12345     ✗
   ```

---

### 4. **Image Upload Failed**

#### ❌ Error Message:
```
Upload failed
Image too large
Storage error
```

#### ✅ Solutions:

1. **Check Permissions**
   ```xml
   <!-- AndroidManifest.xml -->
   <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
   ```

2. **Grant Runtime Permission**
   ```
   Settings > Apps > Blog App > Permissions > Storage > Allow
   ```

3. **Check Image Size**
   ```
   Max size: 2MB (2,048 KB)
   Jika lebih, compress dulu sebelum upload
   ```

4. **Check Storage Rules**
   ```
   Firebase Console > Storage > Rules
   Pastikan allow write untuk authenticated users
   ```

---

### 5. **RecyclerView Not Showing Data**

#### ❌ Problem:
RecyclerView kosong padahal data ada di Firestore

#### ✅ Solutions:

1. **Check Collection Name**
   ```kotlin
   // Pastikan nama collection sama
   Constants.COLLECTION_BLOGS = "blogs"  // lowercase!
   ```

2. **Check Firestore Data**
   ```
   Firebase Console > Firestore > blogs collection
   ✓ Ada data?
   ✓ Field names benar? (blogId, title, content, etc)
   ```

3. **Check Adapter Setup**
   ```kotlin
   // MainActivity.kt
   binding.rvBlogs.adapter = blogAdapter  // ✓ Set?
   binding.rvBlogs.layoutManager = LinearLayoutManager(this)  // ✓ Set?
   ```

4. **Check Logcat**
   ```
   Logcat > Filter: "Firestore"
   Lihat error messages
   ```

---

### 6. **Build/Gradle Errors**

#### ❌ Error Message:
```
Failed to resolve: com.google.firebase:firebase-bom
Plugin not found
```

#### ✅ Solutions:

1. **Check Internet Connection**
   ```
   Gradle needs internet to download dependencies
   ```

2. **Sync Gradle**
   ```
   File > Sync Project with Gradle Files
   Wait until finished (check bottom status bar)
   ```

3. **Invalidate Caches**
   ```
   File > Invalidate Caches and Restart
   Click "Invalidate and Restart"
   ```

4. **Check libs.versions.toml**
   ```toml
   [versions]
   firebaseBom = "33.5.1"  // ✓ Ada?
   googleServices = "4.4.2"  // ✓ Ada?
   ```

---

### 7. **App Crashes on Launch**

#### ❌ Problem:
App crashes immediately after opening

#### ✅ Solutions:

1. **Check Logcat**
   ```
   View > Tool Windows > Logcat
   Filter: "AndroidRuntime"
   Look for "FATAL EXCEPTION"
   ```

2. **Common Crash Causes:**
   ```
   ✗ Missing google-services.json
   ✗ Wrong package name in Firebase
   ✗ Internet permission missing
   ✗ Firebase services not enabled
   ```

3. **Quick Fix:**
   ```
   Build > Clean Project
   Build > Rebuild Project
   File > Invalidate Caches and Restart
   Run app again
   ```

---

### 8. **Network Error**

#### ❌ Error Message:
```
No internet connection
Network request failed
```

#### ✅ Solutions:

1. **Check Device/Emulator Internet**
   ```
   ✓ WiFi connected?
   ✓ Mobile data on?
   ✓ Airplane mode off?
   ```

2. **Check Manifest Permission**
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

3. **Test Connection**
   ```kotlin
   if (AppUtils.isInternetAvailable(context)) {
       // Internet OK
   } else {
       // No internet
   }
   ```

---

### 9. **Glide Image Loading Issues**

#### ❌ Problem:
Images not loading or showing placeholder

#### ✅ Solutions:

1. **Check Image URL**
   ```kotlin
   // URL harus valid dan accessible
   Log.d("ImageURL", blog.imageUrl)
   ```

2. **Check Internet Permission**
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

3. **Check Glide Setup**
   ```kotlin
   Glide.with(context)
       .load(imageUrl)
       .placeholder(R.drawable.placeholder)  // ✓ Ada?
       .error(R.drawable.error)  // ✓ Ada?
       .into(imageView)
   ```

---

### 10. **ViewBinding Not Found**

#### ❌ Error Message:
```
Unresolved reference: ActivityMainBinding
```

#### ✅ Solutions:

1. **Enable ViewBinding**
   ```kotlin
   // app/build.gradle.kts
   android {
       buildFeatures {
           viewBinding = true  // ✓ Ada?
       }
   }
   ```

2. **Sync Gradle**
   ```
   File > Sync Project with Gradle Files
   ```

3. **Rebuild Project**
   ```
   Build > Rebuild Project
   ```

---

## 🔍 DEBUG TIPS

### Logcat Filters:
```
System.out   - For println() logs
AndroidRuntime - For crash logs
Firestore    - For Firestore errors
StorageException - For Storage errors
FirebaseAuth - For Auth errors
```

### Enable Debug Logging:
```kotlin
// Tambahkan di onCreate() MainActivity
Firebase.setLoggingEnabled(true)
```

### Check Firebase Console:
```
1. Usage & Billing - Check quota
2. Firestore Data - Check if data saved
3. Storage Files - Check if images uploaded
4. Authentication Users - Check if user registered
```

---

## 📞 STILL HAVING ISSUES?

### Step-by-step Debug Process:

1. **Read Error Message**
   ```
   ✓ Baca Logcat dengan teliti
   ✓ Google error message (biasanya ada solusi)
   ```

2. **Check Firebase Console**
   ```
   ✓ Services enabled?
   ✓ Rules correct?
   ✓ Data saved?
   ```

3. **Clean & Rebuild**
   ```
   Build > Clean Project
   Build > Rebuild Project
   File > Invalidate Caches > Restart
   ```

4. **Check Basics**
   ```
   ✓ Internet connected?
   ✓ google-services.json exists?
   ✓ Gradle synced successfully?
   ```

5. **Check Documentation**
   ```
   ✓ QUICK_START.md
   ✓ FIREBASE_SETUP_GUIDE.md
   ✓ README.md
   ```

---

## 🎓 PREVENTION TIPS

### Before Running App:
- [ ] google-services.json in app/ folder
- [ ] Gradle sync successful
- [ ] All Firebase services enabled
- [ ] Internet connection available
- [ ] Clean & Rebuild project

### Regular Maintenance:
- [ ] Update Firebase SDK regularly
- [ ] Check Firebase costs monthly
- [ ] Monitor Crashlytics for errors
- [ ] Keep dependencies updated
- [ ] Test on different devices

---

## 🆘 EMERGENCY FIXES

### If Everything Fails:

1. **Nuclear Option - Full Clean:**
   ```bash
   # Close Android Studio
   # Delete folders:
   - .gradle/
   - .idea/
   - app/build/
   - build/
   
   # Reopen Android Studio
   File > Sync Project with Gradle Files
   Build > Rebuild Project
   ```

2. **Reimport Project:**
   ```
   File > Close Project
   File > Open > Select project folder
   ```

3. **Update Android Studio:**
   ```
   Help > Check for Updates
   Update to latest stable version
   ```

---

## ✅ SUCCESS INDICATORS

**App Working When:**
- ✅ No red errors in Logcat
- ✅ Firebase Console shows data
- ✅ Images loading correctly
- ✅ Auth working (login/register)
- ✅ Blog creation successful
- ✅ Real-time updates working

---

**Good Luck! 🍀**

*Remember: Most errors are configuration issues. Double-check Firebase setup first!*
