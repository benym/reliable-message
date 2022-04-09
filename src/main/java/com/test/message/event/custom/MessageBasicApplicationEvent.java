package com.test.message.event.custom;


import com.test.message.enums.MessageAction;
import com.test.message.event.BasicApplicationEvent;
import com.test.message.pojo.dto.MessageEntityDTO;
import java.util.List;
import java.util.Map;

// 如果这个MessageBasicApplicationEvent的泛型没有继承一个类
// 那么ApplicationEvent发布就不会生效
// 被识别为不是ApplicationEvent的子类
// ApplicationEvent源码注释说明，直接继承抽象类是没有意义的
public class MessageBasicApplicationEvent<T extends String> extends BasicApplicationEvent {

    private static final long serialVersionUID = 8931752722780104852L;
    private T data;
    private static final Object SOURCE = new Object();
    private List<T> userlist;
    private List<T> userIds;
    private Map<String, Long> map;
    private MessageEntityDTO messageEntity;

    public MessageBasicApplicationEvent(T source) {
        super(source);
    }

    public MessageBasicApplicationEvent(T source, Class<?> customClass,
            MessageAction messageAction, List<T> list, List<T> ids, Map<String, Long> map,
            MessageEntityDTO messageEntity) {
        super(SOURCE, customClass, messageAction);
        this.data = source;
        this.userlist = list;
        this.userIds = ids;
        this.map = map;
        this.messageEntity = messageEntity;

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<T> userlist) {
        this.userlist = userlist;
    }

    public List<T> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<T> userIds) {
        this.userIds = userIds;
    }

    public Map<String, Long> getMap() {
        return map;
    }

    public void setMap(Map<String, Long> map) {
        this.map = map;
    }

    public MessageEntityDTO getMessageEntity() {
        return messageEntity;
    }

    public void setMessageEntity(MessageEntityDTO messageEntity) {
        this.messageEntity = messageEntity;
    }
}
