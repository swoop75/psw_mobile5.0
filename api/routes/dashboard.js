const express = require('express');
const database = require('../config/database');
const auth = require('../middleware/auth');
const router = express.Router();

// Dashboard overview
router.get('/overview', auth, async (req, res) => {
  try {
    // Get recent companies
    const recentCompanies = await database.query(`
      SELECT 
        nc.new_company_id,
        nc.company_name,
        nc.ticker,
        nc.country_name,
        nc.yield,
        ncs.status as company_status,
        DATE(nc.created) as date_added
      FROM new_companies nc
      LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      ORDER BY nc.new_company_id DESC 
      LIMIT 5
    `);

    // Get summary statistics
    const summaryStats = await database.query(`
      SELECT 
        COUNT(*) as total_companies,
        COUNT(CASE WHEN new_companies_status_id = 1 THEN 1 END) as active_companies,
        COUNT(CASE WHEN yield IS NOT NULL AND yield > 0 THEN 1 END) as companies_with_yield,
        AVG(CASE WHEN yield IS NOT NULL AND yield > 0 THEN yield END) as avg_yield,
        MAX(yield) as max_yield
      FROM new_companies
    `);

    // Get top countries by company count
    const topCountries = await database.query(`
      SELECT 
        country_name,
        COUNT(*) as company_count
      FROM new_companies 
      WHERE country_name IS NOT NULL 
      GROUP BY country_name 
      ORDER BY company_count DESC 
      LIMIT 5
    `);

    // Get companies added this week
    const weeklyAdditions = await database.query(`
      SELECT COUNT(*) as count
      FROM new_companies 
      WHERE created >= DATE_SUB(NOW(), INTERVAL 7 DAY)
    `);

    res.json({
      success: true,
      data: {
        recent_companies: recentCompanies,
        summary: summaryStats[0],
        top_countries: topCountries,
        weekly_additions: weeklyAdditions[0].count
      }
    });
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
    res.status(500).json({ error: 'Failed to fetch dashboard data' });
  }
});

// Dashboard charts data
router.get('/charts', auth, async (req, res) => {
  try {
    // Companies by status
    const statusChart = await database.query(`
      SELECT 
        COALESCE(ncs.status, 'Unknown') as status,
        COUNT(*) as count
      FROM new_companies nc
      LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      GROUP BY ncs.status
    `);

    // Companies by country
    const countryChart = await database.query(`
      SELECT 
        country_name,
        COUNT(*) as count
      FROM new_companies 
      WHERE country_name IS NOT NULL 
      GROUP BY country_name 
      ORDER BY count DESC
    `);

    // Yield distribution
    const yieldChart = await database.query(`
      SELECT 
        CASE 
          WHEN yield IS NULL OR yield = 0 THEN 'No Yield'
          WHEN yield <= 2 THEN '0-2%'
          WHEN yield <= 4 THEN '2-4%'
          WHEN yield <= 6 THEN '4-6%'
          WHEN yield <= 8 THEN '6-8%'
          ELSE '8%+'
        END as yield_range,
        COUNT(*) as count
      FROM new_companies
      GROUP BY yield_range
      ORDER BY 
        CASE yield_range
          WHEN 'No Yield' THEN 0
          WHEN '0-2%' THEN 1
          WHEN '2-4%' THEN 2
          WHEN '4-6%' THEN 3
          WHEN '6-8%' THEN 4
          WHEN '8%+' THEN 5
        END
    `);

    res.json({
      success: true,
      data: {
        status_distribution: statusChart,
        country_distribution: countryChart,
        yield_distribution: yieldChart
      }
    });
  } catch (error) {
    console.error('Error fetching chart data:', error);
    res.status(500).json({ error: 'Failed to fetch chart data' });
  }
});

module.exports = router;