package dk.itu.moapd.lifecycle

import kotlin.test.Test
import kotlin.test.assertTrue

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(LastInteraction().greeting().contains("iOS"), "Check iOS is mentioned")
    }
}