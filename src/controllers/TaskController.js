const TaskService = require('../services/TaskService');
const { validationResult } = require('express-validator');

class TaskController {
  async createTask(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const taskData = {
        ...req.body,
        userId: req.user.userId
      };

      const task = await TaskService.createTask(taskData);
      
      res.status(201).json({
        success: true,
        message: 'Task created successfully',
        data: task
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getTasks(req, res) {
    try {
      const filters = {
        status: req.query.status,
        priority: req.query.priority,
        projectId: req.query.projectId,
        search: req.query.search,
        tags: req.query.tags ? req.query.tags.split(',') : null,
        dueDateFrom: req.query.dueDateFrom,
        dueDateTo: req.query.dueDateTo
      };

      const tasks = await TaskService.findUserTasks(req.user.userId, filters);
      
      res.json({
        success: true,
        data: tasks
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getTask(req, res) {
    try {
      const task = await TaskService.findTaskById(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        data: task
      });
    } catch (error) {
      res.status(404).json({
        success: false,
        message: error.message
      });
    }
  }

  async updateTask(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const task = await TaskService.updateTask(req.params.id, req.user.userId, req.body);
      
      res.json({
        success: true,
        message: 'Task updated successfully',
        data: task
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async deleteTask(req, res) {
    try {
      const result = await TaskService.deleteTask(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        message: result.message
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async updateTaskOrder(req, res) {
    try {
      const { taskOrders } = req.body;
      
      if (!Array.isArray(taskOrders)) {
        return res.status(400).json({
          success: false,
          message: 'Task orders must be an array'
        });
      }

      const result = await TaskService.updateTaskOrder(req.user.userId, taskOrders);
      
      res.json({
        success: true,
        message: result.message
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getTasksByProject(req, res) {
    try {
      const tasks = await TaskService.getTasksByProject(req.params.projectId, req.user.userId);
      
      res.json({
        success: true,
        data: tasks
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getOverdueTasks(req, res) {
    try {
      const tasks = await TaskService.getOverdueTasks(req.user.userId);
      
      res.json({
        success: true,
        data: tasks
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getUpcomingTasks(req, res) {
    try {
      const days = parseInt(req.query.days) || 7;
      const tasks = await TaskService.getUpcomingTasks(req.user.userId, days);
      
      res.json({
        success: true,
        data: tasks
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async duplicateTask(req, res) {
    try {
      const task = await TaskService.duplicateTask(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        message: 'Task duplicated successfully',
        data: task
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getTaskStats(req, res) {
    try {
      const projectId = req.query.projectId || null;
      const stats = await TaskService.getTaskStats(req.user.userId, projectId);
      
      res.json({
        success: true,
        data: stats
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }
}

module.exports = new TaskController();