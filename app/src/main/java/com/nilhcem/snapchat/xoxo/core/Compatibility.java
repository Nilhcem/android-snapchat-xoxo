package com.nilhcem.snapchat.xoxo.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

public final class Compatibility {

    private Compatibility() {
        throw new UnsupportedOperationException();
    }

    public static boolean isCompatible(int apiLevel) {
        return android.os.Build.VERSION.SDK_INT >= apiLevel;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int convertDpToIntPixel(float dp, Context context) {
        return Math.round(convertDpToPixel(dp, context));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setElevation(float dp, View view) {
        if (isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            view.setElevation(convertDpToIntPixel(dp, view.getContext()));
        }
    }
}
