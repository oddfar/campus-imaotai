package com.oddfar.campus.common.core.page;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.utils.sql.SqlUtil;

import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        startPage(pageDomain);
    }

    /**
     * 设置请求分页数据
     */
    public static void startPage(PageDomain pageDomain) {
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 设置请求分页数据
     */
    public static void startPage(Integer pageSize) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        pageDomain.setPageSize(pageSize);
        startPage(pageDomain);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }

    /**
     * 获取分页数据
     * 需要在使用 @startPage 之后获取
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static PageResult getPageResult(List<?> list) {
        long total = new PageInfo(list).getTotal();
        PageResult<?> pageResult = new PageResult<>(list, total);

        return pageResult;
    }
}
