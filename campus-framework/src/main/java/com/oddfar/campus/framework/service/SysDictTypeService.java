package com.oddfar.campus.framework.service;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysDictDataEntity;
import com.oddfar.campus.common.domain.entity.SysDictTypeEntity;

import java.util.List;

public interface SysDictTypeService  {

    PageResult<SysDictTypeEntity> page(SysDictTypeEntity sysDictTypeEntity);


    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictDataEntity> selectDictDataByType(String dictType);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    SysDictTypeEntity selectDictTypeById(Long dictId);

    /**
     * 查询所有字典类型
     *
     * @return 字典类型集合信息
     */
     List<SysDictTypeEntity> selectDictTypeAll();

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
     int updateDictType(SysDictTypeEntity dictType);

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
     int insertDictType(SysDictTypeEntity dictType);

    /**
     * 批量删除字典信息
     *
     * @param dictIds 需要删除的字典ID
     */
     void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 重置字典缓存数据
     */
    void resetDictCache();

    /**
     * 加载字典缓存数据
     */
     void loadingDictCache();

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    boolean checkDictTypeUnique(SysDictTypeEntity dictType);
}
