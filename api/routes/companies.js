const express = require('express');
const { body, query, validationResult } = require('express-validator');
const database = require('../config/database');
const auth = require('../middleware/auth');
const router = express.Router();

// Test route
router.get('/test', (req, res) => {
  res.json({ message: 'Companies route is working!' });
});

// Masterlist endpoint - fetch from database
router.get('/masterlist', async (req, res) => {
  try {
    // Try to fetch from new_companies table first
    const companies = await database.query(`
      SELECT 
        id,
        company_name as name,
        COALESCE(industry, 'Unknown') as industry,
        COALESCE(country_name, 'Unknown') as location,
        CASE 
          WHEN new_companies_status_id = 1 THEN 'Active'
          WHEN new_companies_status_id = 2 THEN 'Pending'
          WHEN new_companies_status_id = 3 THEN 'Inactive'
          ELSE 'Unknown'
        END as status,
        description,
        website
      FROM new_companies 
      WHERE new_companies_status_id = 1
      ORDER BY company_name
      LIMIT 50
    `, [], 'foundation');
    
    res.json({
      success: true,
      companies: companies,
      totalCount: companies.length
    });
  } catch (error) {
    console.error('Error fetching masterlist:', error);
    
    // Fallback to mock data if database query fails
    const companies = [
      {
        id: "1",
        name: "Apple Inc.",
        industry: "Technology",
        location: "USA",
        status: "Active",
        description: "Consumer electronics and software",
        website: "https://apple.com"
      },
      {
        id: "2",
        name: "Microsoft Corp.",
        industry: "Technology", 
        location: "USA",
        status: "Active",
        description: "Software and cloud services",
        website: "https://microsoft.com"
      }
    ];
    
    res.json({
      success: true,
      companies: companies,
      totalCount: companies.length
    });
  }
});

// New Companies - Pending approval
router.get('/new', 
  auth,
  async (req, res) => {
    try {
      // Fetch pending companies from database
      const companies = await database.query(`
        SELECT 
          id,
          company_name as name,
          COALESCE(industry, 'Unknown') as industry,
          COALESCE(country_name, 'Unknown') as location,
          description,
          'Pending' as status,
          COALESCE(created_by, 'Unknown') as submittedBy,
          DATE_FORMAT(created_at, '%Y-%m-%d') as submittedDate,
          contact_email as contactEmail,
          website
        FROM new_companies 
        WHERE new_companies_status_id = 2
        ORDER BY created_at DESC
        LIMIT 50
      `, [], 'foundation');
      
      res.json({
        success: true,
        companies: companies,
        totalCount: companies.length
      });
    } catch (error) {
      console.error('Error fetching new companies:', error);
      
      // Fallback to mock data if database query fails
      const companies = [
        {
          id: "1",
          name: "Spotify Technology",
          industry: "Media & Entertainment",
          location: "Sweden",
          description: "Music streaming service",
          status: "Pending",
          submittedBy: "admin",
          submittedDate: "2024-01-20",
          contactEmail: "info@spotify.com"
        },
        {
          id: "2",
          name: "NVIDIA Corp.",
          industry: "Technology", 
          location: "USA",
          description: "Graphics processing unit and AI chips",
          status: "Pending",
          submittedBy: "admin",
          submittedDate: "2024-01-19",
          contactEmail: "info@nvidia.com"
        }
      ];
      
      res.json({
        success: true,
        companies: companies,
        totalCount: companies.length
      });
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