<template>
  <div class="club-list">
    <h1>社团列表</h1>
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    <div class="action-bar">
      <button @click="addClub" class="btn-primary">添加社团</button>
    </div>

    <div class="club-container">
      <div v-for="club in clubs" :key="club.id" class="club-card">
        <div class="logo-container">
          <img
              :src="logoUrls[club.id] || '/default-logo.png'"
              alt="社团logo"
              class="club-logo"
              @error="handleImageError"
          />
          <button
              class="btn-download"
              @click.stop="handleDownloadLogo(club.id)"
              title="下载Logo"
          >
            ⬇️
          </button>
        </div>
        <div class="club-info">
          <h3>{{ club.name }}</h3>
          <p>{{ club.description }}</p>
        </div>
        <div class="club-actions">
          <button @click="editClub(club.id)" class="btn-edit">编辑</button>
          <button @click="deleteClubHandler(club.id)" class="btn-delete">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { getClubs, deleteClub, downloadLogo } from '../services/clubService';

const router = useRouter();
const { start: startLoading, stop: stopLoading } = inject('globalLoading');
const clubs = ref([]);
const logoUrls = ref({});
const errorMessage = ref('');

// 初始化加载数据
onMounted(async () => {
  try {
    startLoading();
    clubs.value = await getClubs();
    await Promise.all(clubs.value.map(async club => {
      try {
        const file = await downloadLogo(club.id);
        logoUrls.value[club.id] = URL.createObjectURL(file);
      } catch (error) {
        logoUrls.value[club.id] = '';
      }
    }));
  } catch (error) {
    errorMessage.value = error.message;
  } finally {
    stopLoading();
  }
});

// 图片下载功能（已修改）
const handleDownloadLogo = async (clubId) => {
  try {
    startLoading();
    const file = await downloadLogo(clubId);
    const url = URL.createObjectURL(file);

    // 创建隐藏下载链接
    const link = document.createElement('a');
    link.href = url;
    const filename = clubs.value.find(c => c.id === clubId)?.name || clubId;
    const fileExtension = file.type.split('/')[1] || 'png';
    link.download = `${filename}_logo.${fileExtension}`;
    document.body.appendChild(link);
    link.click();

    // 清理资源
    URL.revokeObjectURL(url);
    document.body.removeChild(link);
  } catch (error) {
    errorMessage.value = `下载失败: ${error.message}`;
  } finally {
    stopLoading();
  }
};

// 原有功能保持不变
const handleImageError = (e) => {
  e.target.src = '/default-logo.png';
  e.target.style.objectFit = 'cover';
};

const editClub = (id) => router.push({ name: 'ClubFormEdit', params: { id } });
const addClub = () => router.push({ name: 'ClubFormAdd' });

const deleteClubHandler = async (id) => {
  if (!confirm('确定要删除这个社团吗？')) return;

  try {
    await deleteClub(id);
    clubs.value = clubs.value.filter(c => c.id !== id);
    if (logoUrls.value[id]) {
      URL.revokeObjectURL(logoUrls.value[id]);
      delete logoUrls.value[id];
    }
  } catch (error) {
    errorMessage.value = '删除社团失败';
  }
};
</script>

<style scoped>
/* 保持原有样式不变 */
.club-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.club-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.club-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: transform 0.2s ease;
}

.club-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.logo-container {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.club-logo {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.club-logo:hover {
  transform: scale(1.05);
}

.btn-download {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 2;
}

.btn-download:hover {
  background: rgba(0, 0, 0, 0.9);
  transform: scale(1.1);
}

.club-info {
  flex-grow: 1;
  margin-bottom: 10px;
}

.club-info h3 {
  margin: 0 0 8px;
  color: #333;
}

.club-info p {
  margin: 0;
  color: #666;
  font-size: 0.9em;
  line-height: 1.4;
}

.club-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.btn-primary {
  background-color: #3498db;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-edit {
  background-color: #2ecc71;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-delete {
  background-color: #e74c3c;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

button:hover {
  opacity: 0.9;
}

.error-message {
  color: #e74c3c;
  padding: 10px;
  margin-bottom: 20px;
  background-color: #f8d7da;
  border-radius: 4px;
  border: 1px solid #f5c6cb;
}

@media (max-width: 768px) {
  .club-container {
    grid-template-columns: 1fr;
  }

  .club-card {
    margin-bottom: 15px;
  }

  .btn-download {
    width: 40px;
    height: 40px;
  }
}
</style>