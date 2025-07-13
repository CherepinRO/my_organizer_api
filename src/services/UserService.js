const { User } = require('../models');
const { Op } = require('sequelize');
const jwt = require('jsonwebtoken');

class UserService {
  async createUser(userData) {
    try {
      const existingUser = await User.findOne({
        where: {
          [Op.or]: [
            { email: userData.email },
            { username: userData.username }
          ]
        }
      });

      if (existingUser) {
        throw new Error('User with this email or username already exists');
      }

      const user = await User.create(userData);
      return user;
    } catch (error) {
      throw new Error(`Error creating user: ${error.message}`);
    }
  }

  async findUserById(id) {
    try {
      const user = await User.findByPk(id, {
        include: [
          { association: 'projects', limit: 5 },
          { association: 'tasks', limit: 10 },
          { association: 'notes', limit: 5 },
          { association: 'events', limit: 5 }
        ]
      });

      if (!user) {
        throw new Error('User not found');
      }

      return user;
    } catch (error) {
      throw new Error(`Error finding user: ${error.message}`);
    }
  }

  async findUserByEmail(email) {
    try {
      const user = await User.findOne({ where: { email } });
      return user;
    } catch (error) {
      throw new Error(`Error finding user by email: ${error.message}`);
    }
  }

  async findUserByUsername(username) {
    try {
      const user = await User.findOne({ where: { username } });
      return user;
    } catch (error) {
      throw new Error(`Error finding user by username: ${error.message}`);
    }
  }

  async updateUser(id, updateData) {
    try {
      const user = await User.findByPk(id);
      if (!user) {
        throw new Error('User not found');
      }

      await user.update(updateData);
      return user;
    } catch (error) {
      throw new Error(`Error updating user: ${error.message}`);
    }
  }

  async deleteUser(id) {
    try {
      const user = await User.findByPk(id);
      if (!user) {
        throw new Error('User not found');
      }

      await user.destroy();
      return { message: 'User deleted successfully' };
    } catch (error) {
      throw new Error(`Error deleting user: ${error.message}`);
    }
  }

  async authenticateUser(loginData) {
    try {
      const { email, password } = loginData;
      const user = await User.findOne({ where: { email } });

      if (!user || !await user.validatePassword(password)) {
        throw new Error('Invalid credentials');
      }

      if (!user.isActive) {
        throw new Error('Account is deactivated');
      }

      // Update last login
      await user.update({ lastLogin: new Date() });

      // Generate JWT token
      const token = jwt.sign(
        { userId: user.id, email: user.email },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN }
      );

      return { user, token };
    } catch (error) {
      throw new Error(`Authentication failed: ${error.message}`);
    }
  }

  async updateUserPreferences(id, preferences) {
    try {
      const user = await User.findByPk(id);
      if (!user) {
        throw new Error('User not found');
      }

      await user.update({ preferences });
      return user;
    } catch (error) {
      throw new Error(`Error updating preferences: ${error.message}`);
    }
  }

  async getUserStats(id) {
    try {
      const user = await User.findByPk(id, {
        include: [
          { association: 'projects' },
          { association: 'tasks' },
          { association: 'notes' },
          { association: 'events' }
        ]
      });

      if (!user) {
        throw new Error('User not found');
      }

      return {
        totalProjects: user.projects.length,
        totalTasks: user.tasks.length,
        totalNotes: user.notes.length,
        totalEvents: user.events.length,
        completedTasks: user.tasks.filter(task => task.status === 'completed').length,
        pendingTasks: user.tasks.filter(task => task.status === 'todo').length,
        inProgressTasks: user.tasks.filter(task => task.status === 'in_progress').length
      };
    } catch (error) {
      throw new Error(`Error getting user stats: ${error.message}`);
    }
  }
}

module.exports = new UserService();