# Employee Leave Tracker - Docker Setup

## 🚀 Quick Start

### Prerequisites
- Docker
- Docker Compose
- Cloud PostgreSQL access (Aiven)

### Run the Application

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

3. **Default Admin Credentials:**
   - Username: `admin`
   - Password: `admin123`

### Stop the Application
```bash
docker-compose down
```

## 📦 Services

### Database
- **Type:** Cloud PostgreSQL (Aiven)
- **Host:** sksazid-sksazid.i.aivencloud.com:20700
- **Database:** defaultdb
- **SSL:** Required
- **Note:** Database credentials are configured in application.yml

### Backend (Spring Boot)
- **Port:** 8080
- **Java:** 23
- **Framework:** Spring Boot 4.0.1
- **Features:**
  - JWT Authentication (httpOnly cookies)
  - Employee Management
  - Leave Management with Approval Workflow

### Frontend (React + Vite)
- **Port:** 3000
- **Server:** Nginx
- **Features:**
  - Modern React UI
  - Role-based Access Control
  - Dashboard with Statistics

## 🔧 Development

### Rebuild After Code Changes
```bash
docker-compose up -d --build
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
```

## 🌐 Network Configuration

All services run on the same Docker network (`app-network`):
- Backend connects to cloud PostgreSQL (Aiven) via internet
- Frontend proxies API requests to `backend:8080`

## 📝 Environment Variables

### Backend (in docker-compose.yml)
- `JWT_SECRET` - Secret key for JWT token signing
- `JWT_EXPIRATION` - Access token expiration time (24 hours)
- `JWT_REFRESH_EXPIRATION` - Refresh token expiration time (7 days)

### Database Configuration
Database credentials are configured in `application.yml` and `application-docker.yml`

### Frontend
- `VITE_API_URL` (for build-time configuration)

## 🔒 Security Notes

1. **Change default passwords** in production
2. **Use strong JWT secret** (current one is for development)
3. **Enable HTTPS** in production
4. **Configure CORS** appropriately for your domain
5. **Use Docker secrets** for sensitive data in production

## 🐛 Troubleshooting

### Backend won't start
```bash
docker-compose logs backend
# Check if cloud database is accessible
```

### Database connection issues
- Verify cloud PostgreSQL is accessible from your machine
- Check if SSL is enabled for Aiven connection
- Verify credentials in application.yml
- Check backend logs: `docker-compose logs backend`

### Frontend can't reach backend
- Ensure backend is running: `docker-compose ps backend`
- Check backend logs: `docker-compose logs backend`
- Verify network: `docker network inspect misl_assignment_app-network`

## 📊 Production Deployment

For production, consider:
1. Setting up reverse proxy (Nginx/Traefik)
2. Enabling SSL/TLS for frontend
3. Configuring resource limits
4. Setting up monitoring and logging
5. Using Docker secrets for sensitive data (JWT secret, DB credentials)
6. Configuring CORS for your production domain

Example using environment variables:
```yaml
backend:
  environment:
    SPRING_DATASOURCE_URL: ${DB_URL}
    SPRING_DATASOURCE_USERNAME: ${DB_USER}
    SPRING_DATASOURCE_PASSWORD: ${DB_PASS}
    JWT_SECRET: ${JWT_SECRET}
```
