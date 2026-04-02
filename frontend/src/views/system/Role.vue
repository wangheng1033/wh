<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
      
      <el-table :data="roleList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignPermission(row)">分配权限</el-button>
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
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入描述" />
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
    
    <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="treeRef"
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        show-checkbox
        node-key="id"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions } from '@/api/role'
import { getAllPermissions } from '@/api/permission'

const loading = ref(false)
const roleList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const permissionDialogVisible = ref(false)
const formRef = ref(null)
const treeRef = ref(null)
const currentRoleId = ref(null)
const permissionTree = ref([])

const queryParams = reactive({
  roleName: '',
  status: null,
  page: 1,
  size: 10
})

const formData = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const fetchRoleList = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    roleList.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const buildPermissionTree = (permissions) => {
  const map = {}
  const roots = []
  
  permissions.forEach(p => {
    map[p.id] = { ...p, children: [] }
  })
  
  permissions.forEach(p => {
    if (p.parentId === 0) {
      roots.push(map[p.id])
    } else if (map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    }
  })
  
  return roots
}

const fetchAllPermissions = async () => {
  const res = await getAllPermissions()
  permissionTree.value = buildPermissionTree(res.data)
}

const handleSearch = () => {
  queryParams.page = 1
  fetchRoleList()
}

const handleReset = () => {
  queryParams.roleName = ''
  queryParams.status = null
  queryParams.page = 1
  fetchRoleList()
}

const handleSizeChange = () => {
  queryParams.page = 1
  fetchRoleList()
}

const handlePageChange = () => {
  fetchRoleList()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  currentRoleId.value = null
  Object.assign(formData, {
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  currentRoleId.value = row.id
  Object.assign(formData, {
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description,
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  if (currentRoleId.value) {
    await updateRole(currentRoleId.value, formData)
    ElMessage.success('更新成功')
  } else {
    await createRole(formData)
    ElMessage.success('创建成功')
  }
  
  dialogVisible.value = false
  fetchRoleList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchRoleList()
  })
}

const handleAssignPermission = async (row) => {
  currentRoleId.value = row.id
  const res = await getRolePermissions(row.id)
  treeRef.value?.setCheckedKeys(res.data)
  permissionDialogVisible.value = true
}

const handleSavePermissions = async () => {
  const permissionIds = treeRef.value?.getCheckedKeys()
  await assignRolePermissions(currentRoleId.value, permissionIds)
  ElMessage.success('分配成功')
  permissionDialogVisible.value = false
}

onMounted(() => {
  fetchRoleList()
  fetchAllPermissions()
})
</script>

<style scoped>
.role-container {
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
