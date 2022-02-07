package dk.itu.moapd.lifecycle

import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidLastInteractionTest {

    @Test
    fun testExample() {
        assertTrue("Check Android is mentioned", LastInteraction().greeting().contains("Android"))
    }
}