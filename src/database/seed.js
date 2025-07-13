const { User, Project, Task, Note, Event } = require('../models');
const bcrypt = require('bcryptjs');

async function seedDatabase() {
  try {
    console.log('🌱 Starting database seeding...');
    
    // Create sample users
    const users = await createSampleUsers();
    console.log('✅ Sample users created');
    
    // Create sample projects
    const projects = await createSampleProjects(users);
    console.log('✅ Sample projects created');
    
    // Create sample tasks
    const tasks = await createSampleTasks(users, projects);
    console.log('✅ Sample tasks created');
    
    // Create sample notes
    await createSampleNotes(users, projects, tasks);
    console.log('✅ Sample notes created');
    
    // Create sample events
    await createSampleEvents(users, projects, tasks);
    console.log('✅ Sample events created');
    
    console.log('🎉 Database seeding completed successfully!');
  } catch (error) {
    console.error('❌ Database seeding failed:', error);
    process.exit(1);
  }
}

async function createSampleUsers() {
  const users = await User.bulkCreate([
    {
      username: 'johndoe',
      email: 'john@example.com',
      password: 'Password123',
      firstName: 'John',
      lastName: 'Doe',
      timezone: 'America/New_York',
      preferences: {
        theme: 'light',
        notifications: true,
        dateFormat: 'MM/DD/YYYY'
      }
    },
    {
      username: 'janesmith',
      email: 'jane@example.com',
      password: 'Password123',
      firstName: 'Jane',
      lastName: 'Smith',
      timezone: 'America/Los_Angeles',
      preferences: {
        theme: 'dark',
        notifications: false,
        dateFormat: 'DD/MM/YYYY'
      }
    }
  ], { returning: true });
  
  return users;
}

async function createSampleProjects(users) {
  const projects = await Project.bulkCreate([
    {
      title: 'Personal Website',
      description: 'Build a modern personal portfolio website',
      color: '#3498db',
      status: 'active',
      priority: 'high',
      startDate: new Date('2024-01-01'),
      dueDate: new Date('2024-03-01'),
      userId: users[0].id,
      settings: {
        allowSubProjects: true,
        defaultTaskPriority: 'medium'
      }
    },
    {
      title: 'Home Renovation',
      description: 'Renovate the kitchen and living room',
      color: '#e74c3c',
      status: 'active',
      priority: 'medium',
      startDate: new Date('2024-02-01'),
      dueDate: new Date('2024-06-01'),
      userId: users[0].id,
      settings: {
        allowSubProjects: false,
        defaultTaskPriority: 'low'
      }
    },
    {
      title: 'Mobile App Development',
      description: 'Develop a task management mobile application',
      color: '#9b59b6',
      status: 'active',
      priority: 'high',
      startDate: new Date('2024-01-15'),
      dueDate: new Date('2024-04-15'),
      userId: users[1].id,
      settings: {
        allowSubProjects: true,
        defaultTaskPriority: 'high'
      }
    }
  ], { returning: true });
  
  return projects;
}

async function createSampleTasks(users, projects) {
  const tasks = await Task.bulkCreate([
    {
      title: 'Design homepage mockup',
      description: 'Create a modern and responsive homepage design',
      status: 'completed',
      priority: 'high',
      dueDate: new Date('2024-01-15'),
      completedAt: new Date('2024-01-14'),
      estimatedTime: 480, // 8 hours
      actualTime: 540, // 9 hours
      userId: users[0].id,
      projectId: projects[0].id,
      order: 1,
      tags: ['design', 'ui/ux', 'frontend'],
      metadata: {
        tools: ['Figma', 'Photoshop'],
        difficulty: 'medium'
      }
    },
    {
      title: 'Set up development environment',
      description: 'Install Node.js, set up project structure, and configure tools',
      status: 'completed',
      priority: 'medium',
      dueDate: new Date('2024-01-10'),
      completedAt: new Date('2024-01-09'),
      estimatedTime: 120, // 2 hours
      actualTime: 90, // 1.5 hours
      userId: users[0].id,
      projectId: projects[0].id,
      order: 2,
      tags: ['setup', 'development', 'tools'],
      metadata: {
        tools: ['VS Code', 'Node.js', 'Git'],
        difficulty: 'easy'
      }
    },
    {
      title: 'Implement responsive layout',
      description: 'Code the responsive HTML/CSS layout based on the design',
      status: 'in_progress',
      priority: 'high',
      dueDate: new Date('2024-01-25'),
      estimatedTime: 360, // 6 hours
      userId: users[0].id,
      projectId: projects[0].id,
      order: 3,
      tags: ['frontend', 'css', 'responsive'],
      metadata: {
        tools: ['HTML', 'CSS', 'JavaScript'],
        difficulty: 'medium'
      }
    },
    {
      title: 'Get kitchen measurements',
      description: 'Measure kitchen dimensions for renovation planning',
      status: 'todo',
      priority: 'urgent',
      dueDate: new Date('2024-02-05'),
      estimatedTime: 60, // 1 hour
      userId: users[0].id,
      projectId: projects[1].id,
      order: 1,
      tags: ['planning', 'measurements'],
      metadata: {
        tools: ['Measuring tape', 'Notepad'],
        difficulty: 'easy'
      }
    },
    {
      title: 'Research UI frameworks',
      description: 'Compare React Native vs Flutter for mobile development',
      status: 'completed',
      priority: 'high',
      dueDate: new Date('2024-01-20'),
      completedAt: new Date('2024-01-18'),
      estimatedTime: 240, // 4 hours
      actualTime: 300, // 5 hours
      userId: users[1].id,
      projectId: projects[2].id,
      order: 1,
      tags: ['research', 'mobile', 'frameworks'],
      metadata: {
        tools: ['Internet', 'Documentation'],
        difficulty: 'medium'
      }
    }
  ], { returning: true });
  
  return tasks;
}

