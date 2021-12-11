package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

public class Report {
    public static void main(String[] args) {
        Session session = BuildSession.build();

        String hql = "FROM ItemEntity WHERE material IS NOT NULL";
        TypedQuery<ItemEntity> query = session.createQuery(hql, ItemEntity.class);
        Set<ItemEntity> items = new HashSet<>(query.getResultList());

        long currentTimeMillis = System.currentTimeMillis();
        List<ReportLine> lastDay = new ArrayList<>();
        List<ReportLine> lastWeek = new ArrayList<>();
        List<ReportLine> lastMonth = new ArrayList<>();

        items.forEach(item -> lastDay.add(new ReportLine(
                item.getName(),
                item.getPrices()
                        .stream()
                        .filter(price -> price.getTimestamp() > currentTimeMillis - 86400 * 1000L)
                        .collect(Collectors.toList()))));
        items.forEach(item -> lastWeek.add(new ReportLine(
                item.getName(),
                item.getPrices()
                        .stream()
                        .filter(price -> price.getTimestamp() > currentTimeMillis - 86400 * 1000L * 7)
                        .collect(Collectors.toList()))));
        items.forEach(item -> lastMonth.add(new ReportLine(
                item.getName(),
                item.getPrices()
                        .stream()
                        .filter(price -> price.getTimestamp() > currentTimeMillis - 86400 * 1000L * 30)
                        .collect(Collectors.toList()))));

        final double maxInvestment = 100000d;
        final double products = 5d;
        final double maxUnitPrice = maxInvestment / products / 250.0d;

        Comparator<ReportLine> byRoi = Comparator.comparingDouble(ReportLine::getRoi).reversed();
        System.out.println("1-Day Averages:");
        System.out.println("---------------");
        lastDay.stream()
                .filter(item -> (item.getBuyPrice() != 0
                        && item.getSellPrice() != 0
                        && item.getRoi() > 0)
                        && item.buyPrice <= maxUnitPrice)
                .sorted(byRoi)
                .limit(20)
                .forEach(item -> System.out.printf("%40s   %7.2f   %7.2f  %7.2f   %6.2f%%\n",
                        item.getName(), item.getBuyPrice(), item.getSellPrice(), item.getProfit(), item.getRoi()));

        System.out.println("\n7-Day Averages:");
        System.out.println("---------------");
        lastWeek.stream()
                .filter(item -> (item.getBuyPrice() != 0
                        && item.getSellPrice() != 0
                        && item.getRoi() > 0)
                        && item.buyPrice <= maxUnitPrice)
                .sorted(byRoi)
                .limit(20)
                .forEach(item -> System.out.printf("%40s   %7.2f   %7.2f  %7.2f   %6.2f%%\n",
                        item.getName(), item.getBuyPrice(), item.getSellPrice(), item.getProfit(), item.getRoi()));

        System.out.println("\n30-Day Averages:");
        System.out.println("----------------");
        lastDay.stream()
                .filter(item -> (item.getBuyPrice() != 0
                        && item.getSellPrice() != 0
                        && item.getRoi() > 0)
                        && item.buyPrice <= maxUnitPrice)
                .sorted(byRoi)
                .limit(20)
                .forEach(item -> System.out.printf("%40s   %7.2f   %7.2f  %7.2f   %6.2f%%\n",
                        item.getName(), item.getBuyPrice(), item.getSellPrice(), item.getProfit(), item.getRoi()));
    }

    private static class ReportLine {
        private final String name;
        private final double buyPrice;
        private final double sellPrice;

        private final double profit;
        private final double roi;

        public ReportLine(String name, double buyPrice, double sellPrice) {
            this.name = name;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;

            this.profit = this.sellPrice
                    - Math.max(1, Math.round(this.sellPrice * 0.05))
                    - Math.max(1, Math.round(this.sellPrice * 0.1))
                    - this.buyPrice;
            this.roi = ((this.profit + this.buyPrice) / this.buyPrice - 1) * 100;
        }

        public ReportLine(String name, List<PriceEntity> priceEntities) {
            this(
                    name,
                    priceEntities.stream().mapToInt(PriceEntity::getBuyPrice).average().orElse(0),
                    priceEntities.stream().mapToInt(PriceEntity::getSellPrice).average().orElse(0)
            );
        }

        public String getName() {
            return name;
        }

        public double getBuyPrice() {
            return buyPrice;
        }

        public double getSellPrice() {
            return sellPrice;
        }

        public double getProfit() {
            return profit;
        }

        public double getRoi() {
            return roi;
        }
    }
}
