package com.oddfar.campus.framework.service.impl;

import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysDictDataEntity;
import com.oddfar.campus.common.domain.entity.SysDictTypeEntity;
import com.oddfar.campus.common.exception.ServiceException;
import com.oddfar.campus.common.utils.DictUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.mapper.SysDictDataMapper;
import com.oddfar.campus.framework.mapper.SysDictTypeMapper;
import com.oddfar.campus.framework.service.SysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.oddfar.campus.common.utils.DictUtils.clearDictCache;

@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {
    @Resource
    private SysDictTypeMapper dictTypeMapper;
    @Resource
    private SysDictDataMapper dictDataMapper;

    @Override
    public PageResult<SysDictTypeEntity> page(SysDictTypeEntity sysDictTypeEntity) {
        return dictTypeMapper.selectPage(sysDictTypeEntity);
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictDataEntity> selectDictDataByType(String dictType) {
        List<SysDictDataEntity> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas)) {
            return dictDatas;
        }

        dictDatas = dictDataMapper.selectDictDataByType(dictType);

        if (StringUtils.isNotEmpty(dictDatas)) {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;


    }

    @Override
    public SysDictTypeEntity selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectById(dictId);
    }

    @Override
    public List<SysDictTypeEntity> selectDictTypeAll() {
        return dictTypeMapper.selectList();
    }

    @Override
    @Transactional
    public int updateDictType(SysDictTypeEntity dictType) {
        SysDictTypeEntity oldDict = dictTypeMapper.selectById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        //把要更新的dictType的内容赋值到oldDict
//        BeanUtil.copyProperties(dictType, oldDict);
        int row = dictTypeMapper.updateById(dictType);

        if (row > 0) {
            List<SysDictDataEntity> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public int insertDictType(SysDictTypeEntity dictType) {
        int row = dictTypeMapper.insert(dictType);
        if (row > 0) {
            DictUtils.setDictCache(dictType.getDictType(), null);
        }
        return row;
    }

    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictTypeEntity dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
//            this.removeById(dictId);
            dictTypeMapper.deleteById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        Map<String, List<SysDictDataEntity>> dictDataMap = dictDataMapper.selectList("status", "0").stream().collect(Collectors.groupingBy(SysDictDataEntity::getDictType));
        for (Map.Entry<String, List<SysDictDataEntity>> entry : dictDataMap.entrySet()) {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictDataEntity::getDictSort)).collect(Collectors.toList()));
        }
    }

    @Override
    public boolean checkDictTypeUnique(SysDictTypeEntity dictType) {
        Long dictId = StringUtils.isNull(dictType.getDictId()) ? -1L : dictType.getDictId();
        SysDictTypeEntity info = dictTypeMapper.selectOne(new LambdaQueryWrapperX<SysDictTypeEntity>()
                .eq(SysDictTypeEntity::getDictType, dictType.getDictType()));
        if (StringUtils.isNotNull(info) && info.getDictId().longValue() != dictId.longValue()) {
            return false;
        }
        return true;
    }

}
