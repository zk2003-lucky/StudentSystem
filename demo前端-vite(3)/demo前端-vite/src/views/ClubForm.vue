<!-- ClubForm.vue -->
<template>
  <div class="form-container">
    <h1 class="form-title">{{ id ? '编辑社团' : '添加社团' }}</h1>

    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <form @submit.prevent="submitForm" class="form-content">
      <div class="form-group">
        <label for="name" class="form-label">名称:</label>
        <input
            v-model="club.name"
            id="name"
            class="form-input"
            required
            placeholder="请输入社团名称"
        >
      </div>

      <div class="form-group">
        <label for="description" class="form-label">描述:</label>
        <textarea
            v-model="club.description"
            id="description"
            class="form-textarea"
            required
            placeholder="请输入社团描述"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="logo" class="form-label">Logo:</label>
        <div class="file-upload">
          <input
              type="file"
              @change="handleFileUpload"
              id="logo"
              accept="image/*"
              class="file-input"
          >
          <label for="logo" class="file-label">选择文件</label>
          <span class="file-name">{{ selectedFile ? selectedFile.name : '未选择文件' }}</span>
        </div>
      </div>

      <div class="preview-area" v-if="imagePreviewUrl">
        <img :src="imagePreviewUrl" alt="Logo预览" class="preview-image">
      </div>

      <div class="form-actions">
        <button type="submit" class="btn-submit">提交</button>
        <button type="button" @click="cancelForm" class="btn-cancel">取消</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getClubById, createClub, updateClub, uploadLogo, downloadLogo } from '../services/clubService';

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const club = ref({ name: '', description: '' });
const selectedFile = ref(null);
const imagePreviewUrl = ref('');
const logoEdit = ref(false);
const errorMessage = ref('');

onMounted(async () => {
  try {
    if (id) {
      club.value = await getClubById(id);
      const file = await downloadLogo(id);
      selectedFile.value = file;
      imagePreviewUrl.value = URL.createObjectURL(file);
    }
  } catch (error) {
    errorMessage.value = '获取社团信息失败';
  }
});

const handleFileUpload = (event) => {
  selectedFile.value = event.target.files[0];
  logoEdit.value = true;

  if (imagePreviewUrl.value) {
    URL.revokeObjectURL(imagePreviewUrl.value);
  }
  imagePreviewUrl.value = URL.createObjectURL(selectedFile.value);
};

const submitForm = async () => {
  try {
    if (id) {
      await updateClub(club.value);
      if (logoEdit.value) {
        await uploadLogo(id, selectedFile.value);
      }
    } else {
      const newClub = await createClub(club.value);
      if (logoEdit.value) {
        await uploadLogo(newClub.id, selectedFile.value);
      }
    }
    router.push({ name: 'ClubList' });
  } catch (error) {
    errorMessage.value = id ? '更新失败' : '创建失败';
  }
};

const cancelForm = () => {
  router.go(-1);
};
</script>

<style scoped>
.form-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.form-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 2rem;
}

.form-content {
  display: grid;
  gap: 1.5rem;
}

.form-group {
  display: grid;
  gap: 0.5rem;
}

.form-label {
  font-weight: 500;
  color: #34495e;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #bdc3c7;
  border-radius: 4px;
  font-size: 1rem;
}

.form-textarea {
  height: 120px;
  resize: vertical;
}

.file-upload {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.file-input {
  display: none;
}

.file-label {
  background: #3498db;
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.file-label:hover {
  background: #2980b9;
}

.file-name {
  color: #7f8c8d;
}

.preview-area {
  text-align: center;
  margin: 1rem 0;
}

.preview-image {
  max-width: 200px;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-submit {
  background: #2ecc71;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-submit:hover {
  background: #27ae60;
}

.btn-cancel {
  background: #95a5a6;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-cancel:hover {
  background: #7f8c8d;
}

.error-message {
  color: #e74c3c;
  padding: 1rem;
  background: #f8d7da;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}
</style>