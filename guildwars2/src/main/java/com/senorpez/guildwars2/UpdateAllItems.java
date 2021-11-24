package com.senorpez.guildwars2;

import com.senorpez.guildwars2.api.ItemBuilder;
import com.senorpez.guildwars2.entity.ItemEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UpdateAllItems {
    public static void main(String[] args) throws Exception {
        Session session = BuildSession.build();

        Transaction tx = session.beginTransaction();
        ItemBuilder builder = new ItemBuilder();
        builder.get()
                .map(ItemEntity::new)
                .forEach(session::saveOrUpdate);
        tx.commit();

        session.close();
    }
}
