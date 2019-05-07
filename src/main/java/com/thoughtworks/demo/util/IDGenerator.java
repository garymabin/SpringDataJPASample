package com.thoughtworks.demo.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IDGenerator implements IdentifierGenerator {

    private final Random randomGenerator = new Random();

    public String generateRandomStringNumber() {
        return randomGenerator.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .reduce(String::concat)
                .get();
    }


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return String.format("%s%s%s",
                object.getClass().getSimpleName().substring(0, 1),

                //use current time + random string
                new SimpleDateFormat("yyMMdd").format(new Date()),
                generateRandomStringNumber());
    }
}
