/*
 * Copyright (c) 2017 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.privacymonitor


import com.duckduckgo.app.privacymonitor.model.TermsOfService
import com.duckduckgo.app.trackerdetection.model.TrackerNetwork
import com.duckduckgo.app.trackerdetection.model.TrackingEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


class SiteMonitorTest {

    companion object {
        private const val document = "http://example.com"

        private const val tracker = "http://standalonetracker.com/script.js"
        private const val networkATracker = "http://networkAtracker.com/script.js"
        private const val networkBTracker = "http://networkBtracker.com/script.js"
        private const val majorNetworkATracker = "http://majorNetworkAtracker.com/script.js"
        private const val majorNetworkBTracker = "http://majorNetworkBtracker.com/script.js"

        private val networkA = TrackerNetwork("NetworkA", "networkA.com")
        private val networkB = TrackerNetwork("NetworkB", "networkB.com")
        private val majorNetworkA = TrackerNetwork("MajorNetworkA", "majorNetworkA.com", 0, true)
        private val majorNetworkB = TrackerNetwork("MajorNetworkB", "majorNetworkB.com", 0, true)
    }

    @Test
    fun whenSiteMonitorCreatedThenUrlIsCorrect() {
        val testee = SiteMonitor(document)
        assertEquals(document, testee.url)
    }

    @Test
    fun whenSiteMonitorCreatedWithoutTermsThenTermsAreGenerated() {
        val testee = SiteMonitor(document)
        assertNotNull(testee.termsOfService)
    }

    @Test
    fun whenSiteMonitorCreatedWithTermsThenTermsAreSet() {
        val terms = TermsOfService()
        val testee = SiteMonitor(document, terms)
        assertEquals(terms, testee.termsOfService)
    }

    @Test
    fun whenSiteMonitorCreatedThenTrackerCountIsZero() {
        val testee = SiteMonitor(document)
        assertEquals(0, testee.trackerCount)
    }

    @Test
    fun whenSiteMonitorCreatedThenNetworkCountIsZero() {
        val testee = SiteMonitor(document)
        assertEquals(0, testee.networkCount)
    }

    @Test
    fun whenTrackersAreDetectedThenTrackerCountIsIncremented() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(tracker, document, null, true))
        testee.trackerDetected(TrackingEvent(tracker, document, null, true))
        assertEquals(2, testee.trackerCount)
    }

    @Test
    fun whenUniqueTrackerNetworksAreDetectedThenNetworkCountIsIncrementedEachTime() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(networkATracker, document, networkA, true))
        testee.trackerDetected(TrackingEvent(networkBTracker, document, networkB, true))
        assertEquals(2, testee.networkCount)
    }

    @Test
    fun whenDuplicateTrackerNetworksDetectedThenNetworkCountIsIncrementedOnlyFirstTime() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(networkATracker, document, networkA, true))
        testee.trackerDetected(TrackingEvent(networkATracker, document, networkA, true))
        assertEquals(1, testee.networkCount)
    }

    @Test
    fun whenNonNetworkTrackersAreDetectedThenNetworkCountIsNotIncremented() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(tracker, document, null, true))
        testee.trackerDetected(TrackingEvent(tracker, document, null, true))
        assertEquals(0, testee.networkCount)
    }

    @Test
    fun whenUniqueMajorTrackerNetworksAreDetectedThenMajorNetworkCountIsIncrementedEachTime() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(majorNetworkATracker, document, majorNetworkA, true))
        testee.trackerDetected(TrackingEvent(majorNetworkBTracker, document, majorNetworkB, true))
        assertEquals(2, testee.majorNetworkCount)
    }

    @Test
    fun whenDuplicateMajorTrackerNetworksDetectedThenNetworkCountIsIncrementedOnlyFirstTime() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(majorNetworkATracker, document, majorNetworkA, true))
        testee.trackerDetected(TrackingEvent(majorNetworkATracker, document, majorNetworkA, true))
        assertEquals(1, testee.majorNetworkCount)
    }

    @Test
    fun whenNonMajorNetworkTrackersAreDetectedThenMajorNetworkCountIsNotIncremented() {
        val testee = SiteMonitor(document)
        testee.trackerDetected(TrackingEvent(tracker, document, networkA, true))
        testee.trackerDetected(TrackingEvent(tracker, document, networkB, true))
        assertEquals(0, testee.majorNetworkCount)
    }
}