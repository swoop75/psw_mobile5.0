require('dotenv').config();
const database = require('./config/database');

async function testCrossDatabase() {
  try {
    console.log('Testing cross-database joins...');
    
    // Test brokers in psw_foundation
    const brokers = await database.query('SELECT * FROM brokers', [], 'foundation');
    console.log('Brokers in psw_foundation:');
    console.log(JSON.stringify(brokers, null, 2));
    
    // Test new_companies with broker join
    const companiesWithBrokers = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.country_name as location,
        nc.broker_id,
        b.broker_name,
        ncs.status
      FROM psw_portfolio.new_companies nc
      LEFT JOIN psw_foundation.brokers b ON nc.broker_id = b.broker_id
      LEFT JOIN psw_portfolio.new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      WHERE nc.new_companies_status_id IS NULL
      LIMIT 5
    `, [], 'portfolio');
    
    console.log('\nCompanies with broker names (pending status):');
    console.log(JSON.stringify(companiesWithBrokers, null, 2));
    
    // Test all companies with brokers
    const allCompaniesWithBrokers = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.country_name as location,
        nc.broker_id,
        b.broker_name,
        ncs.status
      FROM psw_portfolio.new_companies nc
      LEFT JOIN psw_foundation.brokers b ON nc.broker_id = b.broker_id
      LEFT JOIN psw_portfolio.new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      LIMIT 10
    `, [], 'portfolio');
    
    console.log('\nAll companies with broker names:');
    console.log(JSON.stringify(allCompaniesWithBrokers, null, 2));
    
    // Test broker filter options
    const brokerOptions = await database.query(`
      SELECT DISTINCT b.broker_id as id, b.broker_name as name
      FROM psw_foundation.brokers b
      INNER JOIN psw_portfolio.new_companies nc ON b.broker_id = nc.broker_id
      WHERE nc.broker_id IS NOT NULL
      ORDER BY b.broker_name
    `, [], 'foundation');
    
    console.log('\nAvailable broker filter options:');
    console.log(JSON.stringify(brokerOptions, null, 2));
    
    // Test country filter options
    const countryOptions = await database.query(`
      SELECT DISTINCT nc.country_name as id, nc.country_name as name
      FROM psw_portfolio.new_companies nc
      WHERE nc.country_name IS NOT NULL AND nc.country_name != ''
      ORDER BY nc.country_name
    `, [], 'portfolio');
    
    console.log('\nAvailable country filter options:');
    console.log(JSON.stringify(countryOptions, null, 2));
    
  } catch (error) {
    console.error('Cross-database test failed:', error);
  }
  
  process.exit(0);
}

testCrossDatabase();