<template>
  <div class="permission-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增权限
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="权限名称">
          <el-input v-model="queryParams.permissionName" placeholder="请输入权限名称" clearable />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="queryParams.resourceType" placeholder="请选择资源类型" clearable>
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="接口" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="permissionList" border stripe v-loading="loading" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="permissionName" label="权限名称" width="150" />
        <el-table-column prop="permissionCode" label="权限编码" width="180" />
        <el-table-column prop="resourceType" label="资源类型" width="100">
          <template #default="{ row }">
            <el-tag :type="resourceTypeMap[row.resourceType]?.type">
              {{ resourceTypeMap[row.resourceType]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" width="150" />
        <el-table-column prop="component" label="组件" width="150" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="formData.permissionCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="formData.resourceType" placeholder="请选择资源类型">
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="接口" value="api" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="permissionTreeData"
            :props="{ label: 'permissionName', value: 'id' }"
            placeholder="请选择父级权限"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="formData.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item label="组件" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入图标" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionList, createPermission, updatePermission, deletePermission, getAllPermissions } from '@/api/permission'

const loading = ref(false)
const permissionList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const currentPermissionId = ref(null)
const allPermissions = ref([])

const queryParams = reactive({
  permissionName: '',
  resourceType: null,
  page: 1,
  size: 10
})

const formData = reactive({
  permissionName: '',
  permissionCode: '',
  resourceType: 'menu',
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

const rules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }]
}

const resourceTypeMap = {
  menu: { label: '菜单', type: 'primary' },
  button: { label: '按钮', type: 'success' },
  api: { label: '接口', type: 'warning' }
}

const permissionTreeData = computed(() => {
  const root = { id: 0, permissionName: '根节点', children: [] }
  const map = { 0: root }
  
  allPermissions.value.forEach(p => {
    map[p.id] = { ...p, children: [] }
  })
  
  allPermissions.value.forEach(p => {
    if (map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    }
  })
  
  return [root]
})

const fetchPermissionList = async () => {
  loading.value = true
  try {
    const res = await getPermissionList(queryParams)
    permissionList.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchAllPermissions = async () => {
  const res = await getAllPermissions()
  allPermissions.value = res.data
}

const handleSearch = () => {
  queryParams.page = 1
  fetchPermissionList()
}

const handleReset = () => {
  queryParams.permissionName = ''
  queryParams.resourceType = null
  queryParams.page = 1
  fetchPermissionList()
}

const handleSizeChange = () => {
  queryParams.page = 1
  fetchPermissionList()
}

const handlePageChange = () => {
  fetchPermissionList()
}

const handleAdd = () => {
  dialogTitle.value = '新增权限'
  currentPermissionId.value = null
  Object.assign(formData, {
    permissionName: '',
    permissionCode: '',
    resourceType: 'menu',
    parentId: 0,
    path: '',
    component: '',
    icon: '',
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑权限'
  currentPermissionId.value = row.id
  Object.assign(formData, {
    permissionName: row.permissionName,
    permissionCode: row.permissionCode,
    resourceType: row.resourceType,
    parentId: row.parentId,
    path: row.path,
    component: row.component,
    icon: row.icon,
    sortOrder: row.sortOrder,
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  if (currentPermissionId.value) {
    await updatePermission(currentPermissionId.value, formData)
    ElMessage.success('更新成功')
  } else {
    await createPermission(formData)
    ElMessage.success('创建成功')
  }
  
  dialogVisible.value = false
  fetchPermissionList()
  fetchAllPermissions()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    fetchPermissionList()
    fetchAllPermissions()
  })
}

onMounted(() => {
  fetchPermissionList()
  fetchAllPermissions()
})
</script>

<style scoped>
.permission-container {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
