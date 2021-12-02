package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import org.hibernate.Session;

import java.io.IOException;

public class UpdateAllItems {
    public static void main(String[] args) throws IOException {
        Session session = BuildSession.build();
        ItemEntity.updateAllItems(session);
        session.close();
    }
}
