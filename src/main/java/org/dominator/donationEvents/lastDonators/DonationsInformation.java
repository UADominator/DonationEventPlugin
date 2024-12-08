package org.dominator.donationEvents.lastDonators;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DonationsInformation {
    private final Double amount;
    private final String name;
    private final String message;
    private final DateTime dateTime;

    public DonationsInformation(double amount, String name, String message, String dateTime) {
        this.amount = amount;
        this.name = name;
        this.message = message;
        this.dateTime = new DateTime(dateTime);
    }

    public DonationsInformation(JsonObject json) {
        this(
                json.get("amount").getAsDouble(),
                json.get("clientName").getAsString(),
                json.has("message") ? json.get("message").getAsString() : "",
                json.get("createdAt").getAsString()
        );
    }

    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeString(){
        return dateTime.toString();
    }

    public boolean isOlder(DonationsInformation other){
        return this.dateTime.isOlder(other.dateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonationsInformation that = (DonationsInformation) o;
        return Objects.equals(amount, that.amount) && Objects.equals(name, that.name) && Objects.equals(message, that.message) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, name, message, dateTime);
    }

    public static class DateTime {
        int year;
        int month;
        int day;

        int hour;
        int minute;
        int seconds;

        public DateTime(int year, int month, int day, int hour, int minute, int seconds) {
            setDayTime(year, month, day, hour, minute, seconds);
        }

        public DateTime(String dateTimeString){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            setDayTime(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        }

        public void setDayTime(int year, int month, int day, int hour, int minute, int seconds) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.seconds = seconds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DateTime dateTime = (DateTime) o;
            return year == dateTime.year && month == dateTime.month && day == dateTime.day && hour == dateTime.hour && minute == dateTime.minute && seconds == dateTime.seconds;
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, month, day, hour, minute, seconds);
        }

        public boolean isOlder(DateTime other){
            if (this.year > other.year) {
                return true;
            } else if (this.year < other.year) {
                return false;
            }

            if (this.month > other.month) {
                return true;
            } else if (this.month < other.month) {
                return false;
            }

            if (this.day > other.day) {
                return true;
            } else if (this.day < other.day) {
                return false;
            }

            if (this.hour > other.hour) {
                return true;
            } else if (this.hour < other.hour) {
                return false;
            }

            if (this.minute > other.minute) {
                return true;
            } else if (this.minute < other.minute) {
                return false;
            }

            return this.seconds > other.seconds;
        }

        public boolean isEqual(DateTime other){
            return this.toString().equals(other.toString());
        }

        public String toString() {
            return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, seconds);
        }
    }
}
