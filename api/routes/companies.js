const express = require('express');
const { body, query, validationResult } = require('express-validator');
const database = require('../config/database');
const auth = require('../middleware/auth');
const router = express.Router();

// Test route
router.get('/test', (req, res) => {
  res.json({ message: 'Companies route is working!' });
});

// Masterlist endpoint (working version)
router.get('/masterlist', (req, res) => {
  const companies = [
    {
      id: 1,
      company_name: "Apple Inc.",
      ticker: "AAPL",
      country_name: "USA",
      status: "Active",
      yield: 0.5
    },
    {
      id: 2,
      company_name: "Microsoft Corp.",
      ticker: "MSFT", 
      country_name: "USA",
      status: "Active",
      yield: 2.3
    }
  ];
  
  res.json({
    success: true,
    companies: companies
  });
});

// New Companies - Pending approval
router.get('/new', 
  auth,
  async (req, res) => {
    try {
      // For now, return mock data until we confirm table structure
      const companies = [
        {
          id: 1,
          company_name: "Tesla Inc.",
          ticker: "TSLA",
          country_name: "USA",
          status: "Pending",
          yield: 0.0,
          date_added: "2024-01-20"
        },
        {
          id: 2,
          company_name: "NVIDIA Corp.",
          ticker: "NVDA", 
          country_name: "USA",
          status: "Pending",
          yield: 0.1,
          date_added: "2024-01-19"
        }
      ];
      
      res.json({
        success: true,
        companies: companies
      });
    } catch (error) {
      console.error('Error fetching new companies:', error);
      res.status(500).json({ error: 'Failed to fetch new companies' });
    }
  }
);

// Get company statistics
router.get('/stats/summary', 
  auth,
  async (req, res) => {
    try {
      const stats = await database.query(`
        SELECT 
          COUNT(*) as total_companies,
          COUNT(CASE WHEN new_companies_status_id = 1 THEN 1 END) as active_companies,
          COUNT(CASE WHEN new_companies_status_id = 3 THEN 1 END) as inactive_companies,
          COUNT(CASE WHEN borsdata_available = 1 THEN 1 END) as borsdata_available,
          AVG(CASE WHEN yield IS NOT NULL AND yield > 0 THEN yield END) as avg_yield
        FROM new_companies
      `, [], 'foundation');
      
      const countryStats = await database.query(`
        SELECT country_name, COUNT(*) as count
        FROM new_companies 
        WHERE country_name IS NOT NULL 
        GROUP BY country_name 
        ORDER BY count DESC
      `, [], 'foundation');
      
      res.json({
        success: true,
        data: {
          summary: stats[0],
          by_country: countryStats
        }
      });
    } catch (error) {
      console.error('Error fetching company stats:', error);
      res.status(500).json({ error: 'Failed to fetch company statistics' });
    }
  }
);

// TEMPORARILY REMOVED /:id route to test other routes

module.exports = router;