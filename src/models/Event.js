const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

const Event = sequelize.define('Event', {
  id: {
    type: DataTypes.UUID,
    defaultValue: DataTypes.UUIDV4,
    primaryKey: true
  },
  title: {
    type: DataTypes.STRING(200),
    allowNull: false,
    validate: {
      len: [1, 200]
    }
  },
  description: {
    type: DataTypes.TEXT,
    allowNull: true
  },
  startDate: {
    type: DataTypes.DATE,
    allowNull: false
  },
  endDate: {
    type: DataTypes.DATE,
    allowNull: false
  },
  isAllDay: {
    type: DataTypes.BOOLEAN,
    defaultValue: false
  },
  location: {
    type: DataTypes.STRING(255),
    allowNull: true
  },
  type: {
    type: DataTypes.ENUM('appointment', 'meeting', 'reminder', 'deadline', 'personal'),
    defaultValue: 'appointment'
  },
  status: {
    type: DataTypes.ENUM('scheduled', 'completed', 'cancelled', 'postponed'),
    defaultValue: 'scheduled'
  },
  priority: {
    type: DataTypes.ENUM('low', 'medium', 'high'),
    defaultValue: 'medium'
  },
  color: {
    type: DataTypes.STRING(7),
    defaultValue: '#3498db',
    validate: {
      is: /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/
    }
  },
  userId: {
    type: DataTypes.UUID,
    allowNull: false,
    references: {
      model: 'Users',
      key: 'id'
    }
  },
  projectId: {
    type: DataTypes.UUID,
    allowNull: true,
    references: {
      model: 'Projects',
      key: 'id'
    }
  },
  taskId: {
    type: DataTypes.UUID,
    allowNull: true,
    references: {
      model: 'Tasks',
      key: 'id'
    }
  },
  recurrence: {
    type: DataTypes.JSONB,
    allowNull: true
  },
  reminders: {
    type: DataTypes.JSONB,
    defaultValue: []
  },
  attendees: {
    type: DataTypes.JSONB,
    defaultValue: []
  },
  metadata: {
    type: DataTypes.JSONB,
    defaultValue: {}
  }
}, {
  timestamps: true,
  indexes: [
    {
      fields: ['userId', 'status']
    },
    {
      fields: ['startDate', 'endDate']
    },
    {
      fields: ['projectId']
    },
    {
      fields: ['taskId']
    },
    {
      fields: ['type']
    }
  ]
});

module.exports = Event;