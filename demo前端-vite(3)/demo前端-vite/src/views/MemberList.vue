<!-- MemberList.vue -->
<template>
  <div class="list-container">
    <h1 class="list-title">成员列表</h1>

    <div class="list-header">
      <button @click="addMember" class="btn-add">
        <span>+ 添加成员</span>
      </button>
    </div>

    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <div class="card-list">
      <div v-for="member in members" :key="member.id" class="member-card">
        <div class="card-content">
          <h3 class="member-name">{{ member.name }}</h3>
          <p class="club-id">社团ID: {{ member.clubId }}</p>
        </div>

        <div class="card-actions">
          <button @click="editMember(member.id)" class="btn-edit">编辑</button>
          <button @click="deleteMemberHandler(member.id)" class="btn-delete">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { getMembers, deleteMember } from '../services/memberService';

const router = useRouter();
const { start: startLoading, stop: stopLoading } = inject('globalLoading');
const members = ref([]);
const errorMessage = ref('');

onMounted(async () => {
  try {
    startLoading();
    members.value = await getMembers();
  } catch (error) {
    errorMessage.value = '获取成员列表失败';
  } finally {
    stopLoading();
  }
});

const editMember = (id) => router.push({ name: 'MemberFormEdit', params: { id } });
const addMember = () => router.push({ name: 'MemberFormAdd' });

const deleteMemberHandler = async (id) => {
  if (!confirm('确定要删除该成员吗？')) return;

  try {
    await deleteMember(id);
    members.value = members.value.filter(m => m.id !== id);
  } catch (error) {
    errorMessage.value = '删除成员失败';
  }
};
</script>

<style scoped>
.list-container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.list-title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 2rem;
}

.list-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1.5rem;
}

.btn-add {
  background: #2ecc71;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-add:hover {
  background: #27ae60;
}

.card-list {
  display: grid;
  gap: 1rem;
}

.member-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  flex-grow: 1;
}

.member-name {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.club-id {
  margin: 0.5rem 0 0;
  color: #7f8c8d;
}

.card-actions {
  display: flex;
  gap: 1rem;
}

.btn-edit {
  background: #3498db;
  color: white;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-delete {
  background: #e74c3c;
  color: white;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.error-message {
  color: #e74c3c;
  padding: 1rem;
  background: #f8d7da;
  border-radius: 4px;
  margin: 1rem 0;
}
</style>