async function createSampleNotes(users, projects, tasks) {
  await Note.bulkCreate([
    {
      title: 'Design Inspiration',
      content: 'Collected some amazing portfolio websites for inspiration:\n\n1. https://example1.com\n2. https://example2.com\n3. https://example3.com',
      type: 'note',
      format: 'markdown',
      isFavorite: true,
      userId: users[0].id,
      projectId: projects[0].id,
      tags: ['inspiration', 'design', 'reference'],
      metadata: {
        source: 'web research',
        category: 'inspiration'
      }
    },
    {
      title: 'Color Palette',
      content: 'Main colors for the website:\n- Primary: #3498db\n- Secondary: #2ecc71\n- Accent: #e74c3c\n- Background: #f8f9fa',
      type: 'note',
      format: 'markdown',
      userId: users[0].id,
      projectId: projects[0].id,
      tags: ['design', 'colors', 'branding'],
      metadata: {
        category: 'design-system'
      }
    },
    {
      title: 'Kitchen Renovation Checklist',
      content: '- [ ] Get measurements\n- [ ] Research contractors\n- [ ] Get quotes\n- [ ] Choose materials\n- [ ] Schedule work',
      type: 'checklist',
      format: 'markdown',
      userId: users[0].id,
      projectId: projects[1].id,
      tags: ['checklist', 'planning'],
      metadata: {
        category: 'planning'
      }
    },
    {
      title: 'Framework Comparison',
      content: 'React Native vs Flutter comparison:\n\n**React Native:**\n- Pros: JavaScript, large community\n- Cons: Performance issues\n\n**Flutter:**\n- Pros: Fast performance, single codebase\n- Cons: Dart language learning curve',
      type: 'note',
      format: 'markdown',
      userId: users[1].id,
      projectId: projects[2].id,
      taskId: tasks[4].id,
      tags: ['research', 'comparison', 'frameworks'],
      metadata: {
        decision: 'pending',
        category: 'research'
      }
    }
  ]);
}

async function createSampleEvents(users, projects, tasks) {
  await Event.bulkCreate([
    {
      title: 'Design Review Meeting',
      description: 'Review homepage design with stakeholders',
      startDate: new Date('2024-01-16T10:00:00Z'),
      endDate: new Date('2024-01-16T11:00:00Z'),
      type: 'meeting',
      status: 'completed',
      priority: 'high',
      color: '#3498db',
      userId: users[0].id,
      projectId: projects[0].id,
      reminders: [
        { type: 'email', minutes: 60 },
        { type: 'notification', minutes: 15 }
      ],
      attendees: [
        { name: 'John Doe', email: 'john@example.com' },
        { name: 'Client', email: 'client@example.com' }
      ],
      metadata: {
        meetingType: 'review',
        location: 'online'
      }
    },
    {
      title: 'Work on responsive layout',
      description: 'Dedicated time block for implementing responsive design',
      startDate: new Date('2024-01-20T09:00:00Z'),
      endDate: new Date('2024-01-20T15:00:00Z'),
      type: 'personal',
      status: 'scheduled',
      priority: 'medium',
      color: '#2ecc71',
      userId: users[0].id,
      projectId: projects[0].id,
      taskId: tasks[2].id,
      reminders: [
        { type: 'notification', minutes: 30 }
      ],
      metadata: {
        workType: 'focused',
        breakSchedule: 'every 2 hours'
      }
    },
    {
      title: 'Contractor Meeting',
      description: 'Meet with potential contractors for kitchen renovation',
      startDate: new Date('2024-02-08T14:00:00Z'),
      endDate: new Date('2024-02-08T15:30:00Z'),
      type: 'appointment',
      status: 'scheduled',
      priority: 'high',
      color: '#e74c3c',
      location: 'Home',
      userId: users[0].id,
      projectId: projects[1].id,
      reminders: [
        { type: 'notification', minutes: 120 },
        { type: 'notification', minutes: 30 }
      ],
      attendees: [
        { name: 'John Doe', email: 'john@example.com' },
        { name: 'Contractor', email: 'contractor@example.com' }
      ],
      metadata: {
        appointmentType: 'consultation',
        preparation: 'prepare questions list'
      }
    }
  ]);
}

// Command line interface
if (require.main === module) {
  seedDatabase();
}

module.exports = { seedDatabase };