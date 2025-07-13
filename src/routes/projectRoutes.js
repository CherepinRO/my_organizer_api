const express = require('express');
const ProjectController = require('../controllers/ProjectController');
const { authenticateToken } = require('../middleware/auth');
const { projectValidation, commonValidation } = require('../middleware/validation');

const router = express.Router();

// All routes require authentication
router.use(authenticateToken);

// Project CRUD operations
router.post('/', projectValidation.create, ProjectController.createProject);
router.get('/', ProjectController.getProjects);
router.get('/:id', commonValidation.uuidParam, ProjectController.getProject);
router.put('/:id', commonValidation.uuidParam, projectValidation.update, ProjectController.updateProject);
router.delete('/:id', commonValidation.uuidParam, ProjectController.deleteProject);

// Additional project operations
router.get('/:id/stats', commonValidation.uuidParam, ProjectController.getProjectStats);
router.put('/:id/archive', commonValidation.uuidParam, ProjectController.archiveProject);
router.post('/:id/duplicate', commonValidation.uuidParam, ProjectController.duplicateProject);

module.exports = router;