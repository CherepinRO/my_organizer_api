const express = require('express');
const TaskController = require('../controllers/TaskController');
const { authenticateToken } = require('../middleware/auth');
const { taskValidation, commonValidation } = require('../middleware/validation');

const router = express.Router();

// All routes require authentication
router.use(authenticateToken);

// Task CRUD operations
router.post('/', taskValidation.create, TaskController.createTask);
router.get('/', TaskController.getTasks);
router.get('/:id', commonValidation.uuidParam, TaskController.getTask);
router.put('/:id', commonValidation.uuidParam, taskValidation.update, TaskController.updateTask);
router.delete('/:id', commonValidation.uuidParam, TaskController.deleteTask);

// Additional task operations
router.put('/reorder', TaskController.updateTaskOrder);
router.get('/project/:projectId', commonValidation.uuidParam, TaskController.getTasksByProject);
router.get('/overdue', TaskController.getOverdueTasks);
router.get('/upcoming', TaskController.getUpcomingTasks);
router.post('/:id/duplicate', commonValidation.uuidParam, TaskController.duplicateTask);
router.get('/stats', TaskController.getTaskStats);

module.exports = router;