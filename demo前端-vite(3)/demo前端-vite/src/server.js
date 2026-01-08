import express from 'express';
import bcrypt from 'bcryptjs';
import cors from 'cors';
import pool from './db.js';

const app = express();
const port = 8080;

// 中间件
app.use(express.json());
app.use(cors());

// 最简注册接口
app.post('/api/auth/register', async (req, res) => {
    console.log('收到注册请求:', req.body); // 打印请求体

    try {
        const { username, password, confirmPassword } = req.body;

        // 1. 验证必填字段
        if (!username || !password || !confirmPassword) {
            return res.status(400).json({ error: '所有字段必填' });
        }

        // 2. 验证密码一致性
        if (password !== confirmPassword) {
            return res.status(400).json({ error: '两次密码不一致' });
        }

        // 3. 检查用户名是否存在
        const [users] = await pool.query(
            'SELECT id FROM users WHERE username = ?',
            [username]
        );

        if (users.length > 0) {
            console.log(`用户名 ${username} 已存在`);
            return res.status(409).json({ error: '用户名已存在' });
        }

        // 4. 密码哈希
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);
        console.log('密码哈希完成:', hashedPassword);

        // 5. 插入用户数据
        const [result] = await pool.query(
            'INSERT INTO users (username, password) VALUES (?, ?)',
            [username, hashedPassword]
        );
        console.log('数据库插入结果:', result);

        // 6. 获取生成的用户ID
        if (!result.insertId) {
            throw new Error('insertId未生成');
        }

        // 7. 返回成功响应
        res.status(201).json({
            id: result.insertId,
            username,
            message: '注册成功'
        });

    } catch (error) {
        console.error('完整错误堆栈:', error.stack); // 打印错误堆栈
        res.status(500).json({
            message: `注册失败: ${error.message}`,
            sqlError: error.sqlMessage // 返回SQL错误详情
        });
    }
});

// 启动服务
app.listen(port, () => {
    console.log(`服务运行在 http://localhost:${port}`);
});