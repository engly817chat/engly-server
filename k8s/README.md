# Engly Server Kubernetes Deployment

This directory contains Kubernetes YAML files and scripts to deploy the Engly Server Spring Boot application on a Kubernetes cluster.

## 📁 Files Overview

- `namespace.yaml` - Creates the `engly` namespace for all resources
- `secrets.yaml` - Contains sensitive configuration data (database credentials, OAuth keys, etc.)
- `jwt-certs-secret.yaml` - Separate secret for JWT RSA certificates
- `configmap.yaml` - Contains non-sensitive configuration data from your Spring Boot YAML files
- `postgres.yaml` - PostgreSQL database deployment with persistent storage
- `engly-deployment.yaml` - Main Spring Boot application deployment and services
- `ingress.yaml` - Ingress configuration with CORS and rate limiting
- `deploy.sh` - Automated deployment script with JWT certificate handling
- `cleanup.sh` - Cleanup script to remove all resources

## 🔧 Prerequisites

1. **Kubernetes Cluster**: Running Kubernetes cluster (local or cloud)
2. **kubectl**: Kubernetes CLI tool installed and configured
3. **Docker Image**: Build and push your Engly Server Docker image to a registry
4. **JWT Certificates**: Your `src/main/resources/certs/` directory with RSA keys
5. **Ingress Controller**: (Optional) Install NGINX Ingress Controller for external access

## 🚀 Quick Start

### 1. Build and Push Docker Image

First, build your Docker image and push it to a registry:

```bash
# Build the image using your existing Dockerfile
docker build -t your-registry/engly-server:latest .

# Push to registry
docker push your-registry/engly-server:latest
```

### 2. Update Configuration

**Update secrets.yaml** with your actual base64-encoded values:
```bash
# Example: Encode a value to base64
echo -n "your-actual-value" | base64
```

**Required secrets to update:**
- `CLIENT_ID` - Google OAuth client ID (from security.yml)
- `CLIENT_SECRET` - Google OAuth client secret (from security.yml)
- `ENGLY_EMAIL` - Your Gmail address (from security.yml)
- `ENGLY_EMAIL_PASSWORD` - Your Gmail app password (from security.yml)
- `SYS_EMAIL` - System admin email (from application.properties)
- `DEV_EMAIL` - Developer email (from application.properties)
- `GOOGLE_DRIVE_*` - Google Drive API credentials (from application.properties)

**Update configmap.yaml** with your actual URLs:
- `FRONTEND_URL` - Your frontend application URL (referenced in application.properties)
- Update other configuration values as needed

**Update engly-deployment.yaml**:
- Change `image: engly-server:latest` to your actual image name and tag

**Update ingress.yaml**:
- Change `host: engly.local` to your actual domain
- Update `cors-allow-origin` to match your frontend URL

### 3. Deploy

The deployment script will automatically handle JWT certificates:

```bash
chmod +x k8s/deploy.sh
./k8s/deploy.sh
```

The script will:
- Check for JWT certificates in `src/main/resources/certs/`
- Run `generate_keys.sh` if certificates don't exist
- Automatically encode certificates to base64 and update the secret
- Deploy all resources in the correct order
- Wait for services to be ready

## 🔍 Application Features Deployed

Based on your Spring Boot configuration, the deployment includes:

### 📊 Monitoring & Observability (from monitoring.yml)
- **Actuator endpoints**: `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`
- **Health checks**: Liveness and readiness probes
- **Prometheus metrics**: Exposed on `/actuator/prometheus`
- **Application info**: Available at `/actuator/info`

### 🔐 Security (from security.yml)
- **Google OAuth2**: Configured with your client credentials
- **JWT Authentication**: Using your RSA certificates
- **Email service**: Gmail SMTP configuration
- **CORS**: Properly configured for your frontend

### 🗄️ Database (from database.yml)
- **PostgreSQL**: With optimized HikariCP connection pooling
- **Flyway migrations**: Automatic database schema management
- **Connection settings**: 20 max connections, optimized timeouts

### ⚡ Performance (from server.yml)
- **HTTP/2**: Enabled for better performance
- **Compression**: Enabled for responses
- **Virtual threads**: Java 21+ virtual threads enabled
- **Rate limiting**: Based on your resilience4j configuration

### 📁 File Uploads
- **Max file size**: 10MB (configurable via MAX_FILE_SIZE)
- **Google Drive integration**: For file storage

## 🔍 Monitoring and Troubleshooting

### Check Deployment Status
```bash
kubectl get pods -n engly
kubectl get services -n engly
kubectl get ingress -n engly
```

### View Logs
```bash
# Application logs
kubectl logs -f deployment/engly-server -n engly

# PostgreSQL logs
kubectl logs -f deployment/postgres -n engly

# Check startup issues
kubectl describe pod <pod-name> -n engly
```

