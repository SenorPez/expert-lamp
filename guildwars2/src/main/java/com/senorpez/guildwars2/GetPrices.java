package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;

import java.io.IOException;

public class GetPrices {
    public static void main(String[] args) throws IOException {
        Session session = BuildSession.build();
        long timestamp = System.currentTimeMillis();
        PriceEntity.getPrices(timestamp, session);
    }
}
