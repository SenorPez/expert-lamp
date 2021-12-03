package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Report {
    public static void main(String[] args) {
        Session session = BuildSession.build();

        String hql = "FROM ItemEntity WHERE material IS NOT NULL";
        TypedQuery<ItemEntity> query = session.createQuery(hql, ItemEntity.class);
        Set<ItemEntity> items = new HashSet<>(query.getResultList());

        List<ReportLine> lines = new ArrayList<>();
        items.forEach(item ->
                lines.add(new ReportLine(item.getName(),
                item.getPrices().stream().mapToInt(PriceEntity::getBuyPrice).average().orElse(0d),
                item.getPrices().stream().mapToInt(PriceEntity::getSellPrice).average().orElse(0d))));
        lines.sort((a, b) -> Double.compare(b.buyPrice, a.buyPrice));

        lines.stream()
                .limit(20)
                .forEach(item -> System.out.print(item.getName() + " " + item.getBuyPrice() + " " + item.getSellPrice() + "\n"));
    }

    private record ReportLine(String name, double buyPrice, double sellPrice) {
        public String getName() {
            return name;
        }

        public double getBuyPrice() {
            return buyPrice;
        }

        public double getSellPrice() {
            return sellPrice;
        }
    }
}
