const { sequelize } = require('../config/database');
const models = require('../models');

async function migrate() {
  try {
    console.log('🔄 Starting database migration...');
    
    // Test database connection
    await sequelize.authenticate();
    console.log('✅ Database connection established');
    
    // Sync all models
    await sequelize.sync({ force: false, alter: true });
    console.log('✅ Database schema synchronized');
    
    // Create indexes if they don't exist
    await createIndexes();
    console.log('✅ Database indexes created');
    
    console.log('🎉 Database migration completed successfully!');
  } catch (error) {
    console.error('❌ Database migration failed:', error);
    process.exit(1);
  }
}

async function createIndexes() {
  const queryInterface = sequelize.getQueryInterface();
  
  try {
    // Additional indexes for better performance
    
    // User indexes
    await queryInterface.addIndex('Users', ['email'], { unique: true, concurrently: true });
    await queryInterface.addIndex('Users', ['username'], { unique: true, concurrently: true });
    await queryInterface.addIndex('Users', ['isActive'], { concurrently: true });
    
    // Project indexes
    await queryInterface.addIndex('Projects', ['userId', 'status'], { concurrently: true });
    await queryInterface.addIndex('Projects', ['dueDate'], { concurrently: true });
    await queryInterface.addIndex('Projects', ['priority'], { concurrently: true });
    
    // Task indexes
    await queryInterface.addIndex('Tasks', ['userId', 'status'], { concurrently: true });
    await queryInterface.addIndex('Tasks', ['projectId'], { concurrently: true });
    await queryInterface.addIndex('Tasks', ['dueDate'], { concurrently: true });
    await queryInterface.addIndex('Tasks', ['priority'], { concurrently: true });
    await queryInterface.addIndex('Tasks', ['parentTaskId'], { concurrently: true });
    
    // Note indexes
    await queryInterface.addIndex('Notes', ['userId', 'isArchived'], { concurrently: true });
    await queryInterface.addIndex('Notes', ['projectId'], { concurrently: true });
    await queryInterface.addIndex('Notes', ['taskId'], { concurrently: true });
    await queryInterface.addIndex('Notes', ['type'], { concurrently: true });
    
    // Event indexes
    await queryInterface.addIndex('Events', ['userId', 'status'], { concurrently: true });
    await queryInterface.addIndex('Events', ['startDate', 'endDate'], { concurrently: true });
    await queryInterface.addIndex('Events', ['projectId'], { concurrently: true });
    await queryInterface.addIndex('Events', ['taskId'], { concurrently: true });
    
    console.log('✅ Additional indexes created successfully');
  } catch (error) {
    // Some indexes might already exist, which is fine
    console.log('ℹ️  Some indexes may already exist, continuing...');
  }
}

async function reset() {
  try {
    console.log('🔄 Resetting database...');
    
    // Drop all tables
    await sequelize.drop();
    console.log('✅ All tables dropped');
    
    // Recreate tables
    await sequelize.sync({ force: true });
    console.log('✅ Database schema recreated');
    
    // Create indexes
    await createIndexes();
    console.log('✅ Database indexes created');
    
    console.log('🎉 Database reset completed successfully!');
  } catch (error) {
    console.error('❌ Database reset failed:', error);
    process.exit(1);
  }
}

// Command line interface
const command = process.argv[2];

switch (command) {
  case 'reset':
    reset();
    break;
  case 'migrate':
  default:
    migrate();
    break;
}

module.exports = { migrate, reset };