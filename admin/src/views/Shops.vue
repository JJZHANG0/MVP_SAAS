<template>
  <div>
    <el-card class="card-shadow">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: 600;">店铺列表</span>
          <el-button type="primary" :icon="Plus" @click="showConnectDialog = true">
            连接新店铺
          </el-button>
        </div>
      </template>
      
      <el-table :data="shops" style="width: 100%">
        <el-table-column prop="shopName" label="店铺名称" />
        <el-table-column prop="shopifyDomain" label="域名" />
        <el-table-column prop="platform" label="平台" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'info'">
              {{ row.active ? '活跃' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerEmail" label="所有者邮箱" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="syncOrders(row)">
              同步订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="showConnectDialog" title="连接Shopify店铺" width="500px">
      <el-form :model="connectForm" label-width="100px">
        <el-form-item label="店铺域名">
          <el-input v-model="connectForm.shopDomain" placeholder="yourshop.myshopify.com" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showConnectDialog = false">取消</el-button>
        <el-button type="primary" @click="connectShop" :loading="connecting">
          连接
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '../api'

const shops = ref([])
const showConnectDialog = ref(false)
const connecting = ref(false)
const connectForm = ref({
  shopDomain: ''
})

const loadShops = async () => {
  try {
    const response = await api.shops.getAll()
    if (response.success) {
      shops.value = response.data || []
    }
  } catch (error) {
    ElMessage.error('加载店铺失败')
  }
}

const connectShop = async () => {
  if (!connectForm.value.shopDomain) {
    ElMessage.warning('请输入店铺域名')
    return
  }
  
  connecting.value = true
  try {
    const response = await api.shops.connect(connectForm.value.shopDomain)
    if (response.success) {
      await api.shops.callback({
        shop: connectForm.value.shopDomain,
        code: 'mock_code_' + Date.now(),
        state: 'mock_state'
      })
      
      ElMessage.success('店铺连接成功')
      showConnectDialog.value = false
      connectForm.value.shopDomain = ''
      loadShops()
    }
  } catch (error) {
    ElMessage.error('连接失败')
  } finally {
    connecting.value = false
  }
}

const syncOrders = async (shop) => {
  try {
    await api.shops.syncOrders(shop.id)
    ElMessage.success('订单同步已开始')
  } catch (error) {
    ElMessage.error('同步失败')
  }
}

onMounted(() => {
  loadShops()
})
</script>
