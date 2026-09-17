import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/diagnosis/:sessionUuid',
    name: 'Diagnosis',
    component: () => import('../views/Diagnosis.vue')
  },
  {
    path: '/guide/:slug',
    name: 'Guide',
    component: () => import('../views/Guide.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
