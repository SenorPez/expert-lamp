package com.senorpez.guildwars2;

import com.senorpez.guildwars2.entity.ItemEntity;
import com.senorpez.guildwars2.entity.PriceEntity;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class Report {
    private static final List<ReportLine> lastDay = new ArrayList<>();
    private static final List<ReportLine> lastWeek = new ArrayList<>();
    private static final List<ReportLine> lastMonth = new ArrayList<>();

    private static final BiPredicate<PriceEntity, Long> within1Day = (p, t) -> p.getTimestamp() > t - 86400 * 1000L;
    private static final BiPredicate<PriceEntity, Long> within7Days = (p, t) -> p.getTimestamp() > t - 86400 * 1000L * 7;
    private static final BiPredicate<PriceEntity, Long> within30Days = (p, t) -> p.getTimestamp() > t - 86400 * 1000L * 30;

    private static final Comparator<ReportLine> byRoi = Comparator.comparingDouble(ReportLine::getRoi).reversed();
    private static final double maxInvestment = 100000d;
    private static final double products = 5d;
    private static final double maxUnitPrice = maxInvestment / products / 250.0d;

    public static void main(String[] args) {
        Session session = BuildSession.build();

        int pageNumber = 1;
        final int pageSize = 10;
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(ItemEntity.class)));
        Long count = session.createQuery(countQuery).getSingleResult();

        CriteriaQuery<ItemEntity> itemQuery = cb.createQuery(ItemEntity.class);
        Root<ItemEntity> from = itemQuery.from(ItemEntity.class);
        CriteriaQuery<ItemEntity> select = itemQuery.select(from);
        TypedQuery<ItemEntity> query = session.createQuery(select);

        long currentTimeMillis = System.currentTimeMillis();
        while (pageNumber < count.intValue()) {
            query.setFirstResult(pageNumber - 1);
            query.setMaxResults(pageSize);
            Set<ItemEntity> items = new HashSet<>(query.getResultList());
            addLastDayPrices(items, currentTimeMillis);
            addLastWeekPrices(items, currentTimeMillis);
            addLastMonthPrices(items, currentTimeMillis);
            session.clear();
            pageNumber += pageSize;
        }

        printLastDayPrices();
        System.out.println();
        printLastWeekPrices();
        System.out.println();
        printLastMonthPrices();
    }

    public static void addPrices(Set<ItemEntity> items, long currentTimeMillis, List<ReportLine> collection, BiPredicate<PriceEntity, Long> within) {
        items.forEach(item -> collection.add(new ReportLine(
                item.getName(),
                item.getPrices()
                        .stream()
                        .filter(p -> within.test(p, currentTimeMillis))
                        .collect(Collectors.toList())
        )));
    }

    private static void addLastDayPrices(Set<ItemEntity> items, long currentTimeMillis) {
        addPrices(items, currentTimeMillis, lastDay, within1Day);
    }

    private static void addLastWeekPrices(Set<ItemEntity> items, long currentTimeMillis) {
        addPrices(items, currentTimeMillis, lastWeek, within7Days);
    }

    private static void addLastMonthPrices(Set<ItemEntity> items, long currentTimeMillis) {
        addPrices(items, currentTimeMillis, lastMonth, within30Days);
    }

    private static void printPrices(List<ReportLine> collection) {
        System.out.println("---------------");
        collection.stream()
                .filter(item -> (
                        item.getBuyPrice() != 0
                                && item.getSellPrice() != 0
                                && item.getRoi() > 0)
                        && item.getBuyPrice() < maxUnitPrice)
                .sorted(byRoi)
                .limit(20)
                .forEach(item -> System.out.printf("%40s   %7.2f   %7.2f  %7.2f   %6.2f%%\n",
                        item.getName(), item.getBuyPrice(), item.getSellPrice(), item.getProfit(), item.getRoi()));
    }

    private static void printLastDayPrices() {
        System.out.println("1-Day Averages:");
        printPrices(lastDay);
    }

    private static void printLastWeekPrices() {
        System.out.println("7-Day Averages:");
        printPrices(lastWeek);
    }

    private static void printLastMonthPrices() {
        System.out.println("30-Day Averages");
        printPrices(lastMonth);
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
