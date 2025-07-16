#!/bin/bash

# Super Organizer API Kubernetes Deployment Script
# This script deploys the Super Organizer API to a Kubernetes cluster

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="super-organizer"
APP_NAME="super-organizer-api"
IMAGE_NAME="super-organizer"
IMAGE_TAG="1.0.0"

echo -e "${GREEN}🚀 Starting Super Organizer API Kubernetes Deployment${NC}"

# Function to check if kubectl is installed
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        echo -e "${RED}❌ kubectl is not installed. Please install kubectl first.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ kubectl is installed${NC}"
}

# Function to check if namespace exists
check_namespace() {
    if kubectl get namespace $NAMESPACE &> /dev/null; then
        echo -e "${YELLOW}⚠️  Namespace $NAMESPACE already exists${NC}"
    else
        echo -e "${GREEN}📦 Creating namespace $NAMESPACE${NC}"
        kubectl apply -f k8s/namespace.yaml
    fi
}

# Function to build Docker image
build_image() {
    echo -e "${GREEN}🔨 Building Docker image${NC}"
    docker build -f k8s/Dockerfile -t $IMAGE_NAME:$IMAGE_TAG .
    echo -e "${GREEN}✅ Docker image built successfully${NC}"
}

# Function to deploy to Kubernetes
deploy_to_k8s() {
    echo -e "${GREEN}📦 Deploying to Kubernetes${NC}"
    
    # Apply all Kubernetes manifests
    echo -e "${YELLOW}📋 Applying Kubernetes manifests...${NC}"
    
    # Create namespace first
    kubectl apply -f k8s/namespace.yaml
    
    # Apply secrets
    kubectl apply -f k8s/secret.yaml
    
    # Apply ConfigMaps
    kubectl apply -f k8s/configmap.yaml
    kubectl apply -f k8s/postgres-init-configmap.yaml
    
    # Apply storage
    kubectl apply -f k8s/postgres-persistent-volume.yaml
    
    # Apply PostgreSQL deployment
    kubectl apply -f k8s/postgres-deployment.yaml
    
    # Wait for PostgreSQL to be ready
    echo -e "${YELLOW}⏳ Waiting for PostgreSQL to be ready...${NC}"
    kubectl wait --for=condition=ready pod -l app=super-organizer-postgres -n $NAMESPACE --timeout=300s
    
    # Apply application deployment
    kubectl apply -f k8s/app-deployment.yaml
    
    # Apply HPA
    kubectl apply -f k8s/hpa.yaml
    
    # Apply Ingress (optional - only if you have ingress controller)
    if [ "$1" = "--with-ingress" ]; then
        kubectl apply -f k8s/ingress.yaml
        echo -e "${GREEN}✅ Ingress applied${NC}"
    fi
    
    echo -e "${GREEN}✅ All Kubernetes resources applied successfully${NC}"
}

# Function to check deployment status
check_status() {
    echo -e "${GREEN}📊 Checking deployment status...${NC}"
    
    echo -e "${YELLOW}📋 Pods:${NC}"
    kubectl get pods -n $NAMESPACE
    
    echo -e "${YELLOW}📋 Services:${NC}"
    kubectl get services -n $NAMESPACE
    
    echo -e "${YELLOW}📋 Deployments:${NC}"
    kubectl get deployments -n $NAMESPACE
    
    echo -e "${YELLOW}📋 HPA:${NC}"
    kubectl get hpa -n $NAMESPACE
    
    if [ "$1" = "--with-ingress" ]; then
        echo -e "${YELLOW}📋 Ingress:${NC}"
        kubectl get ingress -n $NAMESPACE
    fi
}

# Function to show access information
show_access_info() {
    echo -e "${GREEN}🌐 Access Information:${NC}"
    echo -e "${YELLOW}API Base URL:${NC} http://localhost:8080/api/tasks"
    echo -e "${YELLOW}Swagger UI:${NC} http://localhost:8080/swagger-ui.html"
    echo -e "${YELLOW}API Documentation:${NC} http://localhost:8080/api-docs"
    
    echo -e "${GREEN}📋 To port-forward the service:${NC}"
    echo -e "${YELLOW}kubectl port-forward service/super-organizer-api 8080:80 -n $NAMESPACE${NC}"
    
    if [ "$1" = "--with-ingress" ]; then
        echo -e "${GREEN}🌐 Ingress URL:${NC}"
        echo -e "${YELLOW}http://super-organizer.local${NC}"
        echo -e "${YELLOW}Note: Update your /etc/hosts file or configure DNS${NC}"
    fi
}

# Function to clean up
cleanup() {
    echo -e "${YELLOW}🧹 Cleaning up deployment...${NC}"
    kubectl delete namespace $NAMESPACE
    echo -e "${GREEN}✅ Cleanup completed${NC}"
}

# Main script logic
case "$1" in
    "deploy")
        check_kubectl
        check_namespace
        build_image
        deploy_to_k8s $2
        check_status $2
        show_access_info $2
        ;;
    "status")
        check_status $2
        ;;
    "cleanup")
        cleanup
        ;;
    "logs")
        if [ -z "$2" ]; then
            echo -e "${RED}❌ Please specify pod name or use 'all' for all pods${NC}"
            exit 1
        fi
        if [ "$2" = "all" ]; then
            kubectl logs -f -l app=$APP_NAME -n $NAMESPACE
        else
            kubectl logs -f $2 -n $NAMESPACE
        fi
        ;;
    *)
        echo -e "${GREEN}Super Organizer API Kubernetes Deployment Script${NC}"
        echo ""
        echo "Usage: $0 {deploy|status|cleanup|logs} [options]"
        echo ""
        echo "Commands:"
        echo "  deploy [--with-ingress]  - Deploy the application to Kubernetes"
        echo "  status [--with-ingress]  - Show deployment status"
        echo "  cleanup                  - Clean up the deployment"
        echo "  logs <pod-name|all>      - Show logs for a specific pod or all pods"
        echo ""
        echo "Examples:"
        echo "  $0 deploy                - Deploy without ingress"
        echo "  $0 deploy --with-ingress - Deploy with ingress"
        echo "  $0 status                - Show status"
        echo "  $0 logs all              - Show logs for all pods"
        echo "  $0 cleanup               - Clean up deployment"
        ;;
esac