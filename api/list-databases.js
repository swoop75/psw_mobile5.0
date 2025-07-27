require('dotenv').config();
const mysql = require('mysql2/promise');

async function listDatabases() {
  try {
    const connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      port: process.env.DB_PORT,
      user: process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD
    });
    
    const [databases] = await connection.execute('SHOW DATABASES');
    console.log('Available databases:');
    databases.forEach(db => {
      console.log('  -', db.Database);
    });
    
    await connection.end();
  } catch (error) {
    console.error('Error listing databases:', error);
  }
}

listDatabases();