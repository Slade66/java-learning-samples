package com.slade66;

import java.time.LocalDate;

public class Calendar {
    public static void printCurrentMonthCalendar() {
        LocalDate date = LocalDate.now();
        date = date.minusDays(date.getDayOfMonth() - 1);
        System.out.printf("%d年%d月\n", date.getYear(), date.getMonthValue());
        System.out.println("一\t二\t三\t四\t五\t六\t日");
        int dayOfWeek = date.getDayOfWeek().getValue();
        for (int i = 0; i < dayOfWeek - 1; i++) {
            System.out.print('\t');
        }
        int currMonth = date.getMonthValue();
        while (date.getMonthValue() == currMonth) {
            System.out.printf("%d\t", date.getDayOfMonth());
            if (date.getDayOfWeek().getValue() == 7) {
                System.out.println();
            }
            date = date.plusDays(1);
        }
    }

    public static void main(String[] args) {
        Calendar.printCurrentMonthCalendar();
    }
}
