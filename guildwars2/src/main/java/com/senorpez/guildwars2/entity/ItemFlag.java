package com.senorpez.guildwars2.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ItemFlag {
    ACCOUNT_BIND_ON_USE("AccountBindOnUse"),
    ACCOUNT_BOUND("AccountBound"),
    ATTUNED("Attuned"),
    BULK_CONSUME("BulkConsume"),
    DELETE_WARNING("DeleteWarning"),
    HIDE_SUFFIX("HideSuffix"),
    INFUSED("Infused"),
    MONSTER_ONLY("MonsterOnly"),
    NO_MYSTIC_FORGE("NoMysticForge"),
    NO_SALVAGE("NoSalvage"),
    NO_SELL("NoSell"),
    NOT_UPGRADEABLE("NotUpgradeable"),
    NO_UNDERWATER("NoUnderwater"),
    SOULBIND_ON_ACQUIRE("SoulbindOnAcquire"),
    SOULBIND_ON_USE("SoulBindOnUse"),
    TONIC("Tonic"),
    UNIQUE("Unique");

    private static final Map<String, ItemFlag> BY_FLAG = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach(itemFlag -> BY_FLAG.put(itemFlag.flag, itemFlag));
    }

    private final String flag;

    ItemFlag(String flag) {
        this.flag = flag;
    }

    public static ItemFlag findByString(String flag) {
        return BY_FLAG.get(flag);
    }
}
