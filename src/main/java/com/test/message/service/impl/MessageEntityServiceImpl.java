package com.test.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.message.mapper.MessageEntityMapper;
import com.test.message.pojo.domain.MessageEntityDO;
import com.test.message.service.MessageEntityService;
import org.springframework.stereotype.Service;

@Service
public class MessageEntityServiceImpl extends ServiceImpl<MessageEntityMapper, MessageEntityDO>
    implements MessageEntityService {

}




