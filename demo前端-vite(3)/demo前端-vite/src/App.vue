<!-- App.vue -->
<template>
  <div id="app">
    <nav v-if="isLoggedIn" class="main-nav">
      <router-link to="/clubs" class="nav-link">社团管理</router-link>
      <router-link to="/members" class="nav-link">成员管理</router-link>
      <button @click="handleLogout" class="logout-btn">退出登录</button>
    </nav>

    <div v-if="loading" class="loading-overlay">
      <div class="loader"></div>
    </div>
    <router-view></router-view>
  </div>
</template>

<script setup>
import { ref, provide, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(false);

const isLoggedIn = computed(() => localStorage.getItem('token') !== null);

provide('globalLoading', {
  start: () => loading.value = true,
  stop: () => loading.value = false
});

const handleLogout = () => {
  localStorage.removeItem('token');
  router.push('/login');
};

router.beforeEach(() => loading.value = true);
router.afterEach(() => loading.value = false);
</script>

<style>
/* 添加导航样式 */
.main-nav {
  background: #2c3e50;
  padding: 1rem;
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.nav-link:hover {
  background: #34495e;
}

.logout-btn {
  margin-left: auto;
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

/* 其他原有样式保持不变 */
</style>