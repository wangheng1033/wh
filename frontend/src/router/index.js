import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/system/user',
    children: [
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: '/system/permission',
        name: 'Permission',
        component: () => import('@/views/system/Permission.vue'),
        meta: { title: '权限管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
