package com.test.message.listener;


import com.test.message.event.BasicApplicationEvent;
import com.test.message.handler.BasicEventHandler;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationListener;

public abstract class BasicDataListener<T extends BasicApplicationEvent> implements
        ApplicationListener<T> {

    protected Map<Class<?>, BasicEventHandler> map = new HashMap<>();

    public Map<Class<?>, BasicEventHandler> getMap() {
        return map;
    }

    public void setMap(Map<Class<?>, BasicEventHandler> map) {
        this.map = map;
    }

    @Override
    public void onApplicationEvent(T event) {
        BasicEventHandler handler = map.get(event.getClass());
        handler.processData(event.getMessageAction(), event);
    }
}
