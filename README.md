# PSW Mobile 5.0

A secure private Android application for PSW portfolio management with MySQL backend integration.

## Architecture

### Security-First Design
- **No Direct Database Access**: Android app never connects directly to MySQL
- **API Intermediary**: Secure Node.js API acts as gateway
- **JWT Authentication**: Token-based authentication with secure storage
- **Encrypted Storage**: All sensitive data encrypted on device

### Components

#### API Backend (`/api`)
- **Node.js + Express**: RESTful API server
- **MySQL Integration**: Connects to your existing PSW databases
- **Security Middleware**: Rate limiting, CORS, authentication
- **Environment Variables**: Secure configuration management

#### Android App (`/android`)
- **Jetpack Compose**: Modern UI framework
- **MVVM Architecture**: Clean separation of concerns
- **Hilt Dependency Injection**: Modular and testable
- **Retrofit**: Type-safe HTTP client

## Setup Instructions

### 1. API Backend Setup

```bash
cd api
npm install
```

**Configure Environment:**
```bash
cp .env.example .env
# Edit .env with your MySQL credentials and security settings
```

**Start the API:**
```bash
npm run dev  # Development
npm start    # Production
```

### 2. Android App Setup

**Prerequisites:**
- Android Studio Arctic Fox or later
- JDK 11+
- Android SDK 24+ (Android 7.0)

**Configuration:**
1. Open `android/` folder in Android Studio
2. Update API endpoint in `app/build.gradle`:
   ```kotlin
   buildConfigField "String", "API_BASE_URL", "\"http://your-server-ip:3000/api/v1/\""
   ```
3. Sync project and build

## Database Schema

The app displays data from your `new_companies` table:
- **Company Information**: Name, ticker, ISIN, country
- **Financial Data**: Yield percentages, status tracking
- **Metadata**: Creation dates, broker info, strategy groups

## Security Features

### API Security
- JWT token authentication
- Rate limiting (100 requests/15 minutes)
- Request validation and sanitization
- CORS protection
- SQL injection prevention

### Android Security
- Encrypted SharedPreferences for token storage
- Certificate pinning ready
- Network security config
- ProGuard obfuscation ready

## Default Credentials
- **Username**: `admin`
- **Password**: `password123`

⚠️ **Change these in production!**

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/verify` - Token verification

### Dashboard
- `GET /api/v1/dashboard/overview` - Dashboard data

### Companies
- `GET /api/v1/companies` - List companies (with filtering)
- `GET /api/v1/companies/{id}` - Get company details
- `GET /api/v1/companies/stats/summary` - Company statistics

## Development

### API Development
```bash
cd api
npm run dev  # Auto-restart on changes
```

### Android Development
1. Use Android Studio's built-in emulator
2. For physical device testing, ensure both devices are on same network
3. Update API base URL for your network configuration

## Production Deployment

### API Deployment
1. Set `NODE_ENV=production`
2. Configure proper JWT secrets
3. Set up HTTPS with valid certificates
4. Configure firewall rules
5. Set up monitoring and logging

### Android Deployment
1. Generate signed APK/Bundle
2. Update API endpoints to production URLs
3. Enable ProGuard/R8 optimization
4. Test on multiple devices

## Troubleshooting

### Common Issues
1. **Connection Refused**: Check API server is running and firewall allows port 3000
2. **Authentication Failed**: Verify credentials and JWT secret configuration
3. **Empty Dashboard**: Check MySQL connection and table permissions

### Network Configuration
- **Development**: Use `10.0.2.2:3000` for Android emulator
- **Physical Device**: Use your computer's IP address
- **Production**: Use your server's public IP/domain

## Next Steps

1. **Enhanced Security**: Implement certificate pinning, API key rotation
2. **Additional Features**: Company editing, portfolio analytics, charts
3. **Offline Support**: Local caching, sync mechanisms
4. **Push Notifications**: Real-time updates, alerts
5. **User Management**: Multi-user support, role-based access