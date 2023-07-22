package com.example.VacationCalculator.services;


import com.example.VacationCalculator.models.enums.Holidays;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

@Service
public class VacationCalculatorServices {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            .appendOptional(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            .toFormatter();


    public String calculateVacationPay(double salary, int vacation, String vacationStart, String vacationEnd){

        if (!checkDate(vacationStart)) return "Неверная начальная дата отпуска";
        if (!checkDate(vacationEnd)) return "Неверная конечная дата отпуска";
        if(!LocalDate.parse(vacationStart, formatter).isBefore(LocalDate.parse(vacationEnd, formatter)))
            return "Начальная дата должна быть меньше конечной";
        if(salary<0) return "salary не может быть меньше 0";
        if(vacation<0) return "vacation не может быть меньше 0";

        long yearSalary = (long)(salary*100);
        long daySalary = yearSalary/365;
        long resVacation = daySalary*(vacation - numberOfUnpaidDays(LocalDate.parse(vacationStart, formatter), LocalDate.parse(vacationEnd, formatter)));

        return String.valueOf((double) resVacation/100);
    }

    public String calculateVacationPay(double salary, int vacation){

        if(salary<0) return "salary не может быть меньше 0";
        if(vacation<0) return "vacation не может быть меньше 0";

        long yearSalary = (long)(salary*100);
        long daySalary = yearSalary/365;
        long resVacation = daySalary*vacation;

        return String.valueOf((double) resVacation/100);
    }

    private boolean checkDate(String date){
        try {
            LocalDate.parse(date, formatter);
            return true;
        }
        catch (DateTimeParseException e){
            return false;
        }
    }

    private int numberOfUnpaidDays(LocalDate vacationStart, LocalDate vacationEnd){
        int count=0;

        for(LocalDate date =vacationStart; date.isBefore(vacationEnd.plusDays(1)); date = date.plusDays(1)){
            MonthDay monthDay = MonthDay.of(date.getMonth(),date.getDayOfMonth());
            if(Arrays.stream(Holidays.values()).anyMatch(holidays -> holidays.getMonthDay().equals(monthDay))
                    || date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY){
                count++;
            }
        }
        return count;
    }

}
