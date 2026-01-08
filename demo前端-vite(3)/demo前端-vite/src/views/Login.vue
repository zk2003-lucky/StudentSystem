<template>
  <div class="login-wrapper">
    <canvas ref="canvas" class="particles-background"></canvas>
    <div class="login-container">
      <div class="decorative-line"></div>
      <h1 class="title">
        <span class="title-text">欢迎登录</span>
        <div class="title-underline"></div>
      </h1>

      <transition name="slide-fade">
        <div v-if="errorMessage" class="error-message">
          <i class="fas fa-exclamation-triangle"></i>
          <span>{{ errorMessage }}</span>
        </div>
      </transition>

      <div class="form-group">
        <i class="icon fas fa-user"></i>
        <input
            v-model="user.username"
            placeholder=" "
            class="form-control"
            @focus="resetError"
        />
        <label class="floating-label">用户名</label>
      </div>

      <div class="form-group">
        <i class="icon fas fa-lock"></i>
        <input
            v-model="user.password"
            type="password"
            placeholder=" "
            class="form-control"
            @focus="resetError"
        />
        <label class="floating-label">密码</label>
      </div>

      <div class="button-group">
        <button @click="handleLogin" class="btn-primary">
          <i class="fas fa-sign-in-alt"></i>
          <span>立即登录</span>
        </button>
        <button @click="handleCancel" class="btn-secondary">
          <i class="fas fa-undo"></i>
          <span>取消</span>
        </button>
      </div>

      <div class="additional-links">
        <router-link to="/register">
          <i class="fas fa-user-plus"></i>
<!--          立即注册-->
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { login } from '../services/authService';

// 修复：添加缺失的响应式变量声明
const route = useRoute();
const router = useRouter();
const errorMessage = ref('');  // 明确定义 errorMessage
const user = ref({             // 明确定义 user 对象
  username: '',
  password: ''
});
import { ref, onMounted, onBeforeUnmount } from 'vue'

const canvas = ref(null)
let animationFrame = null
let particles = []

class Particle {
  constructor(canvas, ctx) {
    this.canvas = canvas
    this.ctx = ctx
    this.x = Math.random() * canvas.width
    this.y = Math.random() * canvas.height
    this.velocity = {
      x: (Math.random() - 0.5) * 0.5,
      y: (Math.random() - 0.5) * 0.5
    }
    this.radius = Math.random() * 1.5 + 0.5
    this.mouseRadius = 80
    this.color = `rgba(52, 152, 219, ${Math.random() * 0.5 + 0.2})`
  }

  draw() {
    this.ctx.beginPath()
    this.ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    this.ctx.fillStyle = this.color
    this.ctx.fill()
  }

  update(mouse) {
    // 边界检测
    if (this.x + this.radius > this.canvas.width || this.x - this.radius < 0) {
      this.velocity.x = -this.velocity.x
    }
    if (this.y + this.radius > this.canvas.height || this.y - this.radius < 0) {
      this.velocity.y = -this.velocity.y
    }

    // 鼠标交互
    const dx = mouse.x - this.x
    const dy = mouse.y - this.y
    const distance = Math.sqrt(dx * dx + dy * dy)

    if (distance < this.mouseRadius) {
      const angle = Math.atan2(dy, dx)
      const force = (this.mouseRadius - distance) / this.mouseRadius
      this.velocity.x += Math.cos(angle) * force * 0.1
      this.velocity.y += Math.sin(angle) * force * 0.1
    }

    this.x += this.velocity.x
    this.y += this.velocity.y
    this.draw()
  }
}

const mouse = { x: 0, y: 0 }

const initCanvas = () => {
  const ctx = canvas.value.getContext('2d')
  canvas.value.width = window.innerWidth
  canvas.value.height = window.innerHeight

  // 创建粒子
  particles = Array.from({ length: 300 }, () => new Particle(canvas.value, ctx))

  const animate = () => {
    ctx.clearRect(0, 0, canvas.value.width, canvas.value.height)

    // 绘制连接线
    particles.forEach((p1, i) => {
      particles.slice(i).forEach(p2 => {
        const dx = p1.x - p2.x
        const dy = p1.y - p2.y
        const distance = Math.sqrt(dx * dx + dy * dy)

        if (distance < 100) {
          ctx.beginPath()
          ctx.strokeStyle = `rgba(52, 152, 219, ${1 - distance/100})`
          ctx.lineWidth = 0.5
          ctx.moveTo(p1.x, p1.y)
          ctx.lineTo(p2.x, p2.y)
          ctx.stroke()
        }
      })
    })

    particles.forEach(particle => particle.update(mouse))
    animationFrame = requestAnimationFrame(animate)
  }

  animate()
}

