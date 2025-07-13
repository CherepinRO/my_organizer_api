const express = require('express');
const UserController = require('../controllers/UserController');
const { authenticateToken } = require('../middleware/auth');
const { userValidation, commonValidation } = require('../middleware/validation');

const router = express.Router();

// Public routes
router.post('/register', userValidation.register, UserController.register);
router.post('/login', userValidation.login, UserController.login);

// Protected routes
router.use(authenticateToken);

router.get('/profile', UserController.getProfile);
router.put('/profile', userValidation.updateProfile, UserController.updateProfile);
router.delete('/account', UserController.deleteAccount);
router.put('/preferences', UserController.updatePreferences);
router.get('/stats', UserController.getUserStats);
router.put('/password', userValidation.changePassword, UserController.changePassword);

module.exports = router;