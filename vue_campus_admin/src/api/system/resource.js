import request from '@/utils/request'


// 根据角色ID查询资源下拉树结构
export function roleResourceTreeselect(roleId) {
    return request({
        url: '/system/resource/roleApiTreeselect/' + roleId,
        method: 'get'
    })
}
//修改角色与资源关联
export function editRoleResource(data) {

    return request({
        url: '/system/resource/roleApi',
        method: 'put',
        params: data
    })
}