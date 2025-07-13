# Quick Start Guide - My Organizer Database Integration

## 🚀 Get Started in 5 Minutes

### 1. Prerequisites Check
Make sure you have:
- Node.js (v14+) installed
- PostgreSQL (v12+) running
- A text editor or IDE

### 2. Setup Database
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE my_organizer_db;

# Exit PostgreSQL
\q
```

### 3. Install and Configure
```bash
# Install dependencies
npm install

# Set up environment
cp .env.example .env

# Edit .env with your database credentials
# At minimum, set: DB_PASSWORD and JWT_SECRET
```

### 4. Initialize Database
```bash
# Run migrations
npm run db:migrate

# Add sample data (optional)
npm run db:seed
```

### 5. Start the Application
```bash
# Development mode
npm run dev

# The API will be available at http://localhost:3000
```

## 🧪 Test the API

### Register a new user
```bash
curl -X POST http://localhost:3000/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123"
  }'
```

### Create a project (use token from login)
```bash
curl -X POST http://localhost:3000/api/v1/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Project",
    "description": "Testing the API",
    "priority": "high"
  }'
```

## 📖 What's Included

### Database Models
- **Users**: Authentication and profile management
- **Projects**: Hierarchical project organization
- **Tasks**: Detailed task management with dependencies
- **Notes**: Rich text notes with markdown support
- **Events**: Calendar events with reminders

### API Features
- JWT-based authentication
- Input validation and sanitization
- Rate limiting and security headers
- Comprehensive error handling
- Filtering and search capabilities
- Statistics and analytics

### Database Features
- PostgreSQL with Sequelize ORM
- Optimized indexes for performance
- JSONB fields for flexible metadata
- Foreign key relationships
- Automatic timestamps
- Data validation at model level

## 🔧 Available Commands

```bash
# Development
npm run dev              # Start with auto-reload
npm start               # Start production server

# Database
npm run db:migrate      # Run migrations
npm run db:migrate reset # Reset database
npm run db:seed         # Add sample data

# Testing
npm test               # Run tests
npm run test:watch     # Watch mode
npm run test:coverage  # Coverage report
```

## 🌐 API Endpoints Summary

### Users
- `POST /api/v1/users/register` - Register
- `POST /api/v1/users/login` - Login
- `GET /api/v1/users/profile` - Get profile
- `PUT /api/v1/users/profile` - Update profile
- `GET /api/v1/users/stats` - Get statistics

### Projects
- `POST /api/v1/projects` - Create project
- `GET /api/v1/projects` - List projects
- `GET /api/v1/projects/:id` - Get project
- `PUT /api/v1/projects/:id` - Update project
- `DELETE /api/v1/projects/:id` - Delete project

### Tasks
- `POST /api/v1/tasks` - Create task
- `GET /api/v1/tasks` - List tasks
- `GET /api/v1/tasks/:id` - Get task
- `PUT /api/v1/tasks/:id` - Update task
- `DELETE /api/v1/tasks/:id` - Delete task
- `GET /api/v1/tasks/overdue` - Get overdue tasks
- `GET /api/v1/tasks/upcoming` - Get upcoming tasks

## 🎯 Next Steps

1. **Explore the API**: Use the sample data or create your own
2. **Build a Frontend**: Connect a React/Vue.js frontend
3. **Add Features**: Extend with notifications, file uploads, etc.
4. **Deploy**: Use Docker or deploy to your preferred platform

## 📚 Documentation

- Full API documentation: `/api/v1/info`
- Health check: `/api/health`
- Complete README: `README.md`

## 🆘 Need Help?

- Check the comprehensive README.md
- Review the database schema diagrams
- Examine the sample data in `src/database/seed.js`
- Look at the model definitions in `src/models/`

Happy coding! 🚀