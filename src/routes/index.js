const express = require('express');
const userRoutes = require('./userRoutes');
const projectRoutes = require('./projectRoutes');
const taskRoutes = require('./taskRoutes');

const router = express.Router();

// API version prefix
const API_VERSION = process.env.API_VERSION || 'v1';

// Mount routes
router.use(`/${API_VERSION}/users`, userRoutes);
router.use(`/${API_VERSION}/projects`, projectRoutes);
router.use(`/${API_VERSION}/tasks`, taskRoutes);

// Health check endpoint
router.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'API is healthy',
    timestamp: new Date().toISOString(),
    version: API_VERSION
  });
});

// API info endpoint
router.get(`/${API_VERSION}/info`, (req, res) => {
  res.json({
    success: true,
    data: {
      name: 'My Organizer API',
      version: API_VERSION,
      description: 'Personal organizer application with PostgreSQL database integration',
      endpoints: {
        users: `/${API_VERSION}/users`,
        projects: `/${API_VERSION}/projects`,
        tasks: `/${API_VERSION}/tasks`
      }
    }
  });
});

module.exports = router;