package ru.android4life.habittracker;

import java.util.Date;

/**
 * Created by neewy on 05.10.16.
 */

public class HabitPerformanceData {

    private Date day;
    private Integer plannedHabits;
    private Integer doneHabits;

    public HabitPerformanceData(Date day, Integer plannedHabits, Integer doneHabits) {
        this.day = day;
        this.plannedHabits = plannedHabits;
        this.doneHabits = doneHabits;
    }

    public HabitPerformanceData() {
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Integer getPlannedHabits() {
        return plannedHabits;
    }

    public void setPlannedHabits(Integer plannedHabits) {
        this.plannedHabits = plannedHabits;
    }

    public Integer getDoneHabits() {
        return doneHabits;
    }

    public void setDoneHabits(Integer doneHabits) {
        this.doneHabits = doneHabits;
    }

    public Float getDoneRatio() {
        return Float.valueOf(plannedHabits) / Float.valueOf(doneHabits);
    }
}
