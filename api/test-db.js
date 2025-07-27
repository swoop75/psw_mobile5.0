require('dotenv').config();
const database = require('./config/database');

async function testDatabase() {
  try {
    console.log('Testing database connection...');
    
    // Test basic connection
    const countResult = await database.query('SELECT COUNT(*) as count FROM new_companies', [], 'portfolio');
    console.log('Total companies in new_companies table:', countResult[0].count);
    
    // Test the actual query we're using
    const companies = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.country_name as location,
        nc.comments as description,
        ncs.status as status,
        b.broker_name as brokerName
      FROM new_companies nc
      LEFT JOIN brokers b ON nc.broker_id = b.broker_id
      LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      WHERE nc.new_companies_status_id IS NULL
      LIMIT 5
    `, [], 'portfolio');
    
    console.log('Sample companies (status NULL):');
    console.log(JSON.stringify(companies, null, 2));
    
    // Test all companies
    const allCompanies = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.new_companies_status_id,
        ncs.status as status
      FROM new_companies nc
      LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      LIMIT 10
    `, [], 'portfolio');
    
    console.log('\nAll companies sample:');
    console.log(JSON.stringify(allCompanies, null, 2));
    
    // Test status table
    const statuses = await database.query('SELECT * FROM new_companies_status', [], 'portfolio');
    console.log('\nStatus values:');
    console.log(JSON.stringify(statuses, null, 2));
    
    // Test brokers
    const brokers = await database.query('SELECT * FROM brokers LIMIT 5', [], 'portfolio');
    console.log('\nBrokers:');
    console.log(JSON.stringify(brokers, null, 2));
    
  } catch (error) {
    console.error('Database test failed:', error);
  }
  
  process.exit(0);
}

testDatabase();