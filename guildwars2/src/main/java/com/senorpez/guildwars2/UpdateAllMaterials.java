package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.MaterialEntity;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class UpdateAllMaterials {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        Session session = BuildSession.build();
        MaterialEntity.updateAllMaterials(session);
        session.close();
    }
}
