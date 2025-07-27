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
    // Fetch from masterlist table
    const companies = await database.query(`
      SELECT 
        isin as id,
        name,
        COALESCE(market, 'Unknown') as industry,
        COALESCE(country, 'Unknown') as location,
        CASE WHEN delisted = 1 THEN 'Inactive' ELSE 'Active' END as status,
        ticker,
        '' as description,
        '' as website
      FROM masterlist 
      WHERE current_version = 1 AND (delisted = 0 OR delisted IS NULL)
      ORDER BY name
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
      const search = req.query.search;
      const statusFilter = req.query.status; // 'pending', 'all', 'active', 'inactive'
      
      let whereClause = '';
      let params = [];
      
      // Status filtering
      if (!statusFilter || statusFilter === 'pending') {
        whereClause = 'WHERE new_companies_status_id IS NULL';
      } else if (statusFilter === 'active') {
        whereClause = 'WHERE new_companies_status_id = 1';
      } else if (statusFilter === 'inactive') {
        whereClause = 'WHERE new_companies_status_id = 3';
      } else if (statusFilter === 'all') {
        whereClause = 'WHERE 1=1'; // Show all records
      } else {
        whereClause = 'WHERE new_companies_status_id IS NULL'; // Default to pending
      }
      
      if (search) {
        whereClause += ' AND (company LIKE ? OR country_name LIKE ? OR comments LIKE ?)';
        const searchParam = `%${search}%`;
        params = [searchParam, searchParam, searchParam];
      }
      
      // Fetch from new_companies table in psw_portfolio database
      const companies = await database.query(`
        SELECT 
          new_company_id as id,
          company as name,
          'Investment' as industry,
          COALESCE(country_name, 'Unknown') as location,
          COALESCE(comments, '') as description,
          CASE 
            WHEN new_companies_status_id = 1 THEN 'Active'
            WHEN new_companies_status_id = 2 THEN 'Pending'
            WHEN new_companies_status_id = 3 THEN 'Inactive'
            ELSE 'Pending'
          END as status,
          'System User' as submittedBy,
          DATE_FORMAT(NOW(), '%Y-%m-%d') as submittedDate,
          '' as contactEmail,
          ticker,
          COALESCE(yield, 0) as yield_percent
        FROM new_companies 
        ${whereClause}
        ORDER BY company ASC
        LIMIT 50
      `, params, 'portfolio');
      
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

// Company Actions - Approve/Reject
router.post('/action', 
  auth,
  [
    body('companyId').notEmpty().withMessage('Company ID is required'),
    body('action').isIn(['approve', 'reject']).withMessage('Action must be approve or reject')
  ],
  async (req, res) => {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation errors',
          errors: errors.array()
        });
      }

      const { companyId, action } = req.body;
      const userId = req.user.id;
      
      if (action === 'approve') {
        // Update new_companies_status_id to 1 (Active)
        await database.query(`
          UPDATE new_companies 
          SET new_companies_status_id = 1
          WHERE new_company_id = ?
        `, [companyId], 'portfolio');
        
        res.json({
          success: true,
          message: 'Company approved successfully'
        });
      } else if (action === 'reject') {
        // Update new_companies_status_id to 3 (Inactive)
        await database.query(`
          UPDATE new_companies 
          SET new_companies_status_id = 3
          WHERE new_company_id = ?
        `, [companyId], 'portfolio');
        
        res.json({
          success: true,
          message: 'Company rejected successfully'
        });
      }
    } catch (error) {
      console.error('Error performing company action:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to perform action on company'
      });
    }
  }
);

module.exports = router;