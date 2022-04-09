package com.test.message.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.message.enums.MessageAction;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.annotation.Transient;


public class BasicApplicationEvent extends ApplicationEvent {


    private static final long serialVersionUID = -280687725611178365L;

    @Transient
    @JsonIgnore
    private Class<?> customClass;

    @Transient
    @JsonIgnore
    private MessageAction messageAction;

    public BasicApplicationEvent(Object source) {
        super(source);
    }

    public BasicApplicationEvent(Object source, Class<?> customClass, MessageAction messageAction) {
        super(source);
        this.customClass = customClass;
        this.messageAction = messageAction;
    }

    public Class<?> getCustomClass() {
        return customClass;
    }

    public void setCustomClass(Class<?> customClass) {
        this.customClass = customClass;
    }

    public MessageAction getMessageAction() {
        return messageAction;
    }

    public void setMessageAction(MessageAction messageAction) {
        this.messageAction = messageAction;
    }
}
