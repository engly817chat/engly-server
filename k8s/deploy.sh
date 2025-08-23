#!/bin/bash

# Engly Server Kubernetes Deployment Script

set -e

echo "🚀 Starting Engly Server Kubernetes Deployment..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Check if we're connected to a cluster
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Not connected to a Kubernetes cluster"
    exit 1
fi

echo "✅ Connected to Kubernetes cluster"

# Function to check if JWT certificates exist
check_jwt_certs() {
    if [[ ! -f "src/main/resources/certs/privateKey.pem" ]] || [[ ! -f "src/main/resources/certs/publicKey.pem" ]]; then
        echo "⚠️  JWT certificates not found. Generating them..."
        if [[ -f "generate_keys.sh" ]]; then
            chmod +x generate_keys.sh
            ./generate_keys.sh
        else
            echo "❌ generate_keys.sh script not found. Please generate JWT certificates manually."
            exit 1
        fi
    else
        echo "✅ JWT certificates found"
    fi
}

# Function to encode JWT certificates to base64
encode_jwt_certs() {
    echo "🔐 Encoding JWT certificates..."

    if command -v base64 &> /dev/null; then
        # Linux/Mac base64
        PRIVATE_KEY_B64=$(base64 -w 0 src/main/resources/certs/privateKey.pem 2>/dev/null || base64 src/main/resources/certs/privateKey.pem | tr -d '\n')
        PUBLIC_KEY_B64=$(base64 -w 0 src/main/resources/certs/publicKey.pem 2>/dev/null || base64 src/main/resources/certs/publicKey.pem | tr -d '\n')
    else
        echo "❌ base64 command not found"
        exit 1
    fi

    # Update the JWT certificates secret
    sed -i.bak "s|privateKey\.pem: \".*\"|privateKey.pem: \"$PRIVATE_KEY_B64\"|" k8s/jwt-certs-secret.yaml
    sed -i.bak "s|publicKey\.pem: \".*\"|publicKey.pem: \"$PUBLIC_KEY_B64\"|" k8s/jwt-certs-secret.yaml

    echo "✅ JWT certificates encoded and updated in k8s/jwt-certs-secret.yaml"
}

# Function to validate required secrets
validate_secrets() {
    echo "🔍 Validating secrets configuration..."

    # Check if critical secrets are still empty
    if grep -q 'CLIENT_ID: ""' k8s/secrets.yaml; then
        echo "⚠️  WARNING: Google OAuth CLIENT_ID is empty in k8s/secrets.yaml"
        echo "   Please update it with: echo -n 'your-client-id' | base64"
    fi

    if grep -q 'ENGLY_EMAIL: ""' k8s/secrets.yaml; then
        echo "⚠️  WARNING: ENGLY_EMAIL is empty in k8s/secrets.yaml"
        echo "   Please update it with: echo -n 'your-email@gmail.com' | base64"
    fi

    echo "📝 Remember to update all empty secrets in k8s/secrets.yaml before production deployment"
}

# Main deployment process
echo "📁 Creating namespace..."
kubectl apply -f k8s/namespace.yaml

# Check and generate JWT certificates if needed
check_jwt_certs

# Encode JWT certificates
encode_jwt_certs

# Apply JWT certificates secret
echo "🔐 Applying JWT certificates secret..."
kubectl apply -f k8s/jwt-certs-secret.yaml

# Validate secrets configuration
validate_secrets

# Apply secrets (you need to update secrets.yaml with your actual values)
echo "🔐 Applying application secrets..."
kubectl apply -f k8s/secrets.yaml

# Apply configmap
echo "⚙️  Applying configuration..."
kubectl apply -f k8s/configmap.yaml

# Deploy PostgreSQL
echo "🐘 Deploying PostgreSQL..."
kubectl apply -f k8s/postgres.yaml

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/postgres -n engly

# Deploy the application
echo "☕ Deploying Engly Server..."
kubectl apply -f k8s/engly-deployment.yaml

# Wait for application to be ready (with longer timeout for Spring Boot startup)
echo "⏳ Waiting for Engly Server to be ready (this may take a few minutes)..."
kubectl wait --for=condition=available --timeout=600s deployment/engly-server -n engly

# Apply ingress (optional)
echo "🌐 Applying ingress..."
kubectl apply -f k8s/ingress.yaml

echo ""
echo "🎉 Deployment completed successfully!"
echo ""
echo "📊 Checking deployment status:"
kubectl get pods -n engly
echo ""
echo "🔗 Services:"
kubectl get services -n engly
echo ""
echo "🌐 Ingress:"
kubectl get ingress -n engly
echo ""
echo "📝 To check application logs:"
echo "   kubectl logs -f deployment/engly-server -n engly"
echo ""
echo "📝 To check database logs:"
echo "   kubectl logs -f deployment/postgres -n engly"
echo ""
echo "🌐 To access the application:"
echo "   kubectl port-forward service/engly-service 8000:8000 -n engly"
echo "   Then visit: http://localhost:8000"
echo ""
echo "🔍 To check application health:"
echo "   kubectl port-forward service/engly-service 8000:8000 -n engly"
echo "   Then visit: http://localhost:8000/actuator/health"
echo ""
echo "📊 To check Prometheus metrics:"
echo "   kubectl port-forward service/engly-service 8000:8000 -n engly"
echo "   Then visit: http://localhost:8000/actuator/prometheus"
echo ""
echo "📚 To access Swagger UI (if enabled):"
echo "   kubectl port-forward service/engly-service 8000:8000 -n engly"
echo "   Then visit: http://localhost:8000/swagger-ui/index.html"
echo ""
echo "⚠️  Don't forget to:"
echo "   1. Update secrets in k8s/secrets.yaml with your actual base64-encoded values"
echo "   2. Update FRONTEND_URL in k8s/configmap.yaml"
echo "   3. Update ingress host in k8s/ingress.yaml for production"
echo "   4. Build and push your Docker image to a registry"
