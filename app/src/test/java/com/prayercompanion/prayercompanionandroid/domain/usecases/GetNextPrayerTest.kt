package com.prayercompanion.prayercompanionandroid.domain.usecases

import android.location.Location
import com.google.common.truth.Truth
import com.prayercompanion.prayercompanionandroid.domain.Consts
import com.prayercompanion.prayercompanionandroid.domain.PrayersFakeRepository
import com.prayercompanion.prayercompanionandroid.domain.models.Prayer
import com.prayercompanion.prayercompanionandroid.domain.repositories.PrayersRepository
import com.prayercompanion.prayercompanionandroid.domain.usecases.prayers.GetNextPrayer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.*

@OptIn(ExperimentalCoroutinesApi::class)
class GetNextPrayerTest {

    private lateinit var usecase: GetNextPrayer
    private val prayersRepository: PrayersRepository = PrayersFakeRepository()
    private val clock: Clock = mockk()

    @Before
    fun setup() {
        usecase = GetNextPrayer(
            prayersRepository = prayersRepository,
            clock
        )
    }

    @Test
    fun `should return the correct next prayer FAJR`() = runTest {
        val time = LocalTime.of(2, 0, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.FAJR)
        }
    }

    @Test
    fun `should return the correct next prayer DUHA`() = runTest {
        val time = LocalTime.of(5, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.DUHA)
        }

    }

    @Test
    fun `should return the correct next prayer DHUHR`() = runTest {
        val time = LocalTime.of(7, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.DHUHR)
        }

    }

    @Test
    fun `should return the correct next prayer ASR`() = runTest {
        val time = LocalTime.of(12, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.ASR)
        }
    }

    @Test
    fun `should return the correct next prayer MAGHRIB`() = runTest {
        val time = LocalTime.of(15, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.MAGHRIB)
        }

    }

    @Test
    fun `should return the correct next prayer ISHA`() = runTest {
        val time = LocalTime.of(18, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.ISHA)
        }

    }

    @Test
    fun `should return the correct next prayer Next FAJR`() = runTest {
        val time = LocalTime.of(20, 30, 0)
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.FAJR)
        }
    }

    @Test
    fun `should return the correct next prayer DUHA at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.FAJR).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.DUHA)
        }

    }

    @Test
    fun `should return the correct next prayer DHUHR at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.DUHA).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.DHUHR)
        }

    }

    @Test
    fun `should return the correct next prayer ASR at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.DHUHR).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.ASR)
        }
    }

    @Test
    fun `should return the correct next prayer MAGHRIB at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.ASR).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.MAGHRIB)
        }

    }

    @Test
    fun `should return the correct next prayer ISHA at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.MAGHRIB).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.ISHA)
        }

    }

    @Test
    fun `should return the correct next prayer Next FAJR at the exact time of previous Salah`() = runTest {
        val time = PrayersFakeRepository.DEFAULT_DAY_PRAYERS_INFO.get(Prayer.ISHA).time
        stubClock(LocalDateTime.of(Consts.TODAY_DATE, time))
        val prayer = usecase.call(LOCATION, location)

        Truth.assertThat(prayer.isSuccess).isTrue()

        prayer.onSuccess {
            Truth.assertThat(it.prayer).isEqualTo(Prayer.FAJR)
        }
    }

    private fun stubClock(dateTime: LocalDateTime) {
        val newClock = Clock.fixed(
            dateTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
        )
        every { clock.instant() } returns newClock.instant()
        every { clock.zone } returns newClock.zone
    }

    companion object {
        private val LOCATION = Location("").apply {
            latitude = 31.963158
            longitude = 35.930359
        }
    }
}