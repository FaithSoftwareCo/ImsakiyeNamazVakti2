package com.faith.namazvakti.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.faith.namazvakti.models.PrayerTimeReminder;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            PrayerTimeReminder.reschedulePrayerTimeReminders(context);
        }
    }
}
