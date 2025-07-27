require('dotenv').config();
const database = require('./config/database');

async function testSimple() {
  try {
    console.log('Testing simple database query...');
    
    // Test the basic companies query without joins
    const companies = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.country_name as location,
        nc.comments as description,
        nc.new_companies_status_id,
        ncs.status as status
      FROM new_companies nc
      LEFT JOIN new_companies_status ncs ON nc.new_companies_status_id = ncs.id
      LIMIT 10
    `, [], 'portfolio');
    
    console.log('Companies with status:');
    console.log(JSON.stringify(companies, null, 2));
    
    // Test companies with NULL status (pending)
    const pendingCompanies = await database.query(`
      SELECT 
        nc.new_company_id as id,
        nc.company as name,
        nc.country_name as location,
        nc.comments as description,
        nc.new_companies_status_id
      FROM new_companies nc
      WHERE nc.new_companies_status_id IS NULL
      LIMIT 5
    `, [], 'portfolio');
    
    console.log('\nPending companies (status IS NULL):');
    console.log(JSON.stringify(pendingCompanies, null, 2));
    
  } catch (error) {
    console.error('Database test failed:', error);
  }
  
  process.exit(0);
}

testSimple();