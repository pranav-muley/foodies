package com.feastora.food_ordering.Utility;

public class ThreadContextUtils {

    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<Long> tableNumber = new ThreadLocal<>();

    public static void setUserId(String uid) {
        userId.set(uid);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void setTableNumber(Long tableNum) {
        tableNumber.set(tableNum);
    }

    public static Long getTableNumber() {
        return tableNumber.get();
    }

    public static void clear() {
        userId.remove();
        tableNumber.remove();
    }
}
