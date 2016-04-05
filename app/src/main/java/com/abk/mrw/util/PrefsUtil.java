package com.abk.mrw.util;

import com.google.common.base.Optional;

/**
 * Created by kgilmer on 4/2/16.
 */
public final class PrefsUtil {

    private static Optional<Integer> widgetIdOpt = Optional.absent();

    private PrefsUtil() {
    }

    public static String getSharedPrefsRoot() {
        if (widgetIdOpt.isPresent()) {
            return "mrw." + widgetIdOpt.get();
        }

        throw new IllegalStateException("no widget id");
    }

    public static String getSharedPrefsRoot(int widgetId) {
        return "mrw." + widgetId;
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
