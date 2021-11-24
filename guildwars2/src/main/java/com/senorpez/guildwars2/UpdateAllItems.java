package com.senorpez.guildwars2;

import com.senorpez.guildwars2.api.Item;
import com.senorpez.guildwars2.api.ItemBuilder;
import com.senorpez.guildwars2.entity.ItemEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.stream.Stream;

public class UpdateAllItems {
    public static void main(String[] args) throws Exception {
        Session session = BuildSession.build();

        Transaction tx = session.beginTransaction();
        ItemBuilder builder = new ItemBuilder();
        Stream<Item> items = builder.get();
        items.forEach(item -> session.saveOrUpdate(new ItemEntity(item)));
        tx.commit();

        session.close();
    }
}
