#!/bin/bash

# Engly Server Kubernetes Cleanup Script

set -e

echo "🧹 Starting Engly Server Kubernetes Cleanup..."

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

echo "⚠️  This will delete all Engly Server resources from Kubernetes"
echo "   Including: PostgreSQL database with all data!"
read -p "Are you sure you want to continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Cleanup cancelled"
    exit 1
fi

echo "🗑️  Removing ingress..."
kubectl delete -f k8s/ingress.yaml --ignore-not-found=true

echo "🗑️  Removing application deployment..."
kubectl delete -f k8s/engly-deployment.yaml --ignore-not-found=true

echo "🗑️  Removing PostgreSQL (this will delete all data)..."
kubectl delete -f k8s/postgres.yaml --ignore-not-found=true

echo "🗑️  Removing configuration..."
kubectl delete -f k8s/configmap.yaml --ignore-not-found=true

echo "🗑️  Removing JWT certificates secret..."
kubectl delete -f k8s/jwt-certs-secret.yaml --ignore-not-found=true

echo "🗑️  Removing application secrets..."
kubectl delete -f k8s/secrets.yaml --ignore-not-found=true

echo "🗑️  Removing namespace (this will delete any remaining resources)..."
kubectl delete -f k8s/namespace.yaml --ignore-not-found=true

# Wait for namespace deletion
echo "⏳ Waiting for namespace deletion to complete..."
kubectl wait --for=delete namespace/engly --timeout=120s 2>/dev/null || echo "Namespace deletion completed or timed out"

echo ""
echo "✅ Cleanup completed successfully!"
echo "All Engly Server resources have been removed from Kubernetes."
echo ""
echo "📝 Note: Local files and Docker images are not affected."
echo "   - JWT certificates in src/main/resources/certs/ are preserved"
echo "   - Docker images need to be removed manually if desired"
