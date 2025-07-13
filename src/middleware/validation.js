const { body, param, query } = require('express-validator');

const userValidation = {
  register: [
    body('username')
      .trim()
      .isLength({ min: 3, max: 50 })
      .withMessage('Username must be between 3 and 50 characters')
      .isAlphanumeric()
      .withMessage('Username must contain only letters and numbers'),
    
    body('email')
      .trim()
      .isEmail()
      .withMessage('Please provide a valid email address')
      .normalizeEmail(),
    
    body('password')
      .isLength({ min: 6 })
      .withMessage('Password must be at least 6 characters long')
      .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/)
      .withMessage('Password must contain at least one uppercase letter, one lowercase letter, and one number'),
    
    body('firstName')
      .trim()
      .isLength({ min: 1, max: 50 })
      .withMessage('First name is required and must be less than 50 characters'),
    
    body('lastName')
      .trim()
      .isLength({ min: 1, max: 50 })
      .withMessage('Last name is required and must be less than 50 characters'),
    
    body('timezone')
      .optional()
      .isString()
      .withMessage('Timezone must be a string')
  ],

  login: [
    body('email')
      .trim()
      .isEmail()
      .withMessage('Please provide a valid email address')
      .normalizeEmail(),
    
    body('password')
      .notEmpty()
      .withMessage('Password is required')
  ],

  updateProfile: [
    body('firstName')
      .optional()
      .trim()
      .isLength({ min: 1, max: 50 })
      .withMessage('First name must be less than 50 characters'),
    
    body('lastName')
      .optional()
      .trim()
      .isLength({ min: 1, max: 50 })
      .withMessage('Last name must be less than 50 characters'),
    
    body('timezone')
      .optional()
      .isString()
      .withMessage('Timezone must be a string'),
    
    body('avatar')
      .optional()
      .isURL()
      .withMessage('Avatar must be a valid URL')
  ],

  changePassword: [
    body('currentPassword')
      .notEmpty()
      .withMessage('Current password is required'),
    
    body('newPassword')
      .isLength({ min: 6 })
      .withMessage('New password must be at least 6 characters long')
      .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/)
      .withMessage('New password must contain at least one uppercase letter, one lowercase letter, and one number')
  ]
};

const projectValidation = {
  create: [
    body('title')
      .trim()
      .isLength({ min: 1, max: 100 })
      .withMessage('Project title is required and must be less than 100 characters'),
    
    body('description')
      .optional()
      .trim()
      .isLength({ max: 1000 })
      .withMessage('Description must be less than 1000 characters'),
    
    body('color')
      .optional()
      .matches(/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/)
      .withMessage('Color must be a valid hex color'),
    
    body('status')
      .optional()
      .isIn(['active', 'archived', 'completed'])
      .withMessage('Status must be one of: active, archived, completed'),
    
    body('priority')
      .optional()
      .isIn(['low', 'medium', 'high'])
      .withMessage('Priority must be one of: low, medium, high'),
    
    body('startDate')
      .optional()
      .isISO8601()
      .withMessage('Start date must be a valid date'),
    
    body('dueDate')
      .optional()
      .isISO8601()
      .withMessage('Due date must be a valid date'),
    
    body('parentProjectId')
      .optional()
      .isUUID()
      .withMessage('Parent project ID must be a valid UUID')
  ],

  update: [
    body('title')
      .optional()
      .trim()
      .isLength({ min: 1, max: 100 })
      .withMessage('Project title must be less than 100 characters'),
    
    body('description')
      .optional()
      .trim()
      .isLength({ max: 1000 })
      .withMessage('Description must be less than 1000 characters'),
    
    body('color')
      .optional()
      .matches(/^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/)
      .withMessage('Color must be a valid hex color'),
    
    body('status')
      .optional()
      .isIn(['active', 'archived', 'completed'])
      .withMessage('Status must be one of: active, archived, completed'),
    
    body('priority')
      .optional()
      .isIn(['low', 'medium', 'high'])
      .withMessage('Priority must be one of: low, medium, high'),
    
    body('startDate')
      .optional()
      .isISO8601()
      .withMessage('Start date must be a valid date'),
    
    body('dueDate')
      .optional()
      .isISO8601()
      .withMessage('Due date must be a valid date')
  ]
};

const taskValidation = {
  create: [
    body('title')
      .trim()
      .isLength({ min: 1, max: 200 })
      .withMessage('Task title is required and must be less than 200 characters'),
    
    body('description')
      .optional()
      .trim()
      .isLength({ max: 1000 })
      .withMessage('Description must be less than 1000 characters'),
    
    body('status')
      .optional()
      .isIn(['todo', 'in_progress', 'completed', 'cancelled'])
      .withMessage('Status must be one of: todo, in_progress, completed, cancelled'),
    
    body('priority')
      .optional()
      .isIn(['low', 'medium', 'high', 'urgent'])
      .withMessage('Priority must be one of: low, medium, high, urgent'),
    
    body('dueDate')
      .optional()
      .isISO8601()
      .withMessage('Due date must be a valid date'),
    
    body('estimatedTime')
      .optional()
      .isInt({ min: 1 })
      .withMessage('Estimated time must be a positive integer (minutes)'),
    
    body('projectId')
      .optional()
      .isUUID()
      .withMessage('Project ID must be a valid UUID'),
    
    body('parentTaskId')
      .optional()
      .isUUID()
      .withMessage('Parent task ID must be a valid UUID'),
    
    body('tags')
      .optional()
      .isArray()
      .withMessage('Tags must be an array'),
    
    body('tags.*')
      .optional()
      .isString()
      .withMessage('Each tag must be a string')
  ],

  update: [
    body('title')
      .optional()
      .trim()
      .isLength({ min: 1, max: 200 })
      .withMessage('Task title must be less than 200 characters'),
    
    body('description')
      .optional()
      .trim()
      .isLength({ max: 1000 })
      .withMessage('Description must be less than 1000 characters'),
    
    body('status')
      .optional()
      .isIn(['todo', 'in_progress', 'completed', 'cancelled'])
      .withMessage('Status must be one of: todo, in_progress, completed, cancelled'),
    
    body('priority')
      .optional()
      .isIn(['low', 'medium', 'high', 'urgent'])
      .withMessage('Priority must be one of: low, medium, high, urgent'),
    
    body('dueDate')
      .optional()
      .isISO8601()
      .withMessage('Due date must be a valid date'),
    
    body('estimatedTime')
      .optional()
      .isInt({ min: 1 })
      .withMessage('Estimated time must be a positive integer (minutes)'),
    
    body('actualTime')
      .optional()
      .isInt({ min: 1 })
      .withMessage('Actual time must be a positive integer (minutes)'),
    
    body('tags')
      .optional()
      .isArray()
      .withMessage('Tags must be an array'),
    
    body('tags.*')
      .optional()
      .isString()
      .withMessage('Each tag must be a string')
  ]
};

const commonValidation = {
  uuidParam: [
    param('id')
      .isUUID()
      .withMessage('ID must be a valid UUID')
  ],

  paginationQuery: [
    query('page')
      .optional()
      .isInt({ min: 1 })
      .withMessage('Page must be a positive integer'),
    
    query('limit')
      .optional()
      .isInt({ min: 1, max: 100 })
      .withMessage('Limit must be between 1 and 100')
  ]
};

module.exports = {
  userValidation,
  projectValidation,
  taskValidation,
  commonValidation
};