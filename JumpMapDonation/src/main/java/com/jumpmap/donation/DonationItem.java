package com.jumpmap.donation;

/**
 * 후원 시 발동 가능한 각 항목.
 * path 는 config.yml 내 가격이 저장되는 위치(dot notation)입니다.
 */
public enum DonationItem {

    INSTANT_DEATH("prices.instant-death", "즉사"),
    RESET("prices.reset", "태초"),
    DEBUFF_REMOVE("prices.debuff-remove", "디버프 제거"),

    DEBUFF_HEALTH_DOWN("prices.debuffs.health-down", "디버프: 체력 1칸 제거"),
    DEBUFF_BIND("prices.debuffs.bind", "디버프: 구속"),
    DEBUFF_POISON("prices.debuffs.poison", "디버프: 독"),
    DEBUFF_BLINDNESS("prices.debuffs.blindness", "디버프: 실명"),
    DEBUFF_HUNGER_DOWN("prices.debuffs.hunger-down", "디버프: 배고픔 1칸 제거"),
    DEBUFF_REVERSE_CONTROLS("prices.debuffs.reverse-controls", "디버프: 조작키 반전"),
    DEBUFF_KNOCKBACK("prices.debuffs.knockback", "디버프: 밀치기");

    private final String path;
    private final String displayName;

    DonationItem(String path, String displayName) {
        this.path = path;
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** 관리자 명령어에서 쓰는 짧은 key. 예: DEBUFF_HEALTH_DOWN -> "health-down" */
    public String getKey() {
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    public static DonationItem fromKey(String key) {
        for (DonationItem item : values()) {
            if (item.getKey().equalsIgnoreCase(key)) {
                return item;
            }
        }
        return null;
    }
}
