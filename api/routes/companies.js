const express = require('express');
const { body, query, validationResult } = require('express-validator');
const database = require('../config/database');
const auth = require('../middleware/auth');
const router = express.Router();

// Test route
router.get('/test', (req, res) => {
  res.json({ message: 'Companies route is working!' });
});

// Test database connection
router.get('/test-db', async (req, res) => {
  try {
    const result = await database.query('SELECT COUNT(*) as count FROM new_companies', [], 'portfolio');
    res.json({ 
      message: 'Database test successful!', 
      count: result[0].count,
      result: result 
    });
  } catch (error) {
    res.status(500).json({ 
      message: 'Database test failed', 
      error: error.message 
    });
  }
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
  // auth, // Temporarily disabled for testing
  async (req, res) => {
    try {
      const search = req.query.search;
      const statusFilter = req.query.status;
      const brokerFilter = req.query.broker;
      const countryFilter = req.query.country;
      
      let whereClause = 'WHERE 1=1';
      let params = [];
      
      // Status filtering - default show only companies with no status (NULL)
      if (!statusFilter || statusFilter === 'pending') {
        whereClause += ' AND nc.new_companies_status_id IS NULL';
      } else if (statusFilter === 'bought') {
        whereClause += ' AND nc.new_companies_status_id = 1';
      } else if (statusFilter === 'blocked') {
        whereClause += ' AND nc.new_companies_status_id = 2';
      } else if (statusFilter === 'no') {
        whereClause += ' AND nc.new_companies_status_id = 3';
      } else if (statusFilter === 'all') {
        // Show all records - no additional filter
      }
      
      // Broker filtering by broker_id
      if (brokerFilter && brokerFilter !== 'all') {
        whereClause += ' AND nc.broker_id = ?';
        params.push(brokerFilter);
      }
      
      // Country filtering by country_name
      if (countryFilter && countryFilter !== 'all') {
        whereClause += ' AND nc.country_name = ?';
        params.push(countryFilter);
      }
      
      // Search filtering
      if (search) {
        whereClause += ' AND (nc.company LIKE ? OR nc.country_name LIKE ? OR nc.comments LIKE ?)';
        const searchParam = `%${search}%`;
        params.push(searchParam, searchParam, searchParam);
      }
      
      console.log('Executing query with params:', params);
      console.log('Where clause:', whereClause);
      
      // Set dummy user for testing since auth is disabled
      req.user = { id: 1 };
      
      // Fetch real data from psw_portfolio.new_companies table with cross-database joins
      const companies = await database.query(`
        SELECT 
          nc.new_company_id as id,
          nc.company as name,
          'Investment' as industry,
          COALESCE(nc.country_name, 'Unknown') as location,
          COALESCE(nc.comments, '') as description,
          COALESCE(ncs.status, 'pending') as status,
          'System User' as submittedBy,
          DATE_FORMAT(NOW(), '%Y-%m-%d') as submittedDate,
          '' as contactEmail,
          nc.ticker,
          COALESCE(nc.yield, 0) as yield_percent,
          COALESCE(b.broker_name, 'Unknown') as brokerName,
          COALESCE(nc.country_name, 'Unknown') as countryName
        FROM psw_portfolio.new_companies nc
        LEFT JOIN psw_portfolio.new_companies_status ncs ON nc.new_companies_status_id = ncs.id
        LEFT JOIN psw_foundation.brokers b ON nc.broker_id = b.broker_id
        ${whereClause}
        ORDER BY nc.company ASC
        LIMIT 50
      `, params, 'portfolio');
      
      console.log('Query returned', companies.length, 'companies');
      
      res.json({
        success: true,
        companies: companies,
        totalCount: companies.length
      });
    } catch (error) {
      console.error('Error fetching new companies:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to fetch new companies',
        error: error.message
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

// Get available brokers for filtering
router.get('/filters/brokers', 
  // auth, // Temporarily disabled for testing
  async (req, res) => {
    try {
      const brokers = await database.query(`
        SELECT DISTINCT b.broker_id as id, b.broker_name as name
        FROM psw_foundation.brokers b
        INNER JOIN psw_portfolio.new_companies nc ON b.broker_id = nc.broker_id
        WHERE nc.broker_id IS NOT NULL
        ORDER BY b.broker_name
      `, [], 'foundation');
      
      res.json({
        success: true,
        brokers: brokers
      });
    } catch (error) {
      console.error('Error fetching brokers:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to fetch brokers'
      });
    }
  }
);

// Get available countries for filtering
router.get('/filters/countries', 
  // auth, // Temporarily disabled for testing
  async (req, res) => {
    try {
      const countries = await database.query(`
        SELECT DISTINCT nc.country_name as id, nc.country_name as name
        FROM psw_portfolio.new_companies nc
        WHERE nc.country_name IS NOT NULL AND nc.country_name != ''
        ORDER BY nc.country_name
      `, [], 'portfolio');
      
      res.json({
        success: true,
        countries: countries
      });
    } catch (error) {
      console.error('Error fetching countries:', error);
      res.status(500).json({
        success: false,
        message: 'Failed to fetch countries'
      });
    }
  }
);

module.exports = router;