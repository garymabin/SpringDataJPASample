package com.thoughtworks.demo.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;


public class TestAppender extends AppenderBase<ILoggingEvent> {

    private List<ILoggingEvent> eventList = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        if ("org.hibernate.SQL".equals(eventObject.getLoggerName())) {
            eventList.add(eventObject);
        }
    }

    public void clear() {
        this.eventList.clear();
    }

    public List<ILoggingEvent> getEventList() {
        return eventList;
    }
}
