require('dotenv').config();
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

async function importDatabase() {
  let connection;
  
  try {
    // Read the database overview JSON file
    const jsonPath = path.join(__dirname, '../developing/MySQL_overview/mysql_database_overview_latest.json');
    console.log('Reading database structure from:', jsonPath);
    
    const jsonData = JSON.parse(fs.readFileSync(jsonPath, 'utf8'));
    console.log('JSON data loaded successfully');
    
    // Connect to MySQL without specifying database
    connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      port: process.env.DB_PORT,
      user: process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD
    });
    
    console.log('Connected to MySQL server');
    
    // Create psw_portfolio database
    console.log('Creating psw_portfolio database...');
    await connection.query('CREATE DATABASE IF NOT EXISTS psw_portfolio');
    await connection.query('USE psw_portfolio');
    console.log('Database psw_portfolio created and selected');
    
    // Find psw_portfolio database in JSON data
    const portfolioDb = jsonData.databases.find(db => db.name === 'psw_portfolio');
    if (!portfolioDb) {
      throw new Error('psw_portfolio database not found in JSON file');
    }
    
    console.log(`Found psw_portfolio database with ${portfolioDb.tables.length} tables`);
    
    // Create all tables
    console.log('Available tables:', portfolioDb.tables.map(t => t.name));
    
    // Create tables in dependency order
    const tableOrder = [
      'new_companies_status',
      'brokers', 
      'portfolio_strategy_groups',
      'country_info',
      'new_companies'
    ];
    
    for (const tableName of tableOrder) {
      const table = portfolioDb.tables.find(t => t.name === tableName);
      if (table) {
        await createTable(connection, table);
        await insertData(connection, table);
      } else {
        console.log(`  - Table ${tableName} not found in JSON data`);
      }
    }
    
    // Create any other tables not in the ordered list
    for (const table of portfolioDb.tables) {
      if (!tableOrder.includes(table.name)) {
        await createTable(connection, table);
        await insertData(connection, table);
      }
    }
    
    console.log('Database import completed successfully!');
    
  } catch (error) {
    console.error('Error importing database:', error);
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

async function createTable(connection, table) {
  console.log(`Creating table: ${table.name}`);
  
  let createSQL = `CREATE TABLE IF NOT EXISTS \`${table.name}\` (\n`;
  
  // Add columns
  const columnDefs = table.schema.map(column => {
    let def = `  \`${column.Field}\` ${column.Type}`;
    
    if (column.Null === 'NO') {
      def += ' NOT NULL';
    }
    
    if (column.Default !== null && column.Default !== undefined) {
      if (column.Default === 'CURRENT_TIMESTAMP') {
        def += ' DEFAULT CURRENT_TIMESTAMP';
      } else {
        def += ` DEFAULT '${column.Default}'`;
      }
    }
    
    if (column.Extra) {
      def += ` ${column.Extra}`;
    }
    
    return def;
  });
  
  createSQL += columnDefs.join(',\n');
  
  // Add primary key
  const primaryKey = table.schema.find(col => col.Key === 'PRI');
  if (primaryKey) {
    createSQL += `,\n  PRIMARY KEY (\`${primaryKey.Field}\`)`;
  }
  
  createSQL += '\n)';
  
  try {
    await connection.query(createSQL);
    console.log(`  ✓ Table ${table.name} created`);
  } catch (error) {
    console.error(`  ✗ Error creating table ${table.name}:`, error.message);
  }
}

async function insertData(connection, table) {
  if (!table.sample_data || !table.sample_data.rows || table.sample_data.rows.length === 0) {
    console.log(`  - No sample data for table ${table.name}`);
    return;
  }
  
  console.log(`Inserting data into table: ${table.name}`);
  
  const columns = table.sample_data.columns;
  const rows = table.sample_data.rows;
  
  if (columns && rows) {
    const columnList = columns.map(col => `\`${col}\``).join(', ');
    const placeholders = '(' + columns.map(() => '?').join(', ') + ')';
    
    const insertSQL = `INSERT IGNORE INTO \`${table.name}\` (${columnList}) VALUES ${placeholders}`;
    
    for (const row of rows) {
      try {
        await connection.execute(insertSQL, row);
      } catch (error) {
        console.error(`  ✗ Error inserting row into ${table.name}:`, error.message);
      }
    }
    
    console.log(`  ✓ Inserted ${rows.length} rows into ${table.name}`);
  }
}

// Run the import
importDatabase();