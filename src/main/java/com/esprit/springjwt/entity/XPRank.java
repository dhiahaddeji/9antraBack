package com.esprit.springjwt.entity;

public enum XPRank {
    BRONZE(0),
    SILVER(200),
    GOLD(500),
    PLATINUM(1000),
    ELITE(2500);

    private final int minXp;

    XPRank(int minXp) { this.minXp = minXp; }

    public int getMinXp() { return minXp; }

    public static XPRank fromXp(int xp) {
        XPRank result = BRONZE;
        for (XPRank r : values()) {
            if (xp >= r.minXp) result = r;
        }
        return result;
    }

    public XPRank next() {
        XPRank[] vals = values();
        int idx = this.ordinal();
        return idx < vals.length - 1 ? vals[idx + 1] : null;
    }
}
