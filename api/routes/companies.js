const express = require('express');
const { body, query, validationResult } = require('express-validator');
const database = require('../config/database');
const auth = require('../middleware/auth');
const router = express.Router();

// Get all companies with optional filtering
router.get('/', 
  auth,
  [
    query('status').optional().isInt({ min: 1, max: 3 }),
    query('country').optional().isLength({ min: 1, max: 100 }),
    query('limit').optional().isInt({ min: 1, max: 100 }),
    query('offset').optional().isInt({ min: 0 })
  ],
  async (req, res) => {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      const { status, country, limit = 20, offset = 0 } = req.query;
      
      let sql = `
        SELECT 
          nc.new_company_id,
          nc.company_name,
          nc.ticker,
          nc.isin,
          nc.country_name,
          nc.yield,
          nc.inspiration,
          nc.comments,
          nc.borsdata_available,
          ncs.status as company_status,
          ci.country_name as country_full_name
        FROM new_companies nc
        LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
        LEFT JOIN country_info ci ON nc.country_id = ci.id
        WHERE 1=1
      `;
      
      const params = [];
      
      if (status) {
        sql += ' AND nc.new_companies_status_id = ?';
        params.push(status);
      }
      
      if (country) {
        sql += ' AND (nc.country_name LIKE ? OR ci.country_name LIKE ?)';
        params.push(`%${country}%`, `%${country}%`);
      }
      
      sql += ' ORDER BY nc.new_company_id DESC LIMIT ? OFFSET ?';
      params.push(parseInt(limit), parseInt(offset));

      const companies = await database.query(sql, params);
      
      res.json({
        success: true,
        data: companies,
        count: companies.length,
        pagination: {
          limit: parseInt(limit),
          offset: parseInt(offset)
        }
      });
    } catch (error) {
      console.error('Error fetching companies:', error);
      res.status(500).json({ error: 'Failed to fetch companies' });
    }
  }
);

// Get company by ID
router.get('/:id', 
  auth,
  async (req, res) => {
    try {
      const { id } = req.params;
      
      const sql = `
        SELECT 
          nc.*,
          ncs.status as company_status,
          ci.country_name as country_full_name,
          b.broker_name,
          psg.strategy_group_name
        FROM new_companies nc
        LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
        LEFT JOIN country_info ci ON nc.country_id = ci.id
        LEFT JOIN brokers b ON nc.broker_id = b.broker_id
        LEFT JOIN portfolio_strategy_groups psg ON nc.strategy_group_id = psg.strategy_group_id
        WHERE nc.new_company_id = ?
      `;
      
      const companies = await database.query(sql, [id]);
      
      if (companies.length === 0) {
        return res.status(404).json({ error: 'Company not found' });
      }
      
      res.json({
        success: true,
        data: companies[0]
      });
    } catch (error) {
      console.error('Error fetching company:', error);
      res.status(500).json({ error: 'Failed to fetch company' });
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
      `);
      
      const countryStats = await database.query(`
        SELECT country_name, COUNT(*) as count
        FROM new_companies 
        WHERE country_name IS NOT NULL 
        GROUP BY country_name 
        ORDER BY count DESC
      `);
      
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

module.exports = router;