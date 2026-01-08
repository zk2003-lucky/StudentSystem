<template>
  <div class="form-container">
    <h1 class="form-title">{{ id ? '编辑成员' : '添加成员' }}</h1>

    <div v-if="errorMessage" class="error-message">
      <span>{{ errorMessage }}</span>
    </div>

    <form @submit.prevent="submitForm" class="form-content">
      <div class="form-group">
        <label for="name" class="form-label">姓名:</label>
        <input
            v-model.trim="member.name"
            id="name"
            class="form-input"
            :class="{ 'input-error': nameError }"
            required
            placeholder="请输入成员姓名"
            @input="validateName"
        >
        <div v-if="nameError" class="error-hint">{{ nameError }}</div>
      </div>

      <div class="form-group">
        <label for="clubId" class="form-label">社团ID:</label>
        <input
            v-model.number="member.clubId"
            id="clubId"
            class="form-input"
            :class="{ 'input-error': clubIdError }"
            type="number"
            min="1"
            required
            placeholder="请输入有效社团ID"
            @input="validateClubId"
        >
        <div v-if="clubIdError" class="error-hint">{{ clubIdError }}</div>
      </div>

      <div class="form-actions">
        <button
            type="submit"
            class="btn-submit"
            :disabled="isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '提交' }}
        </button>
        <button
            type="button"
            @click="cancelForm"
            class="btn-cancel"
        >
          取消
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getMemberById, createMember, updateMember } from '../services/memberService';

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const member = ref({ name: '', clubId: '' });
const errorMessage = ref('');
const isSubmitting = ref(false);
const nameError = ref('');
const clubIdError = ref('');

onMounted(async () => {
  try {
    if (id) {
      const data = await getMemberById(id);
      member.value = { ...data };
    }
  } catch (error) {
    errorMessage.value = '获取成员信息失败，请刷新重试';
  }
});

const validateName = () => {
  if (!member.value.name.trim()) {
    nameError.value = '姓名不能为空';
    return false;
  }
  if (member.value.name.length > 20) {
    nameError.value = '姓名不能超过20个字符';
    return false;
  }
  nameError.value = '';
  return true;
};

const validateClubId = () => {
  if (!member.value.clubId || member.value.clubId <= 0) {
    clubIdError.value = '请输入有效的社团ID';
    return false;
  }
  clubIdError.value = '';
  return true;
};

const validateForm = () => {
  const validName = validateName();
  const validClubId = validateClubId();
  return validName && validClubId;
};

const submitForm = async () => {
  if (!validateForm()) return;

  try {
    isSubmitting.value = true;
    if (id) {
      await updateMember({ ...member.value });
    } else {
      await createMember({ ...member.value });
    }
    router.push({ name: 'MemberList' });
  } catch (error) {
    errorMessage.value = id ? '更新失败: ' : '创建失败: ';
    errorMessage.value += error.message || '请检查网络连接';
  } finally {
    isSubmitting.value = false;
  }
};

const cancelForm = () => {
  router.go(-1);
};
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  color: #2c3e50;
  font-size: 1.8rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f2f5;
}

.form-content {
  display: grid;
  gap: 1.5rem;
}

.form-group {
  position: relative;
  display: grid;
  gap: 0.8rem;
}

.form-label {
  font-weight: 500;
  color: #4a5568;
  font-size: 0.95rem;
}

.form-input {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

.form-input.input-error {
  border-color: #e74c3c;
  background-color: #fff5f5;
}

.form-input.input-error:focus {
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1);
}

.error-hint {
  color: #e74c3c;
  font-size: 0.85rem;
  margin-top: 0.3rem;
}

.error-message {
  background: #fee2e2;
  color: #dc2626;
  padding: 1rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  border: 1px solid #fecaca;
}

.error-message span {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.btn-submit,
.btn-cancel {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-submit {
  background: #3498db;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #2980b9;
}

.btn-submit:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  opacity: 0.8;
}

.btn-cancel {
  background: #f0f2f5;
  color: #4a5568;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

@media (max-width: 640px) {
  .form-container {
    margin: 1rem;
    padding: 1.5rem;
  }

  .form-title {
    font-size: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
    gap: 0.8rem;
  }

  .btn-submit,
  .btn-cancel {
    width: 100%;
    justify-content: center;
  }
}
</style>