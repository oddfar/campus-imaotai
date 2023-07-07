import request from '@/utils/request'

// 查询I茅台预约商品列列表
export function listItem() {
    return request({
        url: '/imt/item/list',
        method: 'get',
    })
}



// 刷新i茅台预约商品列表
export function refreshItem(itemId) {
    return request({
        url: '/imt/item/refresh',
        method: 'get'
    })
}
