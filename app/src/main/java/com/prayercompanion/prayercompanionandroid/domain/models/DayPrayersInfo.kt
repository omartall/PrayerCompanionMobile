package com.prayercompanion.prayercompanionandroid.domain.models

import com.prayercompanion.prayercompanionandroid.domain.extensions.now
import com.prayercompanion.shared.domain.models.Prayer
import com.prayercompanion.shared.domain.models.PrayerStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

data class DayPrayersInfo constructor(
    val prayers: List<PrayerInfo>
) {

    init {
        require(prayers.map { it.prayer } == Prayer.values().toList()) {
            "DayPrayersInfo created on wrong prayers ${prayers.map { it.prayer }}"
        }
    }

    fun get(prayer: Prayer): PrayerInfo {
        return prayers.find { it.prayer == prayer } ?: throw Exception("prayer not found")
    }

    companion object {
        val Default = DayPrayersInfo(
            listOf(
                PrayerInfo(
                    prayer = Prayer.FAJR,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                ),
                PrayerInfo(
                    prayer = Prayer.DUHA,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                ),
                PrayerInfo(
                    prayer = Prayer.DHUHR,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                ),
                PrayerInfo(
                    prayer = Prayer.ASR,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                ),
                PrayerInfo(
                    prayer = Prayer.MAGHRIB,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                ),
                PrayerInfo(
                    prayer = Prayer.ISHA,
                    dateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
                    selectedStatus = PrayerStatus.None,
                    statusesWithTimeRanges = emptyList()
                )
            )
        )
    }
}