package com.oddfar.campus.framework.service.impl;

import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysDictDataEntity;
import com.oddfar.campus.common.utils.DictUtils;
import com.oddfar.campus.framework.mapper.SysDictDataMapper;
import com.oddfar.campus.framework.service.SysDictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    @Resource
    private SysDictDataMapper dictDataMapper;


    @Override
    public PageResult<SysDictDataEntity> page(SysDictDataEntity dictDataEntity) {
        return dictDataMapper.selectPage(dictDataEntity);
    }

    @Override
    public int insertDictData(SysDictDataEntity dictData) {
        int row = dictDataMapper.insert(dictData);
        if (row > 0) {
            List<SysDictDataEntity> dictDatas = dictDataMapper.selectDictDataByType(dictData.getDictType());
            DictUtils.setDictCache(dictData.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public SysDictDataEntity selectDictDataById(Long dictCode) {
        return dictDataMapper.selectById(dictCode);
    }


    @Override
    public int updateDictData(SysDictDataEntity data) {
        int row = dictDataMapper.updateById(data);
        if (row > 0) {
            List<SysDictDataEntity> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictDataEntity data = dictDataMapper.selectById(dictCode);
            dictDataMapper.deleteById(dictCode);
            List<SysDictDataEntity> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }
}
