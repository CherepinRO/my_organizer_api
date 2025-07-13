# My Organizer Application - Database Integration

A comprehensive Node.js application with PostgreSQL database integration for personal organization and task management.

## 🚀 Features

- **User Management**: Registration, authentication, and profile management
- **Project Organization**: Create, manage, and track projects with hierarchical structure
- **Task Management**: Full CRUD operations for tasks with priorities, due dates, and statuses
- **Note Taking**: Rich text notes with markdown support and attachments
- **Event Scheduling**: Calendar events with reminders and recurrence
- **Advanced Search**: Filter and search across all entities
- **Statistics**: Comprehensive analytics and reporting
- **RESTful API**: Clean, well-documented API endpoints

## 📋 Prerequisites

- Node.js (v14 or higher)
- PostgreSQL (v12 or higher)
- npm or yarn

## 🛠️ Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd my-organizer-app
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Set up environment variables**
   ```bash
   cp .env.example .env
   ```
   
   Edit `.env` with your database credentials and configuration:
   ```env
   # Database Configuration
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=my_organizer_db
   DB_USER=postgres
   DB_PASSWORD=your_password
   DB_SSL=false

   # Application Configuration
   NODE_ENV=development
   PORT=3000
   API_VERSION=v1

   # JWT Configuration
   JWT_SECRET=your_jwt_secret_key_here
   JWT_EXPIRES_IN=24h
   ```

4. **Create PostgreSQL database**
   ```sql
   CREATE DATABASE my_organizer_db;
   ```

5. **Run database migrations**
   ```bash
   npm run db:migrate
   ```

6. **Seed the database (optional)**
   ```bash
   npm run db:seed
   ```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
npm run dev
```

### Production Mode
```bash
npm start
```

The API will be available at `http://localhost:3000`

## 📚 API Documentation

### Base URL
```
http://localhost:3000/api/v1
```

### Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

### API Endpoints

#### Users
- `POST /users/register` - Register a new user
- `POST /users/login` - Login user
- `GET /users/profile` - Get user profile
- `PUT /users/profile` - Update user profile
- `PUT /users/password` - Change password
- `GET /users/stats` - Get user statistics
- `DELETE /users/account` - Delete user account

#### Projects
- `POST /projects` - Create a new project
- `GET /projects` - Get all user projects
- `GET /projects/:id` - Get project by ID
- `PUT /projects/:id` - Update project
- `DELETE /projects/:id` - Delete project
- `GET /projects/:id/stats` - Get project statistics
- `PUT /projects/:id/archive` - Archive project
- `POST /projects/:id/duplicate` - Duplicate project

#### Tasks
- `POST /tasks` - Create a new task
- `GET /tasks` - Get all user tasks
- `GET /tasks/:id` - Get task by ID
- `PUT /tasks/:id` - Update task
- `DELETE /tasks/:id` - Delete task
- `PUT /tasks/reorder` - Reorder tasks
- `GET /tasks/project/:projectId` - Get tasks by project
- `GET /tasks/overdue` - Get overdue tasks
- `GET /tasks/upcoming` - Get upcoming tasks
- `POST /tasks/:id/duplicate` - Duplicate task
- `GET /tasks/stats` - Get task statistics

### Example API Requests

#### Register a new user
```bash
curl -X POST http://localhost:3000/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "Password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

#### Login
```bash
curl -X POST http://localhost:3000/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "Password123"
  }'
```

#### Create a project
```bash
curl -X POST http://localhost:3000/api/v1/projects \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My New Project",
    "description": "A sample project",
    "priority": "high",
    "color": "#3498db"
  }'
```

#### Create a task
```bash
curl -X POST http://localhost:3000/api/v1/tasks \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete API documentation",
    "description": "Write comprehensive API documentation",
    "priority": "medium",
    "dueDate": "2024-02-01T12:00:00Z",
    "projectId": "<project_id>",
    "tags": ["documentation", "api"]
  }'
