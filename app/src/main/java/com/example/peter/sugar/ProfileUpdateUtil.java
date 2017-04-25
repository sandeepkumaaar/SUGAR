package com.example.peter.sugar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Funkyaron on 04.04.2017. <p/>
 * Helper class that provides static methods to handle time-dependent enabling
 * or disabling of SUGAR-Profiles.
 */

public class ProfileUpdateUtil {

    /**
     * Determines when the given profile should be enabled the next time and sets the
     * correspondent "alarm".
     *
     * @param context Needed for Intent, AlarmManager etc.
     * @param profile The profile for which the action should be performed
     */
    public static void setNextEnable(Context context, Profile profile) {

        String name = profile.getName();
        boolean[] days = profile.getDays();
        int[] start = profile.getStart();

        // First check if any day of week should apply
        for (boolean day : days)
        {
            if (day)
            {
                break;
            }
            return;
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, EnableProfileReceiver.class);
        intent.addCategory(name);

        PendingIntent pending = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        long currentTime = cal.getTimeInMillis();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);

        cal.set(Calendar.HOUR_OF_DAY, start[0]);
        cal.set(Calendar.MINUTE, start[1]);
        long targetTime = cal.getTimeInMillis();

        if (days[toIndex(currentDay)] && currentTime < targetTime)
        {
            // Don't change target time
        }
        else
        {
            int daysToAdd = 0;
            int i = toIndex(currentDay);
            int j = 0;
            while (j <= 6)
            {
                daysToAdd++;
                i = (i + 1) % 7;
                j++;
                if (days[i])
                    break;
            }

            cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
            targetTime = cal.getTimeInMillis();
        }

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, targetTime, pending);
    }


    /**
     * Determines when the given profile sould be disabled the next time and sets the
     * correspondent "alarm".
     *
     * @param context Needed for Intent, AlarmManager etc.
     * @param profile The profile for which the action should be performed
     */
    public static void setNextDisable(Context context, Profile profile) {

        String name = profile.getName();
        boolean[] days = profile.getDays();
        int[] end = profile.getEnd();

        // First check if any day of week should apply
        for (boolean day : days)
        {
            if (day)
            {
                break;
            }
            return;
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DisableProfileReceiver.class);
        intent.addCategory(name);

        PendingIntent pending = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        long currentTime = cal.getTimeInMillis();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);

        cal.set(Calendar.HOUR_OF_DAY, end[0]);
        cal.set(Calendar.MINUTE, end[1]);
        long targetTime = cal.getTimeInMillis();

        if (profile.getDays()[toIndex(currentDay)] && currentTime < targetTime)
        {
            // Don't change target time
        }
        else
        {
            int daysToAdd = 0;
            int i = toIndex(currentDay);
            int j = 0;
            while (j <= 6)
            {
                daysToAdd++;
                i = (i + 1) % 7;
                j++;
                if (days[i])
                    break;
            }

            cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
            targetTime = cal.getTimeInMillis();
        }

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, targetTime, pending);
    }



    /**
     * Checks if the given profile should currently be enabled or not and updates its status.
     *
     * @param profile The profile which should be initialized
     */
    public static void updateProfileStatus(Profile profile) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        long currentTime = cal.getTimeInMillis();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);

        cal.set(Calendar.HOUR_OF_DAY, profile.getStart()[0]);
        cal.set(Calendar.MINUTE, profile.getStart()[1]);
        long startTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, profile.getEnd()[0]);
        cal.set(Calendar.MINUTE, profile.getEnd()[1]);
        long endTime = cal.getTimeInMillis();

        boolean[] days = profile.getDays();

        if (days[toIndex(currentDay)] == false) {
            disable(profile);
        } else if (startTime <= currentTime && endTime >= currentTime) {
            enable(profile);
        } else {
            disable(profile);
        }
    }



    /**
     * Enables the given profile. If enabled, the profile's blocklist applies on
     * incoming calls.
     *
     * @param profile The profile which should be enabled
     */
    public static void enable(Profile profile) {
        //TODO: enable profile -> XMLProfileUpdater?
    }



    /**
     * Disables the given profile. If disabled, the profile's blocklist doesn't
     * affect incoming calls.
     *
     * @param profile The profile which should be disabled
     */
    public static void disable(Profile profile) {
        //TODO: Disable profile -> XMLProfileUpdater?
    }



    /**
     * Converts constant field values from java.util.Calendar to array-index,
     * beginning from monday. <p/><p/>
     * Calendar.MONDAY = 2 -> 0 <p/>
     * Calendar.TUESDAY = 3 -> 1 <p/>
     * ... <p/>
     * Calendar.SATURDAY = 7 -> 5 <p/>
     * Calendar.SUNDAY = 1 -> 6 <p/>
     *
     * @param calendarDay Constant field value from java.util.Calendar
     * @return Index that can be used for an array, beginning from monday
     */
    public static int toIndex(int calendarDay) {
        return (calendarDay + 5) % 7;
    }
}
