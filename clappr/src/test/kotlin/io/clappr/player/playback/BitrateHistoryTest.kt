package io.clappr.player.playback

import io.clappr.player.BuildConfig
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class BitrateHistoryTest {
    private lateinit var bitrateHistoryUnderTest: BitrateHistory

    @Before
    fun setup() {
        bitrateHistoryUnderTest = BitrateHistory()
    }

    @Test
    fun shouldListBitrate() {
        bitrateHistoryUnderTest.addBitrateLog(0)
        bitrateHistoryUnderTest.addBitrateLog(0)
        bitrateHistoryUnderTest.addBitrateLog(0)
        bitrateHistoryUnderTest.addBitrateLog(0)

        assertEquals(4, bitrateHistoryUnderTest.bitrateLogList.size, "Bitrate log list size incorrect")
    }

    @Test
    fun shouldNotAddBitrateWhenNull() {
        bitrateHistoryUnderTest.addBitrateLog(null)

        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldIgnoreBitrateWhenNullFromList() {
        bitrateHistoryUnderTest.addBitrateLog(0)
        bitrateHistoryUnderTest.addBitrateLog(null)
        bitrateHistoryUnderTest.addBitrateLog(0)
        bitrateHistoryUnderTest.addBitrateLog(0)

        assertEquals(3, bitrateHistoryUnderTest.bitrateLogList.size)
    }

    @Test
    fun shouldSetBitrateTimeStampAsStartTime() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        assertEquals(2, bitrateHistoryUnderTest.bitrateLogList[0].start)
    }

    @Test
    fun shouldSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        bitrateHistoryUnderTest.addBitrateLog(0, 3)
        assertEquals(3, bitrateHistoryUnderTest.bitrateLogList[0].end)
    }

    @Test
    fun shouldNotSetBitrateEndTime() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList[0].end)
    }

    @Test
    fun shouldSetBitrateTimeAsDifferenceBetweenFirstAndLastTimes() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        bitrateHistoryUnderTest.addBitrateLog(0, 3)
        assertEquals(1, bitrateHistoryUnderTest.bitrateLogList[0].time)
    }

    @Test
    fun shouldNotSetBitrateTime() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        assertEquals(0, bitrateHistoryUnderTest.bitrateLogList[0].time)
    }

    @Test
    fun shouldEqualFirstBitrateEndTimeWithSecondBitrateStartTime() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        bitrateHistoryUnderTest.addBitrateLog(0, 3)
        assertEquals(bitrateHistoryUnderTest.bitrateLogList[0].end ,bitrateHistoryUnderTest.bitrateLogList[1].start)
    }

    @Test
    fun shouldEqualFirstBitrateEndTimeWithSecondBitrateStartTimeWhenNullBitrateAdded() {
        bitrateHistoryUnderTest.addBitrateLog(0, 2)
        bitrateHistoryUnderTest.addBitrateLog(null, 500)
        bitrateHistoryUnderTest.addBitrateLog(0, 3)
        assertEquals(bitrateHistoryUnderTest.bitrateLogList[0].end ,bitrateHistoryUnderTest.bitrateLogList[1].start)
    }

    @Test
    fun shouldSumBitrateWithTime() {
        bitrateHistoryUnderTest.addBitrateLog(90, 2)
        bitrateHistoryUnderTest.addBitrateLog(100, 3)
        bitrateHistoryUnderTest.addBitrateLog(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]


        val sumOfAllBitrateWithTime = ((firstBitrateLog.bitrate * firstBitrateLog.time)
                + (secondBitrateLog.bitrate * secondBitrateLog.time)
                + (thirdBitrateLog.bitrate * thirdBitrateLog.time))

        assertEquals(sumOfAllBitrateWithTime, bitrateHistoryUnderTest.sumOfAllBitrateWithTime())
    }

    @Test
    fun shouldSumTotalTime() {
        bitrateHistoryUnderTest.addBitrateLog(90, 2)
        bitrateHistoryUnderTest.addBitrateLog(100, 3)
        bitrateHistoryUnderTest.addBitrateLog(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        val firstBitrateLog = bitrateList.first()
        val secondBitrateLog = bitrateList[1]
        val thirdBitrateLog = bitrateList[2]

        bitrateList.last().time = 5

        val totalTimeSum = firstBitrateLog.time + secondBitrateLog.time + thirdBitrateLog.time
        assertEquals(totalTimeSum, bitrateHistoryUnderTest.totalBitrateHistoryTime())
    }

    @Test
    fun shouldSetLastBitrateTimeAndDivideSums() {

        bitrateHistoryUnderTest.addBitrateLog(90, 2)
        bitrateHistoryUnderTest.addBitrateLog(100, 3)
        bitrateHistoryUnderTest.addBitrateLog(110, 4)

        val bitrateList = bitrateHistoryUnderTest.bitrateLogList

        bitrateList.last().time = 5
        val averageBitrate =  bitrateHistoryUnderTest.sumOfAllBitrateWithTime() / bitrateHistoryUnderTest.totalBitrateHistoryTime()

        assertEquals(averageBitrate, bitrateHistoryUnderTest.averageBitrate(5))
    }
}