import request from '@/utils/request'


// reservation
export function reservation(mobile) {
    return request({
        url: '/imt/user/reservation',
        method: 'get',
        params: { mobile }
    })
}
//travelReward
export function travelReward(mobile) {
    return request({
        url: '/imt/user/travelReward',
        method: 'get',
        params: { mobile }
    })
}


// 发送验证码
export function sendCode(mobile, deviceId) {
    return request({
        url: '/imt/user/sendCode',
        method: 'get',
        params: { mobile: mobile, deviceId: deviceId }
    })
}

// 查询I茅台用户列表
export function login(mobile, code, deviceId) {
    return request({
        url: '/imt/user/login',
        method: 'get',
        params: { mobile: mobile, code: code, deviceId: deviceId }
    })
}

// 查询I茅台用户列表
export function listUser(query) {
    return request({
        url: '/imt/user/list',
        method: 'get',
        params: query
    })
}

// 查询I茅台用户详细
export function getUser(mobile) {
    return request({
        url: '/imt/user/' + mobile,
        method: 'get'
    })
}

// 新增I茅台用户
export function addUser(data) {
    return request({
        url: '/imt/user',
        method: 'post',
        data: data
    })
}

// 修改I茅台用户
export function updateUser(data) {
    return request({
        url: '/imt/user',
        method: 'put',
        data: data
    })
}

// 删除I茅台用户
export function delUser(mobile) {
    return request({
        url: '/imt/user/' + mobile,
        method: 'delete'
    })
}
