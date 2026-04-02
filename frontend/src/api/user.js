import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function getUserRoles(id) {
  return request({
    url: `/users/${id}/roles`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function assignUserRoles(id, roleIds) {
  return request({
    url: `/users/${id}/roles`,
    method: 'put',
    data: { roleIds }
  })
}

export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}
