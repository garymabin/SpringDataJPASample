package com.thoughtworks.demo.configuration;

import com.thoughtworks.demo.util.SpringEntityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringEntityConfiguration {

    @Autowired
    AutowireCapableBeanFactory beanFactory;

    @Bean
    public SpringEntityListener SpringEntityListener() {
        SpringEntityListener listener = SpringEntityListener.get();
        listener.setBeanFactory(beanFactory);
        return listener;
    }
}
