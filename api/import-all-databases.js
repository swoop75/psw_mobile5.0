require('dotenv').config();
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

async function importAllDatabases() {
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
    
    // Import each database
    const databasesToImport = ['psw_foundation', 'psw_portfolio'];
    
    for (const dbName of databasesToImport) {
      await importDatabase(connection, jsonData, dbName);
    }
    
    console.log('All databases imported successfully!');
    
  } catch (error) {
    console.error('Error importing databases:', error);
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

async function importDatabase(connection, jsonData, dbName) {
  console.log(`\n=== Importing ${dbName} ===`);
  
  // Create database
  await connection.query(`CREATE DATABASE IF NOT EXISTS ${dbName}`);
  await connection.query(`USE ${dbName}`);
  console.log(`Database ${dbName} created and selected`);
  
  // Find database in JSON data
  const dbData = jsonData.databases.find(db => db.name === dbName);
  if (!dbData) {
    console.log(`Database ${dbName} not found in JSON file, skipping`);
    return;
  }
  
  console.log(`Found ${dbName} with ${dbData.tables.length} tables`);
  
  // Import essential tables based on database
  let tablesToImport = [];
  
  if (dbName === 'psw_foundation') {
    tablesToImport = [
      'brokers',
      'broker_type', 
      'portfolio_strategy_groups',
      'currencies',
      'asset_class_group'
    ];
  } else if (dbName === 'psw_portfolio') {
    tablesToImport = [
      'new_companies_status',
      'new_companies',
      'portfolios'
    ];
  }
  
  // Create essential tables first
  for (const tableName of tablesToImport) {
    const table = dbData.tables.find(t => t.name === tableName);
    if (table) {
      await createTable(connection, table);
      await insertData(connection, table);
    } else {
      console.log(`  - Table ${tableName} not found in ${dbName}`);
    }
  }
  
  console.log(`${dbName} import completed`);
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
      } else if (column.Default === '0') {
        def += ' DEFAULT 0';
      } else {
        def += ` DEFAULT '${column.Default}'`;
      }
    }
    
    if (column.Extra) {
      if (column.Extra === 'auto_increment') {
        def += ' AUTO_INCREMENT';
      }
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
importAllDatabases();