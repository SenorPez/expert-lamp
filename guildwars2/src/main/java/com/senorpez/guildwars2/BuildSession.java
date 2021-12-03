package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import com.senorpez.guildwars2.entity.MaterialEntity;
import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;

public class BuildSession {
    public static Session build() {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        stringEncryptor.setPassword(System.getProperty("enc_password"));

        HibernatePBEEncryptorRegistry hibernateRegistry = HibernatePBEEncryptorRegistry.getInstance();
        hibernateRegistry.registerPBEStringEncryptor("stringEncryptor", stringEncryptor);

        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(ItemEntity.class)
                .addAnnotatedClass(MaterialEntity.class)
                .addAnnotatedClass(PriceEntity.class)
                .getMetadataBuilder()
                .build();

        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder()
                .build();
        return sessionFactory.openSession();
    }
}
