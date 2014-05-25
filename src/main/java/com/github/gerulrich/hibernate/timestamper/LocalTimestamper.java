package com.github.gerulrich.hibernate.timestamper;

public class LocalTimestamper
    implements Timestamper {

    @Override
    public long nextTimestamp() {
        return org.hibernate.cache.Timestamper.next();
    }

}
