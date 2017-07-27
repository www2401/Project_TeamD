package com.tagadvance.aqi;

/**
 * @author Tag <tagadvance@gmail.com>
 * @see http://airnowapi.org/aq101
 */
public enum AQI {

    GOOD (0, 50, 0xff00e400, 0xff000000, 1, R.string.good),
    MODERATE (51, 100, 0xffffff00, 0xff000000, 2, R.string.moderate),
    UNHEALTHY_FOR_SENSITIVE_GROUPS (101, 151, 0xffff7e00, 0xffffffff, 3, R.string.unhealthy_for_sensitive_groups),
    UNHEALTHY (151, 200, 0xffff0000, 0xffffffff, 4, R.string.unhealthy),
    VERY_UNHEALTHY (201, 300, 0xff99004c, 0xffffffff, 5, R.string.very_unhealthy),
    HAZARDOUS (301, 500, 0xff7e0023, 0xffffffff, 6, R.string.hazardous);

    private final int min, max, backgroundColor, textColor, category, stringId;

    AQI(int min, int max, int backgroundColor, int textColor, int category, int stringId) {
        this.min = min;
        this.max = max;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.category = category;
        this.stringId = stringId;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getCategory() {
        return category;
    }

    public int getStringId() {
        return this.stringId;
    }

    public static AQI getAQI(int index) {
        for (AQI aqi : values()) {
            if (index >= aqi.getMin() && index <= aqi.getMax()) {
                return aqi;
            }
        }
        String message = String.format("index (%d) is out of bounds", index);
        throw new IllegalArgumentException(message);
    }

}
