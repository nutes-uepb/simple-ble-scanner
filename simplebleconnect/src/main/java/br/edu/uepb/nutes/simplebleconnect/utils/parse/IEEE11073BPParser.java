package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Parse for blood pressure.
 * Copyright (c) 2018 NUTES/UEPB
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
public class IEEE11073BPParser {

    /**
     * Parse for IEEE 11073 device blood pressure.
     * Supported Models: OMRON BP792IT.
     *
     * FORMAT return JOSN:
     * { systolic: int,
     *   diastolic: int,
     *   map: double
     *   pulse: int,
     *   pulse: int,
     *   systolicUnit: string,
     *   diastolicUnit: string,
     *   pulseUnit: string,
     *   timestamp: long }
     *
     * @param data xml
     * @return JSONObject json
     * @throws JSONException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static JSONObject parse(String data) throws JSONException, XmlPullParserException, IOException {
        XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
        xmlParser.setInput(new StringReader(data));

        Calendar timestamp = GregorianCalendar.getInstance();
        JSONObject result = new JSONObject();

        boolean end = false;
        String systolic = null,
                diastolic = null,
                map = null, // Mean Arterial Pressure (MAP)
                heartRate = null, // Pulse
                pressureUnit = null,
                heartRateUnit = null,
                century = null,
                year = null,
                month = null,
                day = null,
                hour = null,
                min = null,
                sec = null,
                sec_fractions = null;

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT && !end) {
            String name = null;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = xmlParser.getName();
                    if (name.equalsIgnoreCase("meta")) {
                        String attributeValue = xmlParser.getAttributeValue(null, "name");
                        if (attributeValue.equalsIgnoreCase("unit")) {
                            if (pressureUnit == null)
                                pressureUnit = xmlParser.nextText();
                            else if (heartRateUnit == null)
                                heartRateUnit = xmlParser.nextText();
                        }
                    } else if (name.equalsIgnoreCase("value")) {
                        if (systolic == null)
                            systolic = xmlParser.nextText();
                        else if (diastolic == null)
                            diastolic = xmlParser.nextText();
                        else if (map == null)
                            map = xmlParser.nextText();
                        else if (century == null)
                            century = xmlParser.nextText();
                        else if (year == null)
                            year = "20".concat(xmlParser.nextText());
                        else if (month == null)
                            month = xmlParser.nextText();
                        else if (day == null)
                            day = xmlParser.nextText();
                        else if (hour == null)
                            hour = xmlParser.nextText();
                        else if (min == null)
                            min = xmlParser.nextText();
                        else if (sec == null)
                            sec = xmlParser.nextText();
                        else if (sec_fractions == null)
                            sec_fractions = xmlParser.nextText();
                        else if (heartRate == null)
                            heartRate = xmlParser.nextText();
                        else
                            end = true;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            eventType = xmlParser.next();
        }

        /**
         * Device timestamp
         */
        timestamp.set(
                Integer.valueOf(year),
                Integer.valueOf(month) - 1,
                Integer.valueOf(day),
                Integer.valueOf(hour),
                Integer.valueOf(min),
                Integer.valueOf(sec)
        );

        /**
         * Populating the JSON
         */
        result.put("systolic", Double.parseDouble(systolic));
        result.put("diastolic",Double.parseDouble(diastolic));
        result.put("map", Double.parseDouble(map));
        result.put("heartRate", Double.parseDouble(heartRate));
        result.put("systolicUnit", pressureUnit);
        result.put("diastolicUnit", pressureUnit);
        result.put("heartRateUnit", heartRateUnit);
        result.put("timestamp", timestamp.getTimeInMillis());

        return result;
    }
}
