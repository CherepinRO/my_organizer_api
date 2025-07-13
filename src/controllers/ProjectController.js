const ProjectService = require('../services/ProjectService');
const { validationResult } = require('express-validator');

class ProjectController {
  async createProject(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const projectData = {
        ...req.body,
        userId: req.user.userId
      };

      const project = await ProjectService.createProject(projectData);
      
      res.status(201).json({
        success: true,
        message: 'Project created successfully',
        data: project
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getProjects(req, res) {
    try {
      const filters = {
        status: req.query.status,
        priority: req.query.priority,
        search: req.query.search
      };

      const projects = await ProjectService.findUserProjects(req.user.userId, filters);
      
      res.json({
        success: true,
        data: projects
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async getProject(req, res) {
    try {
      const project = await ProjectService.findProjectById(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        data: project
      });
    } catch (error) {
      res.status(404).json({
        success: false,
        message: error.message
      });
    }
  }

  async updateProject(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const project = await ProjectService.updateProject(req.params.id, req.user.userId, req.body);
      
      res.json({
        success: true,
        message: 'Project updated successfully',
        data: project
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async deleteProject(req, res) {
    try {
      const result = await ProjectService.deleteProject(req.params.id, req.user.userId);
      
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

  async getProjectStats(req, res) {
    try {
      const stats = await ProjectService.getProjectStats(req.params.id, req.user.userId);
      
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

  async archiveProject(req, res) {
    try {
      const project = await ProjectService.archiveProject(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        message: 'Project archived successfully',
        data: project
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }

  async duplicateProject(req, res) {
    try {
      const project = await ProjectService.duplicateProject(req.params.id, req.user.userId);
      
      res.json({
        success: true,
        message: 'Project duplicated successfully',
        data: project
      });
    } catch (error) {
      res.status(400).json({
        success: false,
        message: error.message
      });
    }
  }
}

module.exports = new ProjectController();