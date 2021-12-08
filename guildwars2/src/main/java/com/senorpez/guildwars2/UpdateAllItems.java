package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class UpdateAllItems {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Session session = BuildSession.build();
        ItemEntity.updateAllItems(session);
        session.close();
    }
}
