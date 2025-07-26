const express = require('express');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { body, validationResult } = require('express-validator');
const database = require('../config/database');
const router = express.Router();

// MySQL database login
router.post('/login', 
  [
    body('username').isLength({ min: 1 }).trim(),
    body('password').isLength({ min: 1 })
  ],
  async (req, res) => {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      const { username, password } = req.body;
      console.log('Login attempt:', { username });
      
      // Query user from psw_foundation.user table
      const users = await database.query(
        'SELECT user_id, user_name, password_hash, email, status FROM user WHERE user_name = ? AND status = 1',
        [username],
        'foundation'
      );

      if (users.length === 0) {
        return res.status(401).json({ error: 'Invalid credentials' });
      }

      const user = users[0];
      
      // Check password (assuming bcrypt hash in database)
      // For now, also check for plain text password for testing
      const isValidPassword = 
        password === user.password_hash || 
        (user.password_hash && await bcrypt.compare(password, user.password_hash));

      if (!isValidPassword) {
        return res.status(401).json({ error: 'Invalid credentials' });
      }

      const token = jwt.sign(
        { 
          userId: user.user_id, 
          username: user.user_name,
          email: user.email,
          role: 'user'
        },
        process.env.JWT_SECRET || 'default-secret',
        { expiresIn: process.env.JWT_EXPIRE || '24h' }
      );

      res.json({
        success: true,
        token,
        user: {
          id: user.user_id,
          username: user.user_name,
          email: user.email,
          role: 'user'
        }
      });
    } catch (error) {
      console.error('Login error:', error);
      res.status(500).json({ error: 'Login failed' });
    }
  }
);

// Verify token
router.get('/verify', async (req, res) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '');
    
    if (!token) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'default-secret');
    
    res.json({
      success: true,
      user: {
        id: decoded.userId,
        username: decoded.username,
        role: decoded.role
      }
    });
  } catch (error) {
    res.status(401).json({ error: 'Invalid token' });
  }
});

// Refresh token
router.post('/refresh', async (req, res) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '');
    
    if (!token) {
      return res.status(401).json({ error: 'No token provided' });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET || 'default-secret');
    
    // Generate new token
    const newToken = jwt.sign(
      { 
        userId: decoded.userId, 
        username: decoded.username,
        role: decoded.role 
      },
      process.env.JWT_SECRET || 'default-secret',
      { expiresIn: process.env.JWT_EXPIRE || '24h' }
    );

    res.json({
      success: true,
      token: newToken
    });
  } catch (error) {
    res.status(401).json({ error: 'Token refresh failed' });
  }
});

module.exports = router;