package com.test.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.message.mapper.MessageLogMapper;
import com.test.message.pojo.domain.MessageLogDO;
import com.test.message.service.MessageLogService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MessageLogServiceImpl extends ServiceImpl<MessageLogMapper, MessageLogDO> implements
        MessageLogService {

    @Resource
    private MessageLogMapper messageLogMapper;

    @Override
    public List<MessageLogDO> getAllUncommitLog() {
        QueryWrapper<MessageLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageLogDO::getStatus, 0).eq(MessageLogDO::getDeleted, 0)
                .select(MessageLogDO::getNoticeUserId, MessageLogDO::getId);
        return messageLogMapper.selectList(queryWrapper);
    }

    @Override
    public List<MessageLogDO> getAllUncommitLog(Long limit) {
        QueryWrapper<MessageLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageLogDO::getStatus, 0).eq(MessageLogDO::getDeleted, 0)
                .select(MessageLogDO::getNoticeUserId, MessageLogDO::getId)
                .last("limit " + limit);
        return messageLogMapper.selectList(queryWrapper);
    }

    @Override
    public List<MessageLogDO> getAllcommitLog(Long limit) {
        QueryWrapper<MessageLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MessageLogDO::getStatus, 1).eq(MessageLogDO::getDeleted, 0)
                .select(MessageLogDO::getId)
                .last("limit " + limit);
        return messageLogMapper.selectList(queryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<MessageLogDO> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo,
                "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty,
                "error: can not execute. because can not find column for id from entity!");
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            // ???????????????????????????id???????????????map?????????????????????????????????saveOrUpdateBatch??????getById????????????id???????????????????????????????????????????????????
            // ??????MessageLogServiceImpl??????Mybatis-plus?????????saveOrUpdateBatch??????
            // ??????getById??????sql????????????????????????saveOrUpdateBatch??????
            if (StringUtils.checkValNull(idVal)) {
                sqlSession.insert(tableInfo.getSqlStatement(SqlMethod.INSERT_ONE.getMethod()),
                        entity);
            } else {
                MapperMethod.ParamMap<MessageLogDO> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, entity);
                sqlSession.update(tableInfo.getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod()),
                        param);
            }
        });
    }
}




