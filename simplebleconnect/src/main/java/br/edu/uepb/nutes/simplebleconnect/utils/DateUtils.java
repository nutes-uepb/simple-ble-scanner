package br.edu.uepb.nutes.simplebleconnect.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Provides useful methods for handling date/time.
 * Copyright (c) 2018 NUTES/UEPB
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
public final class DateUtils {
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Get/Retrieve calendar instance.
     *
     * @return The Calendar.
     */
    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    /**
     * Returns the current datetime in format of format_date string passed as parameter.
     * If "format_date" is null the default value: "yyyy-MM-dd HH:mm:ss" will be used.
     *
     * @param format_date The datetime format
     * @return Datetime formatted
     */
    public static String getCurrentDatetime(String format_date) {
        if (format_date == null)
            format_date = "yyyy-MM-dd HH:mm:ss";

        Calendar calendar = GregorianCalendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns the current datetime in milliseconds.
     *
     * @return long
     */
    public static long getCurrentDatetime() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }

    /**
     * Returns the current timestamp.
     * - millis - long
     * - timezone - Object (TimeZone)
     * - value - String (yyyy-MM-dd HH:mm:ss)
     *
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject getCurrentTimestamp() throws JSONException {
        return new JSONObject().put("timezone", TimeZone.getDefault())
                .put("mills", DateUtils.getCurrentDatetime())
                .put("value", DateUtils.getCurrentDatetime(null)); // yyyy-MM-dd HH:mm:ss
    }

    /**
     * Convert a calendar using the format_date passed as parameter.
     *
     * @param calendar    Instância do calendario
     * @param format_date Formato da data/hora
     * @return Date/Time formatted
     */
    public static String calendarToString(Calendar calendar, String format_date) {
        if (format_date == null || calendar == null)
            return null;

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convert year, month and day using the format_date passed as parameter.
     *
     * @param year
     * @param month
     * @param day
     * @param format_date
     * @return
     */
    public static String dateToString(int year, int month, int day, String format_date) {
        if (format_date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendarToString(calendar, format_date);
    }

    /**
     * Returns array with the date values
     *
     * @param milliseconds
     * @return
     */
    public static int[] getDateValues(long milliseconds) {
        int[] dateValues = new int[6];
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        dateValues[0] = calendar.get(Calendar.YEAR);
        dateValues[1] = calendar.get(Calendar.MONTH);
        dateValues[2] = calendar.get(Calendar.DAY_OF_MONTH);
        dateValues[3] = calendar.get(Calendar.HOUR);
        dateValues[4] = calendar.get(Calendar.MINUTE);
        dateValues[5] = calendar.get(Calendar.SECOND);

        return dateValues;
    }

    /**
     * Retrieve a date/time passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds
     * @param format_date
     * @return String
     */
    public static String formatDate(long milliseconds, String format_date) {
        if (format_date == null)
            return null;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Format date in ISO 8601 for the format passed in the "format_date" parameter.
     * The default timezone will be used.
     *
     * @param str_date
     * @param format_date
     * @return String
     */
    public static String formatDateISO8601(String str_date, String format_date) {
        if (str_date == null || format_date == null)
            return null;

        TimeZone timeZone = TimeZone.getDefault();

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(DateUtils.fromISO8601(str_date));
    }

    public static String getCurrentDateISO8601() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_ISO_8601);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(new Date());
    }

    /**
     * Convert string datetime in UTC ISO 8601
     *
     * @param str_date
     * @return String
     */
    public static Date fromISO8601(String str_date) {
        if (str_date == null)
            return null;

        DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_ISO_8601);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return dateFormat.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add days to calendar.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static Calendar addDays(int days) {
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 0); // 0 hours
        c.set(Calendar.MINUTE, 0); // 0 minute
        c.set(Calendar.SECOND, 1); // 1 second

        return c;
    }

    /**
     * returns the current year in milliseconds.
     * Example YEAR-01-01 00:00:00
     *
     * @return long
     */
    public static long getCurrentYear() {
        Calendar c = GregorianCalendar.getInstance();

        c.set(Calendar.DAY_OF_YEAR, c.getActualMinimum(Calendar.DAY_OF_YEAR));
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));

        return c.getTimeInMillis();
    }

    /**
     * Validate date.
     *
     * @param date   String
     * @param format String
     * @return boolean
     */
    public static boolean isDateValid(String date, @Nullable String format) {
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd";

        try {
            DateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            df.parse(date);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Returns date passed as abbreviated parameter: MMMM dd, EEE
     * - If the phone language is "pt": Jan 18, 2018
     * - If for another: January 18, 2018
     *
     * @param dateMills long
     * @return String
     */
    public static String abbreviatedDate(Context context, long dateMills) {
        if (isToday(dateMills))
            return "tODAY";

        StringBuilder result = new StringBuilder();
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(dateMills);

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, EEE");
        return dateFormat.format(c.getTime());
    }

    /**
     * Returns date passed as abbreviated parameter: MMMM dd, EEE
     * - If the phone language is "pt": Jan 18, 2018
     * - If for another: January 18, 2018
     *
     * @param dateMills long
     * @return String
     */
    public static String getDayWeek(Context context, long dateMills) {
        if (isToday(dateMills))
            return "TODAY";

        StringBuilder result = new StringBuilder();
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(dateMills);

        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, EEE");
        return dateFormat.format(c.getTime());
    }

    /**
     * Checks if the date passed as parameter is today.
     *
     * @param dateMills long
     * @return boolean
     */
    public static boolean isToday(long dateMills) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c1.setTimeInMillis(dateMills);
        c2.setTimeInMillis(getCurrentDatetime());

        if (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c2.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
            return true;

        return false;
    }

    /**
     * Checks if the date passed as parameter is this year.
     *
     * @param dateMills long
     * @return boolean
     */
    public static boolean isYear(long dateMills) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c1.setTimeInMillis(dateMills);
        c2.setTimeInMillis(getCurrentDatetime());

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }

    /**
     * Checks if the date passed as parameter is this year.
     *
     * @param str_date
     * @return boolean
     */
    public static boolean isYear(String str_date) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTimeInMillis(getCurrentDatetime());

        Calendar c = Calendar.getInstance();
        c1.setTime(DateUtils.fromISO8601(str_date));

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }

    /**
     * Convert date in string format to milliseconds.
     *
     * @param date   {@link String}
     * @param format {@link String}
     * @return long
     */
    public static long getDateStringInMillis(String date, @Nullable String format) {
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd";

        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat(format);
            calendar.setTime(df.parse(date));
        } catch (ParseException e) {
        }

        return calendar.getTimeInMillis();
    }
}
