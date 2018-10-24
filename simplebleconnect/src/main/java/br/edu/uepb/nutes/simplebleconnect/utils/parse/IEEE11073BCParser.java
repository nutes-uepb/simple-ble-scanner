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
 * Parse for body composition.
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
public class IEEE11073BCParser {
    /**
     * Parse for IEEE 11073 device blood pressure.
     * Supported Models: OMRON HBF-206IT.
     *
     * FORMAT return JSON:
     * { bodyMass: double,
     *   height: int,
     *   bmi: double
     *   bodyFat: double,
     *   rmr: int,
     *   visceralFat: double,
     *   bodyAge: int,
     *   muscleMass: double,
     *   bodyMassUnit: string,
     *   heightUnit: string,
     *   bmiUnit: string,
     *   bodyFatUnit: string,
     *   rmrUnit: string,
     *   visceralFatUnit: string,
     *   bodyAgeUnit: string,
     *   muscleMassUnit: string,
     *   timestamp: long }
     *
     * @param data xml
     * @return JSONObject json
     * @throws JSONException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static JSONObject parse(String data) throws JSONException, XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlParser = factory.newPullParser();
        Calendar timestamp = GregorianCalendar.getInstance();

        boolean end = false, ignore = false;
        int attrDateCount = 0;

        String bodyMass = null,
                height = null,
                bmi = null, // IMC
                bodyFat = null,
                rmr = null,
                visceralFat = null,
                bodyAge = null,
                muscleMass = null,
                bodyMassUnit = null,
                heightUnit = null,
                bmiUnit = null,
                bodyFatUnit = null,
                rmrUnit = null,
                visceralFatUnit = null,
                bodyAgeUnit = null,
                muscleMassUnit = null,
                century = null,
                year = null,
                month = null,
                day = null,
                hour = null,
                min = null,
                sec = null,
                sec_fractions = null;

        xmlParser.setInput(new StringReader(data));
        int eventType = xmlParser.getEventType();

        JSONObject result = new JSONObject();

        while (eventType != XmlPullParser.END_DOCUMENT && !end) {
            String name = null;
            boolean metadata = false;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = xmlParser.getName();
                    if (name.equalsIgnoreCase("meta")) {
                        String attributeValue = xmlParser.getAttributeValue(null, "name");
                        if (attributeValue.equalsIgnoreCase("unit")) {
                            if (bodyMassUnit == null)
                                bodyMassUnit = xmlParser.nextText();
                            else if (heightUnit == null)
                                heightUnit = xmlParser.nextText();
                            else if (bmiUnit == null)
                                bmiUnit = xmlParser.nextText();
                            else if (bodyFatUnit == null)
                                bodyFatUnit = xmlParser.nextText();
                            else if (rmrUnit == null)
                                rmrUnit = xmlParser.nextText();
                            else if (visceralFatUnit == null)
                                visceralFatUnit = xmlParser.nextText();
                            else if (bodyAgeUnit == null)
                                bodyAgeUnit = xmlParser.nextText();
                            else if (muscleMassUnit == null)
                                muscleMassUnit = xmlParser.nextText();
                        }
                    } else if (name.equalsIgnoreCase("value")) {
                        if (ignore) {
                            attrDateCount += 1;

                            if (attrDateCount == 8) {
                                ignore = false;
                                attrDateCount = 0;
                            }
                            break;
                        }

                        if (bodyMass == null)
                            bodyMass = xmlParser.nextText();
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
                        else if (height == null) {
                            height = xmlParser.nextText();
                            ignore = true;
                        } else if (bmi == null) {
                            bmi = xmlParser.nextText();
                            ignore = true;
                        } else if (bodyFat == null) {
                            bodyFat = xmlParser.nextText();
                            ignore = true;
                        } else if (rmr == null) {
                            rmr = xmlParser.nextText();
                            ignore = true;
                        } else if (visceralFat == null) {
                            visceralFat = xmlParser.nextText();
                            ignore = true;
                        } else if (bodyAge == null) {
                            bodyAge = xmlParser.nextText();
                            ignore = true;
                        } else if (muscleMass == null) {
                            muscleMass = xmlParser.nextText();
                            ignore = true;
                        } else
                            end = true;
                    }
                    break;
                case XmlPullParser.END_TAG:
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
        result.put("bodyMass", Double.parseDouble(bodyMass));
        result.put("height", (int) Double.parseDouble(height));
        result.put("bmi", Double.parseDouble(bmi));
        result.put("bodyFat", Double.parseDouble(bodyFat));
        result.put("rmr", (int) Double.parseDouble(rmr));
        result.put("visceralFat", Double.parseDouble(visceralFat));
        result.put("bodyAge", (int) Double.parseDouble(bodyAge));
        result.put("muscleMass", Double.parseDouble(muscleMass));
        result.put("bodyMassUnit", bodyMassUnit);
        result.put("heightUnit", heightUnit);
        result.put("bmiUnit", bmiUnit);
        result.put("bodyFatUnit", bodyFatUnit);
        result.put("rmrUnit", rmrUnit);
        result.put("visceralFatUnit", visceralFatUnit);
        result.put("bodyAgeUnit", bodyAgeUnit);
        result.put("muscleMassUnit", muscleMassUnit);
        result.put("timestamp", timestamp.getTimeInMillis());

        return result;
    }
}
