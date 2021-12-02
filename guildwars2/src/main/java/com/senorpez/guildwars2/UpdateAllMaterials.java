package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.MaterialEntity;
import org.hibernate.Session;

import java.io.IOException;

public class UpdateAllMaterials {
    public static void main(String[] args) throws IOException {
        Session session = BuildSession.build();
        MaterialEntity.updateAllMaterials(session);
        session.close();
    }
}
