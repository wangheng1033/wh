<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
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
      
      <el-table :data="userList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
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
            <el-button type="primary" link @click="handleAssignRole(row)">分配角色</el-button>
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
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" :placeholder="currentUserId ? '不修改请留空' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
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
    
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-transfer
        v-model="selectedRoles"
        :data="allRoles"
        :titles="['可选角色', '已选角色']"
        :props="{
          key: 'id',
          label: 'roleName'
        }"
      />
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, getUserRoles, assignUserRoles } from '@/api/user'
import { getAllRoles } from '@/api/role'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleDialogVisible = ref(false)
const formRef = ref(null)
const currentUserId = ref(null)
const allRoles = ref([])
const selectedRoles = ref([])

const queryParams = reactive({
  username: '',
  status: null,
  page: 1,
  size: 10
})

const formData = reactive({
  username: '',
  password: '',
  email: '',
  phone: '',
  status: 1
})

const formRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度必须在3-50之间', trigger: 'blur' }
  ],
  password: currentUserId.value 
    ? [] 
    : [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 100, message: '密码长度必须在6-100之间', trigger: 'blur' }
      ]
}))

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchAllRoles = async () => {
  const res = await getAllRoles()
  allRoles.value = res.data
}

const handleSearch = () => {
  queryParams.page = 1
  fetchUserList()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.status = null
  queryParams.page = 1
  fetchUserList()
}

const handleSizeChange = () => {
  queryParams.page = 1
  fetchUserList()
}

const handlePageChange = () => {
  fetchUserList()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  currentUserId.value = null
  Object.assign(formData, {
    username: '',
    password: '',
    email: '',
    phone: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  currentUserId.value = row.id
  Object.assign(formData, {
    username: row.username,
    password: '',
    email: row.email,
    phone: row.phone,
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  const submitData = { ...formData }
  if (currentUserId.value && !submitData.password) {
    delete submitData.password
  }
  
  if (currentUserId.value) {
    await updateUser(currentUserId.value, submitData)
    ElMessage.success('更新成功')
  } else {
    await createUser(submitData)
    ElMessage.success('创建成功')
  }
  
  dialogVisible.value = false
  fetchUserList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUserList()
  })
}

const handleAssignRole = async (row) => {
  currentUserId.value = row.id
  const res = await getUserRoles(row.id)
  selectedRoles.value = res.data
  roleDialogVisible.value = true
}

const handleSaveRoles = async () => {
  await assignUserRoles(currentUserId.value, selectedRoles.value)
  ElMessage.success('分配成功')
  roleDialogVisible.value = false
}

onMounted(() => {
  fetchUserList()
  fetchAllRoles()
})
</script>

<style scoped>
.user-container {
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
