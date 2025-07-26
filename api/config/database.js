const mysql = require('mysql2/promise');

class Database {
  constructor() {
    this.pools = {};
    this.initializePools();
  }

  initializePools() {
    const poolConfig = {
      host: process.env.DB_HOST,
      port: process.env.DB_PORT,
      user: process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD,
      waitForConnections: true,
      connectionLimit: 10,
      queueLimit: 0,
      acquireTimeout: 60000,
      timeout: 60000,
      reconnect: true
    };

    // Create pools for each database
    this.pools.foundation = mysql.createPool({
      ...poolConfig,
      database: process.env.DB_FOUNDATION
    });

    this.pools.marketdata = mysql.createPool({
      ...poolConfig,
      database: process.env.DB_MARKETDATA
    });

    this.pools.portfolio = mysql.createPool({
      ...poolConfig,
      database: process.env.DB_PORTFOLIO
    });
  }

  getPool(database = 'portfolio') {
    return this.pools[database];
  }

  async query(sql, params = [], database = 'portfolio') {
    try {
      const pool = this.getPool(database);
      const [rows] = await pool.execute(sql, params);
      return rows;
    } catch (error) {
      console.error('Database query error:', error);
      throw error;
    }
  }

  async closeAll() {
    await Promise.all(Object.values(this.pools).map(pool => pool.end()));
  }
}

module.exports = new Database();