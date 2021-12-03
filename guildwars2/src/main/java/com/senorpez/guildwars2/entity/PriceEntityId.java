package com.senorpez.guildwars2.entity;

import java.io.Serializable;

public class PriceEntityId implements Serializable {
    private int item;
    private long timestamp;

    public PriceEntityId() {
    }

    public PriceEntityId(int item, long timestamp) {
        this.item = item;
        this.timestamp = timestamp;
    }

    public int getItem() {
        return item;
    }

    public PriceEntityId setItem(int item) {
        this.item = item;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PriceEntityId setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final PriceEntityId that)) return false;
        return that.getItem() == getItem();
    }

    @Override
    public int hashCode() {
        return ((Integer) getItem()).hashCode() * ((Long) getTimestamp()).hashCode();
    }
}
