package com.senorpez.guildwars2;

import com.senorpez.guildwars2.api.Item;
import com.senorpez.guildwars2.entity.ItemEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;

import java.util.stream.Stream;

public class UpdateAllItems {
    public static void main(String[] args) throws Exception {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        stringEncryptor.setPassword(System.getProperty("enc_password"));

        HibernatePBEEncryptorRegistry hibernateRegistry = HibernatePBEEncryptorRegistry.getInstance();
        hibernateRegistry.registerPBEStringEncryptor("stringEncryptor", stringEncryptor);

        SessionFactory sessionFactory = UpdateAllItems.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Stream<Item> items = Item.getMultiple(Stream.of(28445, 28446));
        items.forEach(item -> session.saveOrUpdate(new ItemEntity(item)));
        tx.commit();

        session.close();
    }

    private static SessionFactory buildSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }
}
