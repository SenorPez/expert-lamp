package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class GetPrices {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        Session session = BuildSession.build();
        long timestamp = System.currentTimeMillis();
        PriceEntity.getPrices(timestamp, session);
    }
}
