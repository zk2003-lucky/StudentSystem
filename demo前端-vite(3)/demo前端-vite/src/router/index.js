import { createRouter, createWebHistory } from 'vue-router';
import ClubList from '../views/ClubList.vue';
import ClubForm from '../views/ClubForm.vue';
import MemberList from '../views/MemberList.vue';
import MemberForm from '../views/MemberForm.vue';
import Login from '../views/Login.vue';
import Register from "../views/Register.vue";

const routes = [
  { path: '/clubs', name: 'ClubList', component: ClubList, meta: { requiresAuth: true } },
  { path: '/clubs/new', name: 'ClubFormAdd', component: ClubForm, meta: { requiresAuth: true } },
  { path: '/clubs/:id/edit', name: 'ClubFormEdit', component: ClubForm, meta: { requiresAuth: true } },
  { path: '/members', name: 'MemberList', component: MemberList, meta: { requiresAuth: true } },
  { path: '/members/new', name: 'MemberFormAdd', component: MemberForm, meta: { requiresAuth: true } },
  { path: '/members/:id/edit', name: 'MemberFormEdit', component: MemberForm, meta: { requiresAuth: true } },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register,meta: {requiresAuth: false}},
  { path: '/', redirect: '/clubs', meta: { requiresAuth: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null;
  const authRequired = to.matched.some(record => record.meta.requiresAuth);

  if (authRequired && !isAuthenticated) {
    next({
      name: 'Login',
      query: { redirect: to.fullPath }
    });
  } else if (to.name === 'Login' && isAuthenticated) {
    next('/');
  } else {
    next();
  }
});



export default router;