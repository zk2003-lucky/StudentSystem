<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../services/authService'

const router = useRouter()
const canvas = ref(null)
let particles = []
let mouse = { x: null, y: null }
let animationFrame = null
let ctx = null

// 表单数据
const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

// 错误信息
const errors = ref({
  username: '',
  password: '',
  confirmPassword: '',
  form: ''
})

const isLoading = ref(false)

// 表单验证规则
const validate = {
  username: value => {
    if (!value) return '用户名不能为空'
    if (value.length < 4) return '用户名至少4个字符'
    if (!/^[a-zA-Z0-9_]+$/.test(value)) return '只能包含字母、数字和下划线'
    return ''
  },
  password: value => {
    if (!value) return '密码不能为空'
    if (value.length < 6) return '密码至少6个字符'
    return ''
  },
  confirmPassword: (value, password) => {
    if (!value) return '请确认密码'
    if (value !== password) return '两次密码不一致'
    return ''
  }
}

// 字段验证
const validateField = (field) => {
  let error = ''
  switch(field) {
    case 'username':
      error = validate.username(form.value.username)
      break
    case 'password':
      error = validate.password(form.value.password)
      break
    case 'confirmPassword':
      error = validate.confirmPassword(form.value.confirmPassword, form.value.password)
      break
  }
  errors.value[field] = error
  return !error
}

// 提交处理
const handleSubmit = async () => {
  // 重置错误状态
  errors.value = {
    username: '',
    password: '',
    confirmPassword: '',
    form: ''
  };

  // 执行验证
  const isValid = ['username', 'password', 'confirmPassword']
      .map(field => validateField(field))
      .every(valid => valid);

  if (!isValid) return;

  try {
    isLoading.value = true;
    await register(form.value);

    await router.push({
      path: '/login',
      query: { registered: 'true' }
    });

  } catch (error) {
    // 增强错误处理
    const errorMessage = error.message;

    // 匹配特定错误类型
    if (errorMessage.includes('密码')) {
      errors.value.password = errorMessage;
    } else if (errorMessage.includes('用户')) {
      errors.value.username = errorMessage;
    } else {
      errors.value.form = errorMessage;
    }

    // 自动清除错误
    setTimeout(() => {
      errors.value.form = '';
    }, 5000);

  } finally {
    isLoading.value = false;
  }
}

// 粒子类
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

  update() {
    if (this.x + this.radius > this.canvas.width || this.x - this.radius < 0) {
      this.velocity.x = -this.velocity.x
    }
    if (this.y + this.radius > this.canvas.height || this.y - this.radius < 0) {
      this.velocity.y = -this.velocity.y
    }

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

// 初始化画布
const initCanvas = () => {
  canvas.value.width = window.innerWidth
  canvas.value.height = window.innerHeight
  ctx = canvas.value.getContext('2d')

  // 创建粒子
  particles = Array.from({ length: 100 }, () => new Particle(canvas.value, ctx))

  // 鼠标移动监听
  window.addEventListener('mousemove', e => {
    mouse.x = e.clientX
    mouse.y = e.clientY
  })

  // 动画循环
  const animate = () => {
    ctx.clearRect(0, 0, canvas.value.width, canvas.value.height)
    particles.forEach(particle => particle.update())
    animationFrame = requestAnimationFrame(animate)
  }
  animate()
}

onMounted(() => {
  initCanvas()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationFrame)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', () => {})
})

const handleResize = () => {
  canvas.value.width = window.innerWidth
  canvas.value.height = window.innerHeight
}
</script>

<template>
  <div class="auth-wrapper">
    <canvas ref="canvas" class="particles-background"></canvas>

    <div class="auth-container">
      <div class="auth-content">
        <h1 class="title">
          <span class="title-text">用户注册</span>
          <div class="title-underline"></div>
        </h1>

        <div v-if="errors.form" class="error-message">
          <i class="fas fa-exclamation-triangle"></i>
          {{ errors.form }}
        </div>

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <i class="icon fas fa-user"></i>
            <input
                v-model="form.username"
                placeholder=" "
                class="form-control"
                :class="{ error: errors.username }"
                @blur="validateField('username')"
            />
            <label class="floating-label">用户名</label>
            <div v-if="errors.username" class="field-error">{{ errors.username }}</div>
          </div>

          <div class="form-group">
            <i class="icon fas fa-lock"></i>
            <input
                v-model="form.password"
                type="password"
                placeholder=" "
                class="form-control"
                :class="{ error: errors.password }"
                @blur="validateField('password')"
            />
            <label class="floating-label">密码</label>
            <div v-if="errors.password" class="field-error">{{ errors.password }}</div>
          </div>

          <div class="form-group">
            <i class="icon fas fa-lock"></i>
            <input
                v-model="form.confirmPassword"
                type="password"
                placeholder=" "
                class="form-control"
                :class="{ error: errors.confirmPassword }"
                @blur="validateField('confirmPassword')"
            />
            <label class="floating-label">确认密码</label>
            <div v-if="errors.confirmPassword" class="field-error">
              {{ errors.confirmPassword }}
            </div>
          </div>

          <button
              type="submit"
              class="btn-primary"
              :disabled="isLoading"
          >
            <i v-if="!isLoading" class="fas fa-user-plus"></i>
            <i v-else class="fas fa-spinner fa-spin"></i>
            {{ isLoading ? '注册中...' : '立即注册' }}
          </button>
        </form>

        <div class="auth-links">
          <router-link to="/login">
            <i class="fas fa-arrow-left"></i>
            返回登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 新增全局盒模型设置 */