```

## 🗄️ Database Schema

### Core Tables

#### Users
- `id` (UUID, Primary Key)
- `username` (String, Unique)
- `email` (String, Unique)
- `password` (String, Hashed)
- `firstName` (String)
- `lastName` (String)
- `avatar` (String, URL)
- `isActive` (Boolean)
- `lastLogin` (DateTime)
- `timezone` (String)
- `preferences` (JSONB)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### Projects
- `id` (UUID, Primary Key)
- `title` (String)
- `description` (Text)
- `color` (String, Hex Color)
- `status` (Enum: active, archived, completed)
- `priority` (Enum: low, medium, high)
- `startDate` (DateTime)
- `dueDate` (DateTime)
- `completedAt` (DateTime)
- `userId` (UUID, Foreign Key)
- `parentProjectId` (UUID, Foreign Key, Optional)
- `settings` (JSONB)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### Tasks
- `id` (UUID, Primary Key)
- `title` (String)
- `description` (Text)
- `status` (Enum: todo, in_progress, completed, cancelled)
- `priority` (Enum: low, medium, high, urgent)
- `dueDate` (DateTime)
- `completedAt` (DateTime)
- `estimatedTime` (Integer, Minutes)
- `actualTime` (Integer, Minutes)
- `userId` (UUID, Foreign Key)
- `projectId` (UUID, Foreign Key, Optional)
- `parentTaskId` (UUID, Foreign Key, Optional)
- `order` (Integer)
- `tags` (Array of Strings)
- `metadata` (JSONB)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### Notes
- `id` (UUID, Primary Key)
- `title` (String)
- `content` (Text)
- `type` (Enum: note, checklist, bookmark, document)
- `format` (Enum: plain, markdown, html)
- `isArchived` (Boolean)
- `isFavorite` (Boolean)
- `userId` (UUID, Foreign Key)
- `projectId` (UUID, Foreign Key, Optional)
- `taskId` (UUID, Foreign Key, Optional)
- `tags` (Array of Strings)
- `attachments` (JSONB)
- `metadata` (JSONB)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### Events
- `id` (UUID, Primary Key)
- `title` (String)
- `description` (Text)
- `startDate` (DateTime)
- `endDate` (DateTime)
- `isAllDay` (Boolean)
- `location` (String)
- `type` (Enum: appointment, meeting, reminder, deadline, personal)
- `status` (Enum: scheduled, completed, cancelled, postponed)
- `priority` (Enum: low, medium, high)
- `color` (String, Hex Color)
- `userId` (UUID, Foreign Key)
- `projectId` (UUID, Foreign Key, Optional)
- `taskId` (UUID, Foreign Key, Optional)
- `recurrence` (JSONB)
- `reminders` (JSONB)
- `attendees` (JSONB)
- `metadata` (JSONB)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | `localhost` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `my_organizer_db` |
| `DB_USER` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | - |
| `DB_SSL` | Enable SSL for database | `false` |
| `NODE_ENV` | Environment mode | `development` |
| `PORT` | Server port | `3000` |
| `API_VERSION` | API version | `v1` |
| `JWT_SECRET` | JWT secret key | - |
| `JWT_EXPIRES_IN` | JWT expiration time | `24h` |
| `CORS_ORIGIN` | CORS origin | `http://localhost:3000` |

### Database Commands

```bash
# Run migrations
npm run db:migrate

# Reset database (drops and recreates all tables)
npm run db:migrate reset

# Seed with sample data
npm run db:seed
```

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

## 📦 Project Structure

```
src/
├── config/
│   └── database.js          # Database configuration
├── models/
│   ├── User.js              # User model
│   ├── Project.js           # Project model
│   ├── Task.js              # Task model
│   ├── Note.js              # Note model
│   ├── Event.js             # Event model
│   └── index.js             # Model relationships
├── services/
│   ├── UserService.js       # User business logic
│   ├── ProjectService.js    # Project business logic
│   └── TaskService.js       # Task business logic
├── controllers/
│   ├── UserController.js    # User HTTP handlers
│   ├── ProjectController.js # Project HTTP handlers
│   └── TaskController.js    # Task HTTP handlers
├── middleware/
│   ├── auth.js              # Authentication middleware
│   └── validation.js        # Validation middleware
├── routes/
│   ├── userRoutes.js        # User routes
│   ├── projectRoutes.js     # Project routes
│   ├── taskRoutes.js        # Task routes
│   └── index.js             # Route aggregator
├── database/
│   ├── migrate.js           # Database migration script
│   └── seed.js              # Database seeding script
└── server.js                # Main server file
```

## 🔐 Security Features

- JWT-based authentication
- Password hashing with bcrypt
- Input validation and sanitization
- Rate limiting
- CORS protection
- Security headers with Helmet
- SQL injection prevention with Sequelize ORM

## 🚀 Deployment

### Docker Deployment

1. **Build the image**
   ```bash
   docker build -t my-organizer-app .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

### Manual Deployment

1. **Set production environment variables**
2. **Build the application**
   ```bash
   npm run build
   ```
3. **Start the application**
   ```bash
   npm start
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For questions and support, please open an issue in the GitHub repository.

## 📝 Changelog

### v1.0.0
- Initial release with core functionality
- User authentication and authorization
- Project and task management
- Note taking and event scheduling
- RESTful API with comprehensive documentation