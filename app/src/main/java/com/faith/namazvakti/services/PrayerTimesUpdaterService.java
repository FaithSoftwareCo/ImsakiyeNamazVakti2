package com.faith.namazvakti.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.mehmetakiftutuncu.toolbelt.Log;
import com.github.mehmetakiftutuncu.toolbelt.Optional;
import com.faith.namazvakti.models.Place;
import com.faith.namazvakti.models.PrayerTimeReminder;
import com.faith.namazvakti.models.PrayerTimesOfDay;
import com.faith.namazvakti.utilities.MuezzinAPI;
import com.faith.namazvakti.utilities.Pref;
import com.faith.namazvakti.widgetproviders.PrayerTimesWidgetBase;

import java.util.List;

/**
 * Created by akif on 23/05/16.
 */
public class PrayerTimesUpdaterService extends IntentService implements MuezzinAPI.OnPrayerTimesDownloadedListener {
    public PrayerTimesUpdaterService() {
        super("PrayerTimesUpdaterService");
    }

    private Optional<Place> maybeCurrentPlace;

    public static void setUpdater(Context context) {
        Intent intent = new Intent(context, PrayerTimesUpdaterService.class);

        context.startService(intent);
    }

    @Override protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            update();
        }
    }

    private void update() {
        maybeCurrentPlace = Pref.Places.getCurrentPlace(this);

        if (maybeCurrentPlace.isEmpty()) {
            return;
        }

        Place place = maybeCurrentPlace.get();

        Log.debug(getClass(), "Updating prayer times for '%s'...", place);

        MuezzinAPI.get().getPrayerTimes(place, this);
    }

    @Override public void onPrayerTimesDownloaded(@NonNull List<PrayerTimesOfDay> prayerTimes) {
        Place place = maybeCurrentPlace.get();

        if (PrayerTimesOfDay.saveAllPrayerTimes(this, place, prayerTimes)) {
            // Updated prayer times and saved them successfully, now try to reschedule prayer time reminders.
            PrayerTimeReminder.reschedulePrayerTimeReminders(this);
            PrayerTimesWidgetBase.updateAllWidgets(this);
        }
    }

    @Override public void onDownloadPrayerTimesFailed(Exception e) {
        Log.error(getClass(), "Failed to download prayer times for place '%s'!", maybeCurrentPlace.get());
    }
}
