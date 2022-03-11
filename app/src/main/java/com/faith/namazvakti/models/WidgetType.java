package com.faith.namazvakti.models;

import com.faith.namazvakti.R;

/**
 * Created by akif on 08/07/16.
 */
public enum WidgetType {
    PRAYER_TIMES_HORIZONTAL(R.layout.widget_prayertimes_horizontal),
    PRAYER_TIMES_VERTICAL(R.layout.widget_prayertimes_vertical),
    PRAYER_TIMES_BIG(R.layout.widget_prayertimes_big);

    public final int layoutId;

    WidgetType(int layoutId) {
        this.layoutId = layoutId;
    }
}
