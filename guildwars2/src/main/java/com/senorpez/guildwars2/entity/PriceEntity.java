package com.senorpez.guildwars2.entity;

import com.senorpez.guildwars2.api.commerce.Price;
import com.senorpez.guildwars2.api.commerce.PriceBuilder;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Entity
@IdClass(value = PriceEntityId.class)
@Table(name = "prices")
public class PriceEntity {
    @ManyToOne
    @JoinColumn(name = "item_id")
    @Id
    private ItemEntity item;

    @Column(name = "price_timestamp", nullable = false)
    @Id
    private long timestamp;

    @Column(name = "buy_quantity")
    private int buyQuantity;

    @Column(name = "buy_price")
    private int buyPrice;

    @Column(name = "sell_quantity")
    private int sellQuantity;

    @Column(name = "sell_price")
    private int sellPrice;

    public PriceEntity() {
    }

    public PriceEntity(Price price, long timestamp, Session session) {
        this.timestamp = timestamp;

        this.buyQuantity = price.getBuyQuantity();
        this.buyPrice = price.getBuyPrice();
        this.sellQuantity = price.getSellQuantity();
        this.sellPrice = price.getSellPrice();

        this.item = session.get(ItemEntity.class, price.getId());
    }

    public ItemEntity getItem() {
        return item;
    }

    public PriceEntity setItem(ItemEntity item) {
        this.item = item;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PriceEntity setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public PriceEntity setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
        return this;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public PriceEntity setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
        return this;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public PriceEntity setSellQuantity(int sellQuantity) {
        this.sellQuantity = sellQuantity;
        return this;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public PriceEntity setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof final PriceEntity that)) return false;
        return that.getItem().equals(getItem()) && that.getTimestamp() == getTimestamp();
    }

    @Override
    public int hashCode() {
        return getItem().hashCode() * ((Long) getTimestamp()).hashCode();
    }

    public static void getPrices(long timestamp, Session session) throws IOException, URISyntaxException, InterruptedException {
        PriceBuilder priceBuilder = new PriceBuilder();
        Transaction tx = session.beginTransaction();
        Stream<Price> prices = priceBuilder.get();

        prices.forEach(price -> {
            PriceEntity priceEntity = new PriceEntity(price, timestamp, session);
            if (priceEntity.item != null) session.persist(priceEntity);
        });
        tx.commit();
    }
}
