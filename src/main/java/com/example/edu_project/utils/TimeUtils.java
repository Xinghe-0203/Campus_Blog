package com.example.edu_project.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);

        if (seconds < 60) {
            return "刚刚";
        }

        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "分钟前";
        }

        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "小时前";
        }

        long days = hours / 24;
        if (days < 30) {
            return days + "天前";
        }

        long months = days / 30;
        if (months < 12) {
            return months + "个月前";
        }

        long years = days / 365;
        return years + "年前";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}