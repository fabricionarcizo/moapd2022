package dk.itu.moapd.lifecycle

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonLastInteractionTest {

    @Test
    fun testExample() {
        assertTrue(LastInteraction().greeting().contains("Hello"), "Check 'Hello' is mentioned")
    }
}