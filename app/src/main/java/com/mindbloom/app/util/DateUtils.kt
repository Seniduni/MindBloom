package com.mindbloom.app.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.map

object DateUtils {

    private val iso: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): LocalDate = LocalDate.now()

    fun todayIso(): String = today().format(iso)

    fun iso(date: LocalDate): String = date.format(iso)

    fun parse(value: String): LocalDate = LocalDate.parse(value, iso)

    /** "Saturday, 15 August" — used on the dashboard header. */
    fun longDate(date: LocalDate = today()): String =
        date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH))

    /** "Saturday, 15 August 2026" — used on the journal header. */
    fun longDateWithYear(date: LocalDate = today()): String =
        date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH))

    /** "14 August 2026" — used on journal list rows. */
    fun mediumDate(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))

    /** "August 2026" — calendar and statistics headings. */
    fun monthYear(month: YearMonth): String =
        month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))

    /** Monday of the week containing [date]. */
    fun startOfWeek(date: LocalDate = today()): LocalDate =
        date.minusDays(((date.dayOfWeek.value + 6) % 7).toLong())

    /** The seven dates of the current week, Monday first. */
    fun currentWeek(date: LocalDate = today()): List<LocalDate> {
        val monday = startOfWeek(date)
        return (0..6).map { monday.plusDays(it.toLong()) }
    }

    val weekDayInitials = listOf("M", "T", "W", "T", "F", "S", "S")

    /** "Good Morning" / "Good Afternoon" / "Good Evening". */
    fun greeting(now: LocalTime = LocalTime.now()): String = when (now.hour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
