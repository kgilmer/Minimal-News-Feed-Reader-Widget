package com.abk.mrw.util;

import com.google.common.base.Optional;

/**
 * Consolidates shared preferences partitioning based on widget id.
 * This class is NOT stateless.
 */
public final class PrefsUtil {

    public static final String PREFERENCE_PREFIX = "mrw.";
    //Stores option to the widget id.
    private static Optional<Integer> widgetIdOpt = Optional.absent();

    private PrefsUtil() {
    }

    /**
     * @return name of shared preferences root.
     */
    public static String getSharedPrefsRoot() {
        if (widgetIdOpt.isPresent()) {
            return PREFERENCE_PREFIX + widgetIdOpt.get();
        }

        throw new IllegalStateException("no widget id");
    }

    /**
     * @param widgetId widget id
     * @return name of shared preferences root.
     */
    public static String getSharedPrefsRoot(int widgetId) {
        return PREFERENCE_PREFIX + widgetId;
    }

    public static void setWidgetId(int widgetId) {
        widgetIdOpt = Optional.of(widgetId);
    }

    public static boolean hasWidgetId() {
        return widgetIdOpt.isPresent();
    }

    public static int getWidgetId() {
        return widgetIdOpt.get();
    }
}
