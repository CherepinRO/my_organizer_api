const User = require('./User');
const Project = require('./Project');
const Task = require('./Task');
const Note = require('./Note');
const Event = require('./Event');

// Define associations
// User associations
User.hasMany(Project, { foreignKey: 'userId', as: 'projects' });
User.hasMany(Task, { foreignKey: 'userId', as: 'tasks' });
User.hasMany(Note, { foreignKey: 'userId', as: 'notes' });
User.hasMany(Event, { foreignKey: 'userId', as: 'events' });

// Project associations
Project.belongsTo(User, { foreignKey: 'userId', as: 'owner' });
Project.hasMany(Task, { foreignKey: 'projectId', as: 'tasks' });
Project.hasMany(Note, { foreignKey: 'projectId', as: 'notes' });
Project.hasMany(Event, { foreignKey: 'projectId', as: 'events' });
Project.hasMany(Project, { foreignKey: 'parentProjectId', as: 'subProjects' });
Project.belongsTo(Project, { foreignKey: 'parentProjectId', as: 'parentProject' });

// Task associations
Task.belongsTo(User, { foreignKey: 'userId', as: 'assignee' });
Task.belongsTo(Project, { foreignKey: 'projectId', as: 'project' });
Task.hasMany(Task, { foreignKey: 'parentTaskId', as: 'subTasks' });
Task.belongsTo(Task, { foreignKey: 'parentTaskId', as: 'parentTask' });
Task.hasMany(Note, { foreignKey: 'taskId', as: 'notes' });
Task.hasMany(Event, { foreignKey: 'taskId', as: 'events' });

// Note associations
Note.belongsTo(User, { foreignKey: 'userId', as: 'author' });
Note.belongsTo(Project, { foreignKey: 'projectId', as: 'project' });
Note.belongsTo(Task, { foreignKey: 'taskId', as: 'task' });

// Event associations
Event.belongsTo(User, { foreignKey: 'userId', as: 'organizer' });
Event.belongsTo(Project, { foreignKey: 'projectId', as: 'project' });
Event.belongsTo(Task, { foreignKey: 'taskId', as: 'task' });

module.exports = {
  User,
  Project,
  Task,
  Note,
  Event
};