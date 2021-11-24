package com.senorpez.guildwars2;

import com.senorpez.guildwars2.api.Material;
import com.senorpez.guildwars2.api.MaterialBuilder;
import com.senorpez.guildwars2.entity.MaterialEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.stream.Stream;

public class UpdateAllMaterials {
    public static void main(String[] args) throws IOException {
        Session session = BuildSession.build();

        Transaction tx = session.beginTransaction();
        MaterialBuilder builder = new MaterialBuilder();
        Stream<Material> materials = builder.get();
        materials.forEach(material -> MaterialEntity
                .buildEntities(material)
                .forEach(session::saveOrUpdate)
        );
        tx.commit();

        session.close();
    }
}
