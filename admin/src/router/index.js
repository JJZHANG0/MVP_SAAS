import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'shops',
        name: 'Shops',
        component: () => import('../views/Shops.vue')
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/Products.vue')
      },
      {
        path: 'manuals',
        name: 'Manuals',
        component: () => import('../views/Manuals.vue')
      },
      {
        path: 'sessions',
        name: 'Sessions',
        component: () => import('../views/Sessions.vue')
      },
      {
        path: 'sessions/:id',
        name: 'SessionDetail',
        component: () => import('../views/SessionDetail.vue')
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: () => import('../views/Knowledge.vue')
      },
      {
        path: 'ai-usage',
        name: 'AIUsage',
        component: () => import('../views/AIUsage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
