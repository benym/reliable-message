package com.test.message.service;


public interface MessageService {

    /**
     * 发送消息给用户
     *
     * @return Integer
     */
    Integer sendToUser();

    /**
     * 测试事务回滚
     */
    void testTransaction();

    void test1();

    void test2();

    void test3();
}
