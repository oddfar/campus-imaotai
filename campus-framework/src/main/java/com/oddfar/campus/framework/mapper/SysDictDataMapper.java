package com.oddfar.campus.framework.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.oddfar.campus.common.core.BaseMapperX;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.entity.SysDictDataEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDictDataMapper extends BaseMapperX<SysDictDataEntity> {
    default PageResult<SysDictDataEntity> selectPage(SysDictDataEntity dictData) {

        return selectPage( new LambdaQueryWrapperX<SysDictDataEntity>()
                .likeIfPresent(SysDictDataEntity::getDictType, dictData.getDictType())
                .likeIfPresent(SysDictDataEntity::getDictLabel, dictData.getDictLabel())
                .eqIfPresent(SysDictDataEntity::getStatus, "0")
                .betweenIfPresent(SysDictDataEntity::getCreateTime, dictData.getParams())
                .orderByAsc(SysDictDataEntity::getDictSort));
    }

    default List<SysDictDataEntity> selectDictDataByType(String dictType) {
        return selectList(new LambdaQueryWrapperX<SysDictDataEntity>()
                .eq(SysDictDataEntity::getDictType, dictType)
                .eq(SysDictDataEntity::getStatus, "0")
                .orderByAsc(SysDictDataEntity::getDictSort));
    }

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
//    @Update("update sys_dict_data set dict_type = #{newDictType} where dict_type = #{oldDictType}")
    default int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType) {
        SysDictDataEntity dictData = new SysDictDataEntity();
        dictData.setDictType(newDictType);
        return update(dictData, new UpdateWrapper<SysDictDataEntity>().eq("dict_type", oldDictType));
    }

    /**
     * 查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    default Long countDictDataByType(String dictType) {
//       select count(1) from sys_dict_data where dict_type=#{dictType}
        return selectCount("dict_type", dictType);

    }

}
