const express = require('express');
const jwt = require('jsonwebtoken');
const { body, validationResult } = require('express-validator');
const router = express.Router();

// Simple login for testing
router.post('/login', 
  [
    body('username').isLength({ min: 3 }).trim(),
    body('password').isLength({ min: 6 })
  ],
  async (req, res) => {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      const { username, password } = req.body;
      
      // Simple check for testing
      if (username === 'admin' && password === 'password123') {
        const token = jwt.sign(
          { 
            userId: 1, 
            username: 'admin',
            role: 'admin' 
          },
          process.env.JWT_SECRET || 'default-secret',
          { expiresIn: process.env.JWT_EXPIRE || '24h' }
        );

        res.json({
          success: true,
          token,
          user: {
            id: 1,
            username: 'admin',
            role: 'admin'
          }
        });
      } else {
        res.status(401).json({ error: 'Invalid credentials - UPDATED CODE' });
      }
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