*, *::before, *::after {
  box-sizing: border-box;
}

.auth-wrapper {
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

.auth-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 440px;
  padding: 30px 25px;
  margin: 0 auto;
}

.auth-content {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 40px 35px;
}

.title {
  text-align: center;
  margin-bottom: 2rem;
}

.title-text {
  display: inline-block;
  color: #1f2937;
  font-size: 2.2rem;
  font-weight: 600;
}

.title-underline {
  width: 60px;
  height: 4px;
  background: #3498db;
  margin: 16px auto;
  border-radius: 2px;
}

.form-group {
  position: relative;
  margin-bottom: 1.5rem;
}

.form-control {
  width: 100%;
  padding: 14px 45px 14px 50px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: transparent;
}

.form-control:focus {
  border-color: #3498db;
  box-shadow: 0 0 12px rgba(52, 152, 219, 0.15);
  outline: none;
}

.floating-label {
  position: absolute;
  left: 50px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  pointer-events: none;
  transition: all 0.3s ease;
  background: linear-gradient(transparent 50%, white 50%);
  padding: 0 8px;
}

.form-control:focus ~ .floating-label,
.form-control:not(:placeholder-shown) ~ .floating-label {
  transform: translateY(-34px) scale(0.9);
  color: #3498db;
  left: 42px;
}

.icon {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  z-index: 2;
}

.btn-primary {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 1rem;
}

.btn-primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.error-message {
  background: #fee2e2;
  color: #ef4444;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
}

.field-error {
  color: #ef4444;
  font-size: 0.9rem;
  margin-top: 6px;
  padding-left: 20px;
}

.auth-links {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.95rem;
}

.auth-links a {
  color: #3498db;
  text-decoration: none;
  transition: all 0.3s ease;
}

.auth-links a:hover {
  color: #2980b9;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .auth-container {
    padding: 20px 15px;
  }

  .auth-content {
    padding: 30px 25px;
  }

  .title-text {
    font-size: 2rem;
  }

  .form-control {
    padding: 12px 40px 12px 45px;
  }

  .floating-label {
    left: 45px;
  }

  .icon {
    left: 15px;
  }

  .form-control:focus ~ .floating-label,
  .form-control:not(:placeholder-shown) ~ .floating-label {
    left: 37px;
  }
}

@media (max-width: 375px) {
  .auth-content {
    padding: 25px 20px;
  }

  .form-control {
    font-size: 15px;
    padding: 10px 35px 10px 40px;
  }

  .floating-label {
    font-size: 14px;
    left: 40px;
  }

  .icon {
    left: 12px;
  }
}
/* 新增修复样式 */
.form-control {
  padding: 16px 45px 16px 50px; /* 增加上下padding */
}

.floating-label {
  left: 50px;
  top: 18px; /* 调整初始位置 */
  transform-origin: left center;
  background: white;
  padding: 0 8px;
  z-index: 1;
}

.form-control:focus ~ .floating-label,
.form-control:not(:placeholder-shown) ~ .floating-label {
  transform: translateY(-28px) scale(0.85);
  left: 42px;
  top: 10px;
}

.icon {
  left: 20px;
  z-index: 2;
}

/* 移动端适配调整 */
@media (max-width: 480px) {
  .form-control {
    padding: 14px 40px 14px 45px;
  }

  .floating-label {
    left: 45px;
    top: 16px;
  }

  .form-control:focus ~ .floating-label,
  .form-control:not(:placeholder-shown) ~ .floating-label {
    left: 37px;
    transform: translateY(-24px) scale(0.85);
  }

  .icon {
    left: 15px;
  }
}

@media (max-width: 375px) {
  .form-control {
    padding: 12px 35px 12px 40px;
    font-size: 15px;
  }

  .floating-label {
    left: 40px;
    font-size: 14px;
  }

  .form-control:focus ~ .floating-label,
  .form-control:not(:placeholder-shown) ~ .floating-label {
    left: 32px;
    transform: translateY(-20px) scale(0.8);
  }
}
</style>