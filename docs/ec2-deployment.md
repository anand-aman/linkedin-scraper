# EC2 Deployment with Docker Hub

This guide walks through deploying the LinkedIn Scraper application on AWS EC2 using pre-built Docker Hub images.

## Prerequisites

- **AWS Account** with EC2 access
- **EC2 Instance** (t3.micro or larger recommended)
  - OS: Ubuntu 22.04 LTS (or similar Linux distribution)
  - Security Group: Allow inbound traffic on ports 22 (SSH), 80 (HTTP), 8080 (API)
- **Docker Hub Account** with access to the published images (if private)

## Step 1: Launch EC2 Instance

1. Go to **AWS EC2 Console** → **Launch Instance**
2. Choose **Ubuntu 22.04 LTS** AMI
3. Select instance type: **t3.micro** or **t3.small** (free tier eligible or as per needs)
4. Configure Security Group:
   - SSH (port 22): Source = Your IP
   - HTTP (port 80): Source = 0.0.0.0/0
   - Custom TCP (port 8080): Source = 0.0.0.0/0
5. Launch and download the key pair (.pem file)

## Step 2: Connect to EC2

```bash
chmod 600 your-key-pair.pem
ssh -i your-key-pair.pem ubuntu@<EC2_PUBLIC_IP>
```

## Step 3: Install Docker and Docker Compose

Run these commands on the EC2 instance:

```bash
# Update packages
sudo apt-get update -y
sudo apt-get upgrade -y

# Install Docker and Docker Compose
sudo apt install -y docker.io docker-compose-v2

# Enable Docker service
sudo systemctl enable --now docker

# Add ubuntu user to docker group (avoid using sudo)
sudo usermod -aG docker ubuntu

# Apply group changes (logout and login, or run this)
newgrp docker
```

Verify installation:
```bash
docker --version
docker compose version
```

## Step 4: Clone Repository

```bash
cd /opt
sudo git clone https://github.com/anand-aman/linkedin-scraper.git
cd linkedin-scraper
sudo chown -R ubuntu:ubuntu .
```

## Step 5: Configure Environment Variables

Create `.env` file with your ZenRows API key and Docker Hub details:

```bash
cat > .env << 'EOF'
ZENROWS_API_KEY=your_zenrows_api_key_here
DOCKERHUB_USER=itsamananand
TAG=v1
EOF
```

**Variables explained:**
- `ZENROWS_API_KEY`: API key for LinkedIn scraping (required)
- `DOCKERHUB_USER`: Docker Hub username where images are published
- `TAG`: Docker image tag version (e.g., `v1`, `latest`)

## Step 6: Pull and Run Application

```bash
# Pull latest images from Docker Hub
docker compose -f docker-compose.ec2.yml pull

# Start services in background
docker compose -f docker-compose.ec2.yml up -d

# Verify services are running
docker compose -f docker-compose.ec2.yml ps
```

Expected output:
```
NAME                                    STATUS              PORTS
linkedin-scraper-backend-1             Up (healthy)        0.0.0.0:8080->8080/tcp
linkedin-scraper-frontend-1            Up                  0.0.0.0:80->80/tcp
```

## Step 7: Verify Deployment

Test the application:

```bash
# Test backend API
curl http://localhost:8080/api-docs

# Test frontend (from your browser)
http://<EC2_PUBLIC_IP>
```

Expected responses:
- **Frontend**: Loads the LinkedIn Scraper UI
- **Backend API**: Returns OpenAPI documentation

## Step 8: Monitor and Manage

### View logs
```bash
# All services
docker compose -f docker-compose.ec2.yml logs -f

# Specific service
docker compose -f docker-compose.ec2.yml logs -f backend
```

### Restart services
```bash
# Restart all
docker compose -f docker-compose.ec2.yml restart

# Restart specific service
docker compose -f docker-compose.ec2.yml restart backend
```

### Stop application
```bash
docker compose -f docker-compose.ec2.yml stop
```

### Stop and remove all containers
```bash
docker compose -f docker-compose.ec2.yml down
```

## Useful EC2 Commands

### Update running containers to new image version
```bash
# Update .env with new TAG
sed -i 's/TAG=v1/TAG=v2/' .env

# Pull new images and redeploy
docker compose -f docker-compose.ec2.yml pull
docker compose -f docker-compose.ec2.yml up -d
```

### Check resource usage
```bash
docker stats
```

### Inspect container details
```bash
docker ps
docker inspect <container_id>
docker logs <container_id>
```

## Troubleshooting

### Containers keep restarting
Check logs for errors:
```bash
docker compose -f docker-compose.ec2.yml logs backend
```

Common issues:
- **ZenRows API key invalid**: Verify `ZENROWS_API_KEY` in `.env`
- **Out of memory**: EC2 instance may be too small; increase instance type or Java heap settings in `docker-compose.ec2.yml`

### Connection refused on port 80 or 8080
- Verify Security Group allows inbound traffic on those ports
- Check if containers are running: `docker ps`

### Backend unhealthy
- Wait 40+ seconds after startup (service has startup period)
- Verify ZenRows API key is valid
- Check backend logs: `docker compose -f docker-compose.ec2.yml logs backend`

### Frontend can't reach backend
- Verify backend container is healthy
- Check `API_BASE_URL` environment variable in `docker-compose.ec2.yml`
- Ensure port 8080 is accessible (check Security Group)

## Cost Optimization

- **Use t3.micro** for minimal traffic (free tier eligible)
- **Stop instance when not in use** to save costs
- **Use Auto Scaling** for production with variable load
- **Enable CloudWatch monitoring** for performance tracking

## Production Recommendations

For production deployments:

1. **Use private Docker Hub images** and configure proper authentication
2. **Enable HTTPS** with Let's Encrypt (use Nginx reverse proxy)
3. **Database persistence**: Add volume mounts if needed
4. **Backup strategy**: Regularly backup configuration and data
5. **Monitoring**: Set up CloudWatch alarms for container health
6. **Load balancing**: Use AWS ELB for multiple EC2 instances
7. **Secrets management**: Use AWS Secrets Manager instead of `.env` file
8. **Log aggregation**: Stream logs to CloudWatch or ELK Stack

## Additional Resources

- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [LinkedIn Scraper API Reference](./api.md)
- [Architecture Overview](./architecture.md)
