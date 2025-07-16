# Super Organizer API - Kubernetes Deployment

This directory contains all the Kubernetes configuration files needed to deploy the Super Organizer API to a Kubernetes cluster.

## 📁 File Structure

```
k8s/
├── namespace.yaml                    # Namespace definition
├── configmap.yaml                   # Application configuration
├── secret.yaml                      # Database credentials
├── postgres-persistent-volume.yaml  # Storage configuration
├── postgres-deployment.yaml         # PostgreSQL deployment
├── postgres-init-configmap.yaml     # Database initialization scripts
├── app-deployment.yaml              # Main application deployment
├── ingress.yaml                     # Ingress configuration
├── hpa.yaml                         # Horizontal Pod Autoscaler
├── Dockerfile                       # Container image definition
├── deploy.sh                        # Deployment automation script
└── README.md                        # This file
```

## 🚀 Quick Start

### Prerequisites

1. **Kubernetes Cluster**: A running Kubernetes cluster (local or cloud)
2. **kubectl**: Kubernetes command-line tool
3. **Docker**: For building the application image
4. **Ingress Controller**: (Optional) For external access

### Deployment Steps

1. **Clone the repository** (if not already done):
   ```bash
   git clone <repository-url>
   cd my_organizer_api
   ```

2. **Deploy the application**:
   ```bash
   # Deploy without ingress (for local development)
   ./k8s/deploy.sh deploy
   
   # Deploy with ingress (for production)
   ./k8s/deploy.sh deploy --with-ingress
   ```

3. **Check deployment status**:
   ```bash
   ./k8s/deploy.sh status
   ```

4. **Access the application**:
   ```bash
   # Port forward to access locally
   kubectl port-forward service/super-organizer-api 8080:80 -n super-organizer
   ```

## 📋 Configuration Details

### Application Configuration

The application is configured via ConfigMap (`configmap.yaml`) with the following settings:

- **Database**: PostgreSQL with persistent storage
- **Port**: 8080 (internal), 80 (service)
- **Health Checks**: `/actuator/health` endpoint
- **Swagger UI**: Available at `/swagger-ui.html`

### Database Configuration

- **Database**: PostgreSQL 15
- **Storage**: 10Gi persistent volume
- **Credentials**: Stored in Kubernetes secrets
- **Initialization**: Automatic schema and sample data creation

### Scaling Configuration

- **Replicas**: 3 (default)
- **HPA**: Automatic scaling based on CPU (70%) and memory (80%)
- **Min/Max Replicas**: 2-10

## 🔧 Customization

### Environment Variables

You can customize the deployment by modifying the following files:

1. **Database Credentials** (`secret.yaml`):
   ```yaml
   data:
     postgres-username: <base64-encoded-username>
     postgres-password: <base64-encoded-password>
     postgres-database: <base64-encoded-database-name>
   ```

2. **Application Configuration** (`configmap.yaml`):
   ```yaml
   data:
     application.yml: |
       # Your custom configuration here
   ```

3. **Resource Limits** (`app-deployment.yaml`):
   ```yaml
   resources:
     requests:
       memory: "512Mi"
       cpu: "500m"
     limits:
       memory: "1Gi"
       cpu: "1000m"
   ```

### Ingress Configuration

To use the ingress, you need:

1. **Ingress Controller**: Install an ingress controller (e.g., nginx-ingress)
2. **Domain**: Update the host in `ingress.yaml`
3. **SSL**: Configure SSL certificates (optional)

## 📊 Monitoring and Logs

### View Logs

```bash
# View logs for all application pods
./k8s/deploy.sh logs all

# View logs for a specific pod
./k8s/deploy.sh logs <pod-name>
```

### Check Status

```bash
# Check deployment status
./k8s/deploy.sh status

# Check specific resources
kubectl get pods -n super-organizer
kubectl get services -n super-organizer
kubectl get hpa -n super-organizer
```

### Health Checks

The application includes health checks at:
- **Liveness**: `/actuator/health`
- **Readiness**: `/actuator/health`
- **Startup**: `/actuator/health`

## 🔒 Security Considerations

1. **Secrets**: Database credentials are stored in Kubernetes secrets
2. **Network Policies**: Consider implementing network policies for pod-to-pod communication
3. **RBAC**: Configure appropriate RBAC for production deployments
4. **SSL/TLS**: Enable HTTPS for production deployments

## 🧹 Cleanup

To remove the deployment:

```bash
./k8s/deploy.sh cleanup
```

This will delete the entire namespace and all associated resources.

## 🔧 Troubleshooting

### Common Issues

1. **Image Pull Errors**:
   - Ensure Docker image is built: `docker build -f k8s/Dockerfile -t super-organizer:1.0.0 .`
   - Check image exists: `docker images | grep super-organizer`

2. **Database Connection Issues**:
   - Check PostgreSQL pod status: `kubectl get pods -n super-organizer -l app=super-organizer-postgres`
   - Check database logs: `kubectl logs -f <postgres-pod-name> -n super-organizer`

3. **Application Startup Issues**:
   - Check application logs: `./k8s/deploy.sh logs all`
   - Verify ConfigMap: `kubectl describe configmap super-organizer-config -n super-organizer`

4. **Ingress Issues**:
   - Ensure ingress controller is installed
   - Check ingress status: `kubectl get ingress -n super-organizer`
   - Verify host configuration in `ingress.yaml`

### Debugging Commands

```bash
# Get detailed pod information
kubectl describe pod <pod-name> -n super-organizer

# Check events
kubectl get events -n super-organizer --sort-by='.lastTimestamp'

# Check resource usage
kubectl top pods -n super-organizer

# Access application shell
kubectl exec -it <pod-name> -n super-organizer -- /bin/bash
```

## 📚 Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Spring Boot Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [PostgreSQL on Kubernetes](https://kubernetes.io/docs/tutorials/stateful-application/postgresql/)
- [Ingress Controllers](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)

## 🤝 Contributing

When contributing to the Kubernetes configuration:

1. Test changes in a development environment
2. Update documentation for any configuration changes
3. Follow Kubernetes best practices
4. Consider security implications of changes

## 📄 License

This Kubernetes configuration is part of the Super Organizer API project and follows the same license terms.