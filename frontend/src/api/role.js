import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

export function getAllRoles() {
  return request({
    url: '/roles/all',
    method: 'get'
  })
}

export function getRoleById(id) {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

export function getRolePermissions(id) {
  return request({
    url: `/roles/${id}/permissions`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

export function assignRolePermissions(id, permissionIds) {
  return request({
    url: `/roles/${id}/permissions`,
    method: 'put',
    data: { permissionIds }
  })
}

export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}
