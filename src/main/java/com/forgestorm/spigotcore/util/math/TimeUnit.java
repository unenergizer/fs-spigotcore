package com.forgestorm.spigotcore.util.math;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class will convert seconds to a readable string.
 * Usage: TimeUnit.toString(69696969);
 * Website: https://www.spigotmc.org/resources/timeformat-api.16318/
 *
 * @author xYourFreindx
 */
@SuppressWarnings("WeakerAccess")
public enum TimeUnit {

    SEC("second", 1, 's'),
    MIN("minute", 60, 'm'),
    HOUR("hour", 3600, 'h'),
    DAY("day", 86400, 'd'),
    WEEK("week", 604800, 'w');

    private static final Map<Character, Long> conversion = new HashMap<>();
    private static final TimeUnit[] order = new TimeUnit[]{WEEK, DAY, HOUR, MIN, SEC};
    private static final Pattern isLong = Pattern.compile("[0-9]+");

    static {
        for (TimeUnit unit : values())
            conversion.put(unit.unit, unit.ms);
    }

    private final String name;
    private final long ms;
    private final char unit;

    TimeUnit(String name, long ms, char unit) {
        this.name = name;
        this.ms = ms;
        this.unit = unit;
    }

    private static long convert(char c) {
        if (conversion.containsKey(c))
            return conversion.get(c);
        return 0;
    }

    public static String toString(long time) {
        StringBuilder sb = new StringBuilder();
        int t;
        for (TimeUnit unit : order) {
            if (time >= unit.ms) {
                t = (int) Math.floor((double) time / (double) unit.ms);
                sb.append(unit.addUnit(t)).append(" ");
                time -= t * unit.ms;
            }
        }
        return sb.toString().trim();
    }

    public static long toLong(String string) {
        long fromString = 0;
        for (String t : string.split(" ")) {
            char c = t.charAt(t.length() - 1);
            t = t.substring(0, t.length() - 1);
            if (!isLong.matcher(t).matches())
                continue;
            fromString += Long.parseLong(t) * TimeUnit.convert(c);
        }
        return fromString;
    }

    public String addUnit(int x) {
        String r = this.name;
        if (x > 1)
            r += "s";
        return x + " " + r;
    }
}