onMounted(() => {
  initCanvas()
  window.addEventListener('resize', handleResize)
  window.addEventListener('mousemove', handleMouseMove)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationFrame)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', handleMouseMove)
})

const handleResize = () => {
  canvas.value.width = window.innerWidth
  canvas.value.height = window.innerHeight
}

const handleMouseMove = (e) => {
  mouse.x = e.clientX
  mouse.y = e.clientY
}

// 新增 resetError 方法
const resetError = () => {
  errorMessage.value = '';
};

const handleLogin = async () => {
  try {
    const token = await login(user.value);
    localStorage.setItem('token', token);
    const redirect = route.query.redirect || '/';
    router.replace(redirect);
  } catch (error) {
    errorMessage.value = '登录失败，请检查用户名和密码';
    localStorage.removeItem('token');
  }
};

const handleCancel = () => router.go(-1);
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f0f4f8; /* 柔和的蓝灰色背景 */
  position: relative;
  overflow: hidden;
}
.login-wrapper {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: #1a1a2e;
}

.particles-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.login-container {
  position: relative;
  z-index: 2;
  width: 440px;
  max-width: 95%;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transform: translateY(0);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 0 auto;
}

/* 添加抽象几何图形背景 */
.login-wrapper::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: rgba(163, 218, 255, 0.1);
  border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  top: -30%;
  right: -20%;
  animation: float 20s infinite linear;
}

.login-wrapper::after {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: rgba(178, 255, 214, 0.08);
  border-radius: 70% 30% 30% 70% / 70% 70% 30% 30%;
  bottom: -30%;
  left: -20%;
  animation: float 25s infinite linear;
}

@keyframes float {
  0% { transform: rotate(0deg) translate(0, 0); }
  100% { transform: rotate(360deg) translate(50px, 50px); }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 440px;
  max-width: 95%;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.title-text {
  color: #2c3e50;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.title-underline {
  background: #3498db; /* 更柔和的蓝色 */
  height: 3px;
}

.form-control {
  border: 1px solid #dbe4ed; /* 更细的边框 */
  background: #f8fafc;
}

.form-control:focus {
  border-color: #3498db;
  box-shadow: 0 2px 12px rgba(52, 152, 219, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
}

.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(45deg, #63f1ec, #5cf664, #4876ec);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-container {
  position: relative;
  width: 440px;
  max-width: 95%;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transform: translateY(0);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.login-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.3);
}

.decorative-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #f1af63, #f6d25c, #dcec48);
}

.title {
  text-align: center;
  margin-bottom: 2.5rem;
  position: relative;
}

.title-text {
  display: inline-block;
  color: #1f2937;
  font-size: 2.4rem;
  font-weight: 600;
  letter-spacing: 1px;
  position: relative;
}

.title-underline {
  width: 60px;
  height: 4px;
  background: #6366f1;
  margin: 16px auto;
  border-radius: 2px;
  transform-origin: center;
  animation: underlineScale 1.2s ease;
}

@keyframes underlineScale {
  0% { transform: scaleX(0); }
  100% { transform: scaleX(1); }
}

.form-group {
  position: relative;
  margin-bottom: 2rem;
}

.form-control {
  width: 100%;
  height: 56px;
  padding: 24px 48px 8px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.form-control:focus {
  border-color: #6366f1;
  background: #ffffff;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.15);
}

.floating-label {
  position: absolute;
  left: 48px;
  top: 18px;
  color: #94a3b8;
  pointer-events: none;
  transition: all 0.3s ease;
  font-size: 16px;
}

.form-control:focus ~ .floating-label,
.form-control:not(:placeholder-shown) ~ .floating-label {
  transform: translateY(-14px);
  font-size: 12px;
  color: #6366f1;
}

.icon {
  position: absolute;
  left: 18px;
  top: 18px;
  color: #94a3b8;
  font-size: 18px;
  transition: color 0.3s ease;
}

.form-control:focus ~ .icon {
  color: #6366f1;
}

.button-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.2rem;
  margin-top: 2.5rem;
}

.btn-primary, .btn-secondary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 52px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.3);
}

.btn-secondary {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  color: #64748b;
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1);
}

.error-message {
  background: #fff0f0;
  color: #ef4444;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #fecaca;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.additional-links {
  margin-top: 2rem;
  text-align: center;
}

.additional-links a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.additional-links a:hover {
  color: #8b5cf6;
  transform: translateX(4px);
}

@media (max-width: 480px) {
  .login-container {
    padding: 40px 24px;
    border-radius: 20px;
  }

  .title-text {
    font-size: 2rem;
  }

  .form-control {
    height: 52px;
    padding: 22px 44px 6px;
  }

  .floating-label {
    left: 44px;
  }

  .icon {
    left: 16px;
    font-size: 16px;
  }

  .button-group {
    grid-template-columns: 1fr;
  }
}
</style>