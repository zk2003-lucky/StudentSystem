// 最简单的单例连接（非生产环境）
import mysql from 'mysql2/promise';

const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',          // 替换为你的数据库用户
    password: 'LIUMENGYU666',   // 替换为你的数据库密码
    database: 'club_management',
    waitForConnections: true,
    connectionLimit: 10
});

export default pool;