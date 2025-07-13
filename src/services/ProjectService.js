const { Project, Task, Note, Event } = require('../models');
const { Op } = require('sequelize');

class ProjectService {
  async createProject(projectData) {
    try {
      const project = await Project.create(projectData);
      return project;
    } catch (error) {
      throw new Error(`Error creating project: ${error.message}`);
    }
  }

  async findProjectById(id, userId) {
    try {
      const project = await Project.findOne({
        where: { id, userId },
        include: [
          { association: 'owner' },
          { association: 'tasks', limit: 20 },
          { association: 'notes', limit: 10 },
          { association: 'events', limit: 10 },
          { association: 'subProjects' },
          { association: 'parentProject' }
        ]
      });

      if (!project) {
        throw new Error('Project not found');
      }

      return project;
    } catch (error) {
      throw new Error(`Error finding project: ${error.message}`);
    }
  }

  async findUserProjects(userId, filters = {}) {
    try {
      const where = { userId };
      
      if (filters.status) {
        where.status = filters.status;
      }

      if (filters.priority) {
        where.priority = filters.priority;
      }

      if (filters.search) {
        where[Op.or] = [
          { title: { [Op.iLike]: `%${filters.search}%` } },
          { description: { [Op.iLike]: `%${filters.search}%` } }
        ];
      }

      const projects = await Project.findAll({
        where,
        include: [
          { association: 'tasks', attributes: ['id', 'status'] },
          { association: 'subProjects', attributes: ['id', 'title', 'status'] }
        ],
        order: [['createdAt', 'DESC']]
      });

      return projects;
    } catch (error) {
      throw new Error(`Error finding user projects: ${error.message}`);
    }
  }

  async updateProject(id, userId, updateData) {
    try {
      const project = await Project.findOne({ where: { id, userId } });
      if (!project) {
        throw new Error('Project not found');
      }

      // Handle completion status
      if (updateData.status === 'completed' && !project.completedAt) {
        updateData.completedAt = new Date();
      } else if (updateData.status !== 'completed' && project.completedAt) {
        updateData.completedAt = null;
      }

      await project.update(updateData);
      return project;
    } catch (error) {
      throw new Error(`Error updating project: ${error.message}`);
    }
  }

  async deleteProject(id, userId) {
    try {
      const project = await Project.findOne({ where: { id, userId } });
      if (!project) {
        throw new Error('Project not found');
      }

      // Check if project has sub-projects
      const subProjects = await Project.count({ where: { parentProjectId: id } });
      if (subProjects > 0) {
        throw new Error('Cannot delete project with sub-projects');
      }

      await project.destroy();
      return { message: 'Project deleted successfully' };
    } catch (error) {
      throw new Error(`Error deleting project: ${error.message}`);
    }
  }

  async getProjectStats(id, userId) {
    try {
      const project = await Project.findOne({
        where: { id, userId },
        include: [
          { association: 'tasks' },
          { association: 'notes' },
          { association: 'events' },
          { association: 'subProjects' }
        ]
      });

      if (!project) {
        throw new Error('Project not found');
      }

      const tasks = project.tasks || [];
      const completedTasks = tasks.filter(task => task.status === 'completed');
      const pendingTasks = tasks.filter(task => task.status === 'todo');
      const inProgressTasks = tasks.filter(task => task.status === 'in_progress');

      return {
        totalTasks: tasks.length,
        completedTasks: completedTasks.length,
        pendingTasks: pendingTasks.length,
        inProgressTasks: inProgressTasks.length,
        totalNotes: project.notes?.length || 0,
        totalEvents: project.events?.length || 0,
        totalSubProjects: project.subProjects?.length || 0,
        completionPercentage: tasks.length > 0 ? Math.round((completedTasks.length / tasks.length) * 100) : 0
      };
    } catch (error) {
      throw new Error(`Error getting project stats: ${error.message}`);
    }
  }

  async archiveProject(id, userId) {
    try {
      const project = await Project.findOne({ where: { id, userId } });
      if (!project) {
        throw new Error('Project not found');
      }

      await project.update({ status: 'archived' });
      return project;
    } catch (error) {
      throw new Error(`Error archiving project: ${error.message}`);
    }
  }

  async duplicateProject(id, userId) {
    try {
      const originalProject = await Project.findOne({
        where: { id, userId },
        include: [
          { association: 'tasks' },
          { association: 'notes' }
        ]
      });

      if (!originalProject) {
        throw new Error('Project not found');
      }

      const projectData = {
        title: `${originalProject.title} (Copy)`,
        description: originalProject.description,
        color: originalProject.color,
        priority: originalProject.priority,
        userId: userId,
        settings: originalProject.settings
      };

      const newProject = await Project.create(projectData);

      // Duplicate tasks
      if (originalProject.tasks && originalProject.tasks.length > 0) {
        const tasksToCreate = originalProject.tasks.map(task => ({
          title: task.title,
          description: task.description,
          priority: task.priority,
          estimatedTime: task.estimatedTime,
          userId: userId,
          projectId: newProject.id,
          tags: task.tags,
          metadata: task.metadata
        }));

        await Task.bulkCreate(tasksToCreate);
      }

      // Duplicate notes
      if (originalProject.notes && originalProject.notes.length > 0) {
        const notesToCreate = originalProject.notes.map(note => ({
          title: note.title,
          content: note.content,
          type: note.type,
          format: note.format,
          userId: userId,
          projectId: newProject.id,
          tags: note.tags,
          metadata: note.metadata
        }));

        await Note.bulkCreate(notesToCreate);
      }

      return newProject;
    } catch (error) {
      throw new Error(`Error duplicating project: ${error.message}`);
    }
  }
}

module.exports = new ProjectService();