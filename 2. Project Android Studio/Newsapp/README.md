# News App - WebView Controller

An Android News App built with Kotlin that provides a fully-featured web browsing experience for reading news websites. The app includes WebView controls, navigation features, and quick access to popular news sites.

## Features

### 🌐 WebView Features
- **Full WebView Integration**: Complete web browsing experience
- **JavaScript Support**: Enabled for modern web applications
- **Zoom Controls**: Built-in zoom functionality
- **Mixed Content Support**: HTTP/HTTPS compatibility

### 🎮 Navigation Controls
- **Back/Forward Navigation**: Browser-style navigation
- **Refresh Function**: Reload current page
- **Progress Indicator**: Visual loading progress
- **Status Bar**: Real-time loading status

### 🚀 Quick Access
- **Popular News Sites**: One-tap access to:
  - Detik.com (Indonesian News)
  - Kompas.com (Indonesian News)
  - CNN International
  - BBC News
- **Custom URL Input**: Enter any website URL
- **Smart URL Handling**: Automatic HTTPS prefix

### 📱 Modern UI Features
- **Material Design 3**: Modern Android UI components
- **Swipe to Refresh**: Pull-down to reload pages
- **Responsive Layout**: Optimized for different screen sizes
- **Custom Color Scheme**: Professional blue and orange theme

### 🛠️ Advanced Features
- **Menu Options**:
  - Share current page
  - Add bookmarks (placeholder)
  - Clear cache and history
  - Settings (placeholder)
- **Error Handling**: Graceful error management
- **Back Button Override**: Natural browser navigation

## Technical Implementation

### Architecture
- **Single Activity**: MainActivity with ViewBinding
- **WebView Configuration**: Optimized settings for news consumption
- **Material Toolbar**: Custom action bar with menu items
- **Linear Layout**: Organized UI structure

### WebView Settings
```kotlin
- JavaScript: Enabled
- DOM Storage: Enabled
- Zoom Controls: Enabled (built-in)
- Mixed Content: Compatibility mode
- Wide Viewport: Enabled
- Load Overview Mode: Enabled
```

### Dependencies
- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- SwipeRefreshLayout
- ConstraintLayout

## How to Use

1. **Launch the App**: Opens with Detik.com as default
2. **Enter Custom URL**: Type any website URL in the input field
3. **Quick Access**: Tap news site chips for instant access
4. **Navigate**: Use Back/Forward buttons for browsing
5. **Refresh**: Tap refresh button or swipe down
6. **Share**: Use menu to share current page
7. **Clear Data**: Clear cache through menu options

## Screenshots

The app provides:
- Clean, professional interface
- Intuitive navigation controls  
- Quick access to popular news sites
- Real-time loading indicators
- Responsive design elements

## Development Notes

### Project Structure
```
app/
├── src/main/
│   ├── java/com/example/newsapp/
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml
│   │   ├── drawable/
│   │   │   ├── ic_menu.xml
│   │   │   ├── ic_bookmark.xml
│   │   │   └── ic_share.xml
│   │   ├── menu/
│   │   │   └── main_menu.xml
│   │   └── values/
│   │       ├── colors.xml
│   │       └── strings.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

### Key Components
- **WebView**: Core browsing functionality
- **MaterialToolbar**: App bar with menu
- **TextInputEditText**: URL input field
- **MaterialButtons**: Navigation controls
- **Chips**: Quick access buttons
- **SwipeRefreshLayout**: Pull-to-refresh
- **ProgressIndicator**: Loading progress

### Permissions
- `INTERNET`: Network access for web browsing
- `ACCESS_NETWORK_STATE`: Check network connectivity

## Future Enhancements

- [ ] Bookmark management system
- [ ] Download manager
- [ ] Full-screen reading mode
- [ ] Dark mode support
- [ ] History management
- [ ] Search functionality
- [ ] Offline reading capability
- [ ] Push notifications for news updates

## Requirements

- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 36)
- **Compile SDK**: 36
- **Language**: Kotlin
- **Build Tool**: Gradle KTS

## Installation

1. Clone or download the project
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

This News App demonstrates modern Android development practices with WebView integration, providing users with a seamless news reading experience across multiple platforms and websites.