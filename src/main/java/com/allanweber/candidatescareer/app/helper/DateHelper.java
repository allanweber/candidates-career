package com.allanweber.candidatescareer.app.helper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("PMD")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateHelper {
    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date getUTCDatetimeAsDate() {
        return stringDateToDate(getUTCDatetimeAsString());
    }

    public static String getUTCDatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static Date stringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            dateToReturn = dateFormat.parse(StrDate);
        } catch (ParseException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid date");
        }

        return dateToReturn;
    }
}
