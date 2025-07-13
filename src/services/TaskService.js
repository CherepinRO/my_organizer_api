const { Task, Project, Note, Event } = require('../models');
const { Op } = require('sequelize');

class TaskService {
  async createTask(taskData) {
    try {
      // Validate project ownership if projectId is provided
      if (taskData.projectId) {
        const project = await Project.findOne({
          where: { id: taskData.projectId, userId: taskData.userId }
        });
        if (!project) {
          throw new Error('Project not found or access denied');
        }
      }

      const task = await Task.create(taskData);
      return task;
    } catch (error) {
      throw new Error(`Error creating task: ${error.message}`);
    }
  }

  async findTaskById(id, userId) {
    try {
      const task = await Task.findOne({
        where: { id, userId },
        include: [
          { association: 'assignee' },
          { association: 'project' },
          { association: 'parentTask' },
          { association: 'subTasks' },
          { association: 'notes' },
          { association: 'events' }
        ]
      });

      if (!task) {
        throw new Error('Task not found');
      }

      return task;
    } catch (error) {
      throw new Error(`Error finding task: ${error.message}`);
    }
  }

  async findUserTasks(userId, filters = {}) {
    try {
      const where = { userId };
      
      if (filters.status) {
        where.status = filters.status;
      }

      if (filters.priority) {
        where.priority = filters.priority;
      }

      if (filters.projectId) {
        where.projectId = filters.projectId;
      }

      if (filters.search) {
        where[Op.or] = [
          { title: { [Op.iLike]: `%${filters.search}%` } },
          { description: { [Op.iLike]: `%${filters.search}%` } }
        ];
      }

      if (filters.tags && filters.tags.length > 0) {
        where.tags = { [Op.overlap]: filters.tags };
      }

      if (filters.dueDateFrom || filters.dueDateTo) {
        where.dueDate = {};
        if (filters.dueDateFrom) {
          where.dueDate[Op.gte] = filters.dueDateFrom;
        }
        if (filters.dueDateTo) {
          where.dueDate[Op.lte] = filters.dueDateTo;
        }
      }

      const tasks = await Task.findAll({
        where,
        include: [
          { association: 'project', attributes: ['id', 'title', 'color'] },
          { association: 'parentTask', attributes: ['id', 'title'] },
          { association: 'subTasks', attributes: ['id', 'title', 'status'] }
        ],
        order: [
          ['order', 'ASC'],
          ['createdAt', 'DESC']
        ]
      });

      return tasks;
    } catch (error) {
      throw new Error(`Error finding user tasks: ${error.message}`);
    }
  }

  async updateTask(id, userId, updateData) {
    try {
      const task = await Task.findOne({ where: { id, userId } });
      if (!task) {
        throw new Error('Task not found');
      }

      // Handle completion status
      if (updateData.status === 'completed' && task.status !== 'completed') {
        updateData.completedAt = new Date();
      } else if (updateData.status !== 'completed' && task.status === 'completed') {
        updateData.completedAt = null;
      }

      await task.update(updateData);
      return task;
    } catch (error) {
      throw new Error(`Error updating task: ${error.message}`);
    }
  }

  async deleteTask(id, userId) {
    try {
      const task = await Task.findOne({ where: { id, userId } });
      if (!task) {
        throw new Error('Task not found');
      }

      // Check if task has sub-tasks
      const subTasks = await Task.count({ where: { parentTaskId: id } });
      if (subTasks > 0) {
        throw new Error('Cannot delete task with sub-tasks');
      }

      await task.destroy();
      return { message: 'Task deleted successfully' };
    } catch (error) {
      throw new Error(`Error deleting task: ${error.message}`);
    }
  }

  async updateTaskOrder(userId, taskOrders) {
    try {
      const updatePromises = taskOrders.map(({ id, order }) => 
        Task.update({ order }, { where: { id, userId } })
      );

      await Promise.all(updatePromises);
      return { message: 'Task order updated successfully' };
    } catch (error) {
      throw new Error(`Error updating task order: ${error.message}`);
    }
  }

  async getTasksByProject(projectId, userId) {
    try {
      const project = await Project.findOne({
        where: { id: projectId, userId }
      });

      if (!project) {
        throw new Error('Project not found');
      }

      const tasks = await Task.findAll({
        where: { projectId, userId },
        include: [
          { association: 'subTasks' },
          { association: 'parentTask', attributes: ['id', 'title'] }
        ],
        order: [
          ['order', 'ASC'],
          ['createdAt', 'DESC']
        ]
      });

      return tasks;
    } catch (error) {
      throw new Error(`Error getting tasks by project: ${error.message}`);
    }
  }

  async getOverdueTasks(userId) {
    try {
      const tasks = await Task.findAll({
        where: {
          userId,
          dueDate: { [Op.lt]: new Date() },
          status: { [Op.not]: 'completed' }
        },
        include: [
          { association: 'project', attributes: ['id', 'title', 'color'] }
        ],
        order: [['dueDate', 'ASC']]
      });

      return tasks;
    } catch (error) {
      throw new Error(`Error getting overdue tasks: ${error.message}`);
    }
  }

  async getUpcomingTasks(userId, days = 7) {
    try {
      const today = new Date();
      const futureDate = new Date(today.getTime() + (days * 24 * 60 * 60 * 1000));

      const tasks = await Task.findAll({
        where: {
          userId,
          dueDate: {
            [Op.gte]: today,
            [Op.lte]: futureDate
          },
          status: { [Op.not]: 'completed' }
        },
        include: [
          { association: 'project', attributes: ['id', 'title', 'color'] }
        ],
        order: [['dueDate', 'ASC']]
      });

      return tasks;
    } catch (error) {
      throw new Error(`Error getting upcoming tasks: ${error.message}`);
    }
  }

  async duplicateTask(id, userId) {
    try {
      const originalTask = await Task.findOne({ where: { id, userId } });
      if (!originalTask) {
        throw new Error('Task not found');
      }

      const taskData = {
        title: `${originalTask.title} (Copy)`,
        description: originalTask.description,
        priority: originalTask.priority,
        estimatedTime: originalTask.estimatedTime,
        userId: userId,
        projectId: originalTask.projectId,
        tags: originalTask.tags,
        metadata: originalTask.metadata
      };

      const newTask = await Task.create(taskData);
      return newTask;
    } catch (error) {
      throw new Error(`Error duplicating task: ${error.message}`);
    }
  }

  async getTaskStats(userId, projectId = null) {
    try {
      const where = { userId };
      if (projectId) {
        where.projectId = projectId;
      }

      const tasks = await Task.findAll({ where });

      const stats = {
        total: tasks.length,
        todo: tasks.filter(t => t.status === 'todo').length,
        inProgress: tasks.filter(t => t.status === 'in_progress').length,
        completed: tasks.filter(t => t.status === 'completed').length,
        cancelled: tasks.filter(t => t.status === 'cancelled').length,
        overdue: tasks.filter(t => t.dueDate && t.dueDate < new Date() && t.status !== 'completed').length,
        highPriority: tasks.filter(t => t.priority === 'high' || t.priority === 'urgent').length
      };

      return stats;
    } catch (error) {
      throw new Error(`Error getting task stats: ${error.message}`);
    }
  }
}

module.exports = new TaskService();