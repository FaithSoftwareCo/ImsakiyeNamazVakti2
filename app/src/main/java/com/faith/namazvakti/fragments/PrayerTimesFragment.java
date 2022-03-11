package com.faith.namazvakti.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.*;
import android.widget.TextView;

import com.faith.namazvakti.models.PrayerTimeType;
import com.github.mehmetakiftutuncu.toolbelt.Log;
import com.github.mehmetakiftutuncu.toolbelt.Optional;
import com.kennyc.view.MultiStateView;
import com.faith.namazvakti.R;
import com.faith.namazvakti.activities.MuezzinActivity;
import com.faith.namazvakti.models.Place;
import com.faith.namazvakti.models.PrayerTimeReminder;
import com.faith.namazvakti.models.PrayerTimesOfDay;
import com.faith.namazvakti.utilities.MuezzinAPI;
import com.faith.namazvakti.utilities.Pref;
import com.faith.namazvakti.utilities.RemainingTime;
import com.faith.namazvakti.widgetproviders.PrayerTimesWidgetBase;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.faith.namazvakti.models.PrayerTimeType.*;

/**
 * Created by akif on 08/05/16.
 */
public class PrayerTimesFragment extends StatefulFragment implements MuezzinAPI.OnPrayerTimesDownloadedListener {
    private LinearLayout linearLayoutDayArray[] = new LinearLayout[31];
    /*private LinearLayout linearLayoutDay1;
    private LinearLayout linearLayoutDay2;
    private LinearLayout linearLayoutDay3;
    private LinearLayout linearLayoutDay4;
    private LinearLayout linearLayoutDay5;
    private LinearLayout linearLayoutDay6;
    private LinearLayout linearLayoutDay7;
    private LinearLayout linearLayoutDay8;
    private LinearLayout linearLayoutDay9;
    private LinearLayout linearLayoutDay10;
    private LinearLayout linearLayoutDay11;
    private LinearLayout linearLayoutDay12;
    private LinearLayout linearLayoutDay13;
    private LinearLayout linearLayoutDay14;
    private LinearLayout linearLayoutDay15;
    private LinearLayout linearLayoutDay16;
    private LinearLayout linearLayoutDay17;
    private LinearLayout linearLayoutDay18;
    private LinearLayout linearLayoutDay19;
    private LinearLayout linearLayoutDay20;
    private LinearLayout linearLayoutDay21;
    private LinearLayout linearLayoutDay22;
    private LinearLayout linearLayoutDay23;
    private LinearLayout linearLayoutDay24;
    private LinearLayout linearLayoutDay25;
    private LinearLayout linearLayoutDay26;
    private LinearLayout linearLayoutDay27;
    private LinearLayout linearLayoutDay28;
    private LinearLayout linearLayoutDay29;
    private LinearLayout linearLayoutDay30;
    private LinearLayout linearLayoutDay31;
*/
    private TextView textViewRemainingTimeInfo;
    private TextView textViewRemainingTime;
    private TextView textViewFajr;
    private TextView textViewDhuhr;
    private TextView textViewAsr;
    private TextView textViewMaghrib;
    private TextView textViewIsha;
    private TextView textViewShuruq;
    private TextView textViewQibla;

    private Place place;
    private Optional<PrayerTimesOfDay> maybePrayerTimesOfDay;

    private Timer timer;
    private TimerTask timerTask;

    private Context context;
    private MuezzinActivity muezzinActivity;

    private int defaultTextColor;
    private int redTextColor;

    private static final String FULL_DATE_PATTERN = "dd MMMM YYYY";
    private static final String DATE_MONTH_PATTERN = "dd MMMM";
    private static final DateTimeFormatter FULL_DATE_FORMATTTER = DateTimeFormat.forPattern(FULL_DATE_PATTERN);
    private static final DateTimeFormatter DATE_MONTH_FORMATTTER = DateTimeFormat.forPattern(DATE_MONTH_PATTERN);

    public PrayerTimesFragment() {}

    public static PrayerTimesFragment with(Bundle bundle) {
        PrayerTimesFragment prayerTimesFragment = new PrayerTimesFragment();
        prayerTimesFragment.setArguments(bundle);

        return prayerTimesFragment;
    }

    @Override public void onStart() {
        super.onStart();

        loadTodaysPrayerTimes();
    }

    @Override public void onResume() {
        super.onResume();

        scheduleRemainingTimeCounter();
    }

    @Override public void onPause() {
        super.onPause();

        cancelRemainingTimeCounter();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.context    = context;
            muezzinActivity = (MuezzinActivity) context;

            TypedValue typedValue = new TypedValue();
            muezzinActivity.getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
            TypedArray typedArray = getActivity().obtainStyledAttributes(typedValue.data, new int[] {android.R.attr.textColorSecondary});
            defaultTextColor = typedArray.getColor(0, -1);
            typedArray.recycle();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                redTextColor = getResources().getColor(R.color.red);
            } else {
                redTextColor = getResources().getColor(R.color.red, muezzinActivity.getTheme());
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must extend MuezzinActivity!");
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_prayertimes, container, false);

        multiStateViewLayout      = (MultiStateView) layout.findViewById(R.id.multiStateView_prayerTimes);
        textViewRemainingTimeInfo = (TextView) layout.findViewById(R.id.textView_prayerTimes_remainingTimeInfo);
        textViewRemainingTime     = (TextView) layout.findViewById(R.id.textView_prayerTimes_remainingTime);
        textViewFajr              = (TextView) layout.findViewById(R.id.textView_prayerTimes_fajrTime);
        textViewShuruq            = (TextView) layout.findViewById(R.id.textView_prayerTimes_shuruqTime);
        textViewDhuhr             = (TextView) layout.findViewById(R.id.textView_prayerTimes_dhuhrTime);
        textViewAsr               = (TextView) layout.findViewById(R.id.textView_prayerTimes_asrTime);
        textViewMaghrib           = (TextView) layout.findViewById(R.id.textView_prayerTimes_maghribTime);
        textViewIsha              = (TextView) layout.findViewById(R.id.textView_prayerTimes_ishaTime);
        textViewQibla             = (TextView) layout.findViewById(R.id.textView_prayerTimes_qiblaTime);
        int i= 0;
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day1);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day2);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day3);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day4);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day5);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day6);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day7);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day8);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day9);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day10);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day11);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day12);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day13);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day14);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day15);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day16);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day17);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day18);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day19);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day20);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day21);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day22);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day23);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day24);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day25);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day26);
        linearLayoutDayArray[i++]         = (LinearLayout) layout.findViewById(R.id.day27);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day28);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day29);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day30);
        linearLayoutDayArray[i++]          = (LinearLayout) layout.findViewById(R.id.day31);

        Optional<Place> maybePlace = Place.fromBundle(getArguments());

        if (maybePlace.isDefined()) {
            place = maybePlace.get();
        }

        return layout;
    }

    @Override public void onPrayerTimesDownloaded(@NonNull List<PrayerTimesOfDay> prayerTimes) {
        if (!PrayerTimesOfDay.saveAllPrayerTimes(context, place, prayerTimes)) {
            changeStateTo(MultiStateView.VIEW_STATE_ERROR, RETRY_ACTION_DOWNLOAD);
            return;
        }

        LocalDate today = LocalDate.now();

        for (int i = 0, size = prayerTimes.size(); i < size; i++) {
            if (prayerTimes.get(i).date.equals(today)) {
                maybePrayerTimesOfDay = Optional.with(prayerTimes.get(i));
                break;
            }
        }

        if (maybePrayerTimesOfDay.isEmpty()) {
            Log.error(getClass(), "Did not find today's prayer times in downloaded prayer times!");

            changeStateTo(MultiStateView.VIEW_STATE_EMPTY, RETRY_ACTION_DOWNLOAD);
        } else {
            initializeUI();
        }
    }

    @Override public void onDownloadPrayerTimesFailed(Exception e) {
        Log.error(getClass(), e, "Failed to download prayer times for place '%s'!", place);
        changeStateTo(MultiStateView.VIEW_STATE_ERROR, RETRY_ACTION_DOWNLOAD);
    }

    @Override protected void changeStateTo(int newState, final int retryAction) {
        if (multiStateViewLayout != null) {
            switch (newState) {
                case MultiStateView.VIEW_STATE_CONTENT:
                    multiStateViewLayout.setViewState(newState);
                    break;

                case MultiStateView.VIEW_STATE_LOADING:
                case MultiStateView.VIEW_STATE_EMPTY:
                case MultiStateView.VIEW_STATE_ERROR:
                    multiStateViewLayout.setViewState(newState);

                    if (muezzinActivity != null) {
                        muezzinActivity.setTitle(R.string.applicationName);
                        muezzinActivity.setSubtitle("");
                    }

                    View layout = multiStateViewLayout.getView(newState);

                    if (layout != null) {
                        View fab = layout.findViewById(R.id.fab_retry);

                        if (fab != null) {
                            fab.setOnClickListener(v -> retry(retryAction));
                        }
                    }

                    break;
            }
        }
    }

    @Override protected void retry(int action) {
        switch (action) {
            case RETRY_ACTION_DOWNLOAD:
                changeStateTo(MultiStateView.VIEW_STATE_LOADING, 0);
                MuezzinAPI.get().getPrayerTimes(place, this);
                break;
        }
    }

    private void loadTodaysPrayerTimes() {
        changeStateTo(MultiStateView.VIEW_STATE_LOADING, 0);
// fatih
// !!!!!!
       // !!!!!!
        maybePrayerTimesOfDay = PrayerTimesOfDay.getPrayerTimesForToday(context, place);

/*{"2020-04-19":{"fajr":"04:12","shuruq":"05:39","dhuhr":"12:31","asr":"16:13","maghrib":"19:13","isha":"20:34","qibla":"00:00"}}
* */
        if (maybePrayerTimesOfDay.isEmpty()) {
            Log.debug(getClass(), "Today's prayer times for place '%s' wasn't found on database!", place);

            MuezzinAPI.get().getPrayerTimes(place, this);
        } else {
            Log.debug(getClass(), "Loaded today's prayer times for place '%s' from database!", place);

            initializeUI();
        }
    }

    private void initializeUI() {
        changeStateTo(MultiStateView.VIEW_STATE_CONTENT, 0);

        Optional<String> maybePlaceName = place.getPlaceName(context);

        if (maybePlaceName.isDefined()) {
            if (muezzinActivity != null) {
                String gregorianDate = LocalDate.now().toString(FULL_DATE_FORMATTTER);
                String hijriDate = getHijriDate();

                muezzinActivity.setTitle(maybePlaceName.get());
                muezzinActivity.setSubtitle(gregorianDate + " / " + hijriDate);
            }

            Optional<Place> maybeLastPlace = Pref.Places.getLastPlace(context);

            if (maybeLastPlace.isDefined() && !maybeLastPlace.get().equals(place)) {
                PrayerTimeReminder.reschedulePrayerTimeReminders(context);
            }
        }

        if (maybePrayerTimesOfDay.isDefined()) {
            PrayerTimesOfDay prayerTimes = maybePrayerTimesOfDay.get();


            PrayerTimeType currentPrayerTimeType  = prayerTimes.currentPrayerTimeType();
            if( currentPrayerTimeType == Fajr )
                textViewFajr.setBackgroundColor(0xff66ff66);
            else
                textViewFajr.setBackgroundColor(0);

            if( currentPrayerTimeType == Dhuhr )
                textViewDhuhr.setBackgroundColor(0xff66ff66);
            else
                textViewDhuhr.setBackgroundColor(0);

            if( currentPrayerTimeType == Asr )
                textViewAsr.setBackgroundColor(0xff66ff66);
            else
                textViewAsr.setBackgroundColor(0);

            if( currentPrayerTimeType == Maghrib )
                textViewMaghrib.setBackgroundColor(0xff66ff66);
            else
                textViewMaghrib.setBackgroundColor(0);

            if( currentPrayerTimeType == Isha )
                textViewIsha.setBackgroundColor(0xff66ff66);
            else
                textViewIsha.setBackgroundColor(0);

            if( currentPrayerTimeType == Shuruq )
                textViewShuruq.setBackgroundColor(0xff66ff66);
            else
                textViewShuruq.setBackgroundColor(0);

            textViewFajr.setText(prayerTimes.fajr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewDhuhr.setText(prayerTimes.dhuhr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewAsr.setText(prayerTimes.asr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewMaghrib.setText(prayerTimes.maghrib.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewIsha.setText(prayerTimes.isha.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewShuruq.setText(prayerTimes.shuruq.toString(PrayerTimesOfDay.TIME_FORMATTER));
            textViewQibla.setText(prayerTimes.qibla.toString(PrayerTimesOfDay.TIME_FORMATTER));

            SetMonthlyDays();
        }

        PrayerTimesWidgetBase.updateAllWidgets(context);
    }

    private void SetMonthlyDays() {
        //linearLayoutDayArray[0].

        ArrayList<PrayerTimesOfDay> prayerTimesForMonthly = PrayerTimesOfDay.getPrayerTimesForMonthly(context, place);
        for(int i = 0; i < 31; i++)
        {
            PrayerTimesOfDay prayerTimesOfDayTemp = (i < prayerTimesForMonthly.size()) ? prayerTimesForMonthly.get(i):null;
            SetDay(i, prayerTimesOfDayTemp);
        }

    }

    private void SetDay(int i, PrayerTimesOfDay prayerTimesOfDayTemp) {
        // Add textview 1
        if(prayerTimesOfDayTemp == null)
        {
            linearLayoutDayArray[i].setVisibility(View.GONE);
        }
        else {
            LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutParams layoutParams2 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1.5f;
            layoutParams2.weight = 1.0f;

            TextView textView1 = new TextView(linearLayoutDayArray[i].getContext());
            textView1.setLayoutParams(layoutParams);
            String gregorianDate = prayerTimesOfDayTemp.date.toString(DATE_MONTH_FORMATTTER);
            textView1.setText(gregorianDate);
           // textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView1);

            // Add textview 2
            TextView textView2 = new TextView(linearLayoutDayArray[i].getContext());
            textView2.setLayoutParams(layoutParams2);
            textView2.setText(prayerTimesOfDayTemp.fajr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView2);

            // Add textview 3
            TextView textView3 = new TextView(linearLayoutDayArray[i].getContext());
            textView3.setLayoutParams(layoutParams2);
            textView3.setText(prayerTimesOfDayTemp.shuruq.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView3.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView3);

            // Add textview 4
            TextView textView4 = new TextView(linearLayoutDayArray[i].getContext());
            textView4.setLayoutParams(layoutParams2);
            textView4.setText(prayerTimesOfDayTemp.dhuhr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView3.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView4);

            // Add textview 5
            TextView textView5 = new TextView(linearLayoutDayArray[i].getContext());
            textView5.setLayoutParams(layoutParams2);
            textView5.setText(prayerTimesOfDayTemp.asr.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView3.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView5);

            // Add textview 6
            TextView textView6 = new TextView(linearLayoutDayArray[i].getContext());
            textView6.setLayoutParams(layoutParams2);
            textView6.setText(prayerTimesOfDayTemp.maghrib.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView3.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView6);

            // Add textview 7
            TextView textView7 = new TextView(linearLayoutDayArray[i].getContext());
            textView7.setLayoutParams(layoutParams2);
            textView7.setText(prayerTimesOfDayTemp.isha.toString(PrayerTimesOfDay.TIME_FORMATTER));
            //textView3.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            // textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayoutDayArray[i].addView(textView7);
        }
    }

    private void updateRemainingTime() {
        if (maybePrayerTimesOfDay.isDefined()) {
            PrayerTimesOfDay prayerTimes = maybePrayerTimesOfDay.get();

            LocalTime nextPrayerTime  = prayerTimes.nextPrayerTime();
            String nextPrayerTimeName = PrayerTimesOfDay.prayerTimeLocalizedName(context, prayerTimes.nextPrayerTimeType());

            LocalTime remaining  = RemainingTime.to(nextPrayerTime);
            String remainingTime = remaining.toString(RemainingTime.FORMATTER);

            boolean isRemainingLessThan45Minutes = remaining.getHourOfDay() == 0 && remaining.getMinuteOfHour() < 45;
            int color = isRemainingLessThan45Minutes ? redTextColor : defaultTextColor;

            if (isAdded()) {
                textViewRemainingTimeInfo.setText(getString(R.string.prayerTimes_cardTitle_remainingTime, nextPrayerTimeName));
                textViewRemainingTime.setText(remainingTime);

                textViewRemainingTimeInfo.setTextColor(color);
                textViewRemainingTime.setTextColor(color);
            }
        }
    }

    private void scheduleRemainingTimeCounter() {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override public void run() {
                FragmentActivity activity = getActivity();

                if (activity != null) {
                    activity.runOnUiThread(() -> updateRemainingTime());
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void cancelRemainingTimeCounter() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        if (timerTask != null && timerTask.scheduledExecutionTime() > 0) {
            timerTask.cancel();
        }
    }

    private String getHijriDate() {
        LocalDateTime hijriNow = LocalDateTime.now(IslamicChronology.getInstance());
        String originalHijriDate = hijriNow.toString(FULL_DATE_PATTERN, Locale.getDefault());

        String hijriMonthName = getString(
            getResources().getIdentifier(
                "hijriMonth" + hijriNow.getMonthOfYear(),
                "string",
                getContext().getApplicationInfo().packageName
            )
        );

        return originalHijriDate.replaceAll("^(.+) (.+) (.+)$", "$1 " + hijriMonthName + " $3");
    }
}
