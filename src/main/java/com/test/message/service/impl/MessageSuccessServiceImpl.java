package com.test.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.message.mapper.MessageSuccessMapper;
import com.test.message.pojo.domain.MessageSuccessDO;
import com.test.message.service.MessageSuccessService;
import org.springframework.stereotype.Service;


@Service
public class MessageSuccessServiceImpl extends ServiceImpl<MessageSuccessMapper, MessageSuccessDO>
    implements MessageSuccessService {

}