### Access Application Endpoints
```bash
# Port forward to access locally
kubectl port-forward service/engly-service 8000:8000 -n engly

# Then access:
# Main application: http://localhost:8000
# Health check: http://localhost:8000/actuator/health
# Metrics: http://localhost:8000/actuator/prometheus
# API docs: http://localhost:8000/swagger-ui/index.html
```

### Debug Common Issues

**Application won't start:**
```bash
# Check if JWT certificates are mounted correctly
kubectl exec -it deployment/engly-server -n engly -- ls -la /app/certs/

# Check environment variables
kubectl exec -it deployment/engly-server -n engly -- env | grep -E "(DB_|SPRING_|CLIENT_)"
```

**Database connection issues:**
```bash
# Test database connectivity
kubectl exec -it deployment/postgres -n engly -- psql -U englyuser -d engly -c "SELECT 1;"
```

## 🌐 External Access

### Using NodePort (Development)
The deployment includes a NodePort service on port 30000:
```bash
# Access via node IP
http://<node-ip>:30000
```

### Using Ingress (Production)
1. Install NGINX Ingress Controller:
   ```bash
   kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
   ```

2. Update `/etc/hosts` file (for local testing):
   ```
   <cluster-ip> engly.local
   ```

3. Access via: `http://engly.local`

## 🗄️ Database Management

### Access PostgreSQL
```bash
# Port forward to PostgreSQL
kubectl port-forward service/postgres-service 5432:5432 -n engly

# Connect using psql
psql -h localhost -p 5432 -U englyuser -d engly
```

### Backup Database
```bash
# Create backup
kubectl exec deployment/postgres -n engly -- pg_dump -U englyuser engly > backup.sql

# Restore backup
kubectl exec -i deployment/postgres -n engly -- psql -U englyuser -d engly < backup.sql
```

## 📊 Application Monitoring

Your Spring Boot application exposes comprehensive monitoring:

### Health Endpoints
- `/actuator/health` - Overall health status
- `/actuator/health/liveness` - Kubernetes liveness probe
- `/actuator/health/readiness` - Kubernetes readiness probe

### Metrics Endpoints
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus-formatted metrics
- `/actuator/caches` - Cache statistics (Caffeine)

### Configuration Endpoints
- `/actuator/configprops` - Configuration properties
- `/actuator/env` - Environment variables
- `/actuator/info` - Application information

## 🔒 Security Considerations

1. **Secrets Management**: 
   - Never commit actual secrets to version control
   - Consider using external secret management (Vault, AWS Secrets Manager)
   - Rotate secrets regularly

2. **Network Security**:
   - The deployment uses ClusterIP for internal communication
   - CORS is configured for your frontend domain
   - Rate limiting is applied via ingress

3. **Database Security**:
   - PostgreSQL runs with non-root user
   - Database credentials are stored in Kubernetes secrets
   - Persistent volume for data retention

## 🧹 Cleanup

To remove all resources:
```bash
chmod +x k8s/cleanup.sh
./k8s/cleanup.sh
```

⚠️ **Warning**: This will delete the PostgreSQL database and all data!

## 📈 Scaling

Scale the application:
```bash
kubectl scale deployment engly-server --replicas=3 -n engly
```

Scale the database (if using a scalable PostgreSQL solution):
```bash
# For development, PostgreSQL runs as single instance
# For production, consider PostgreSQL cluster solutions
```

## 🔧 Customization

### Resource Limits
Adjust CPU and memory in `engly-deployment.yaml`:
```yaml
resources:
  requests:
    memory: "768Mi"    # Optimized for your Tomcat config
    cpu: "500m"
  limits:
    memory: "1536Mi"   # Allows for JVM overhead
    cpu: "1500m"
```

### Database Storage
Increase PostgreSQL storage in `postgres.yaml`:
```yaml
spec:
  resources:
    requests:
      storage: 50Gi  # Increase as needed
```

### Profile Configuration
Change Spring profiles in `configmap.yaml`:
```yaml
SPRING_PROFILES_ACTIVE: "prod"  # Change from 'engly' to 'prod'
```

## 🔄 CI/CD Integration

Example GitHub Actions workflow:
```yaml
name: Deploy to Kubernetes
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Build and push image
      run: |
        docker build -t your-registry/engly-server:${{ github.sha }} .
        docker push your-registry/engly-server:${{ github.sha }}
    - name: Update deployment
      run: |
        sed -i 's|engly-server:latest|engly-server:${{ github.sha }}|' k8s/engly-deployment.yaml
        kubectl apply -f k8s/
```

## 🆘 Support

For deployment issues:
1. Check application logs for Spring Boot errors
2. Verify all environment variables are set correctly
3. Ensure JWT certificates are properly mounted
4. Check database connectivity
5. Verify ingress configuration for external access

Common Spring Boot startup issues:
- Missing environment variables
- Database connection failures
- JWT certificate path issues
- Port conflicts
- Resource constraints
