package dk.itu.moapd.androidtesting

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle.State
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.*

import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivityTest_createMainActivity() {
        val scenario = activityScenarioRule.scenario
        scenario.moveToState(State.RESUMED)
    }

    @Test
    fun mainActivityTest_readNameEditText() {
        onView(withId(R.id.name_edit_text))
            .check(matches(withText("")))
    }

    @Test
    fun mainActivityTest_readEmailEditText() {
        onView(withId(R.id.email_edit_text))
            .check(matches(withText("")))
    }

    @Test
    fun mainActivityTest_readPasswordEditText() {
        onView(withId(R.id.password_edit_text))
            .check(matches(withText("")))
    }

    @Test
    fun mainActivityTest_clickSendButton() {
        // Virtual application context.
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val error = targetContext.resources.getString(R.string.error)

        // Evaluate the name EditText.
        onView(withId(R.id.name_edit_text))
            .check(matches(hasNoErrorText()))
        onView(withId(R.id.send_button))
            .perform(click())

        onView(withId(R.id.name_edit_text))
            .check(matches(hasErrorText(error)))
        onView(withId(R.id.name_edit_text))
            .perform(clearText(), typeText("Name"))
        onView(withId(R.id.send_button))
            .perform(click())
        onView(withId(R.id.name_edit_text))
            .check(matches(hasNoErrorText()))

        // Evaluate the email EditText.
        onView(withId(R.id.email_edit_text))
            .check(matches(hasErrorText(error)))
        onView(withId(R.id.email_edit_text))
            .perform(clearText(), typeText("name@itu.dk"))
        onView(withId(R.id.send_button))
            .perform(click())
        onView(withId(R.id.email_edit_text))
            .check(matches(hasNoErrorText()))

        // Evaluate the password EditText.
        onView(withId(R.id.password_edit_text))
            .check(matches(hasErrorText(error)))
        onView(withId(R.id.password_edit_text))
            .perform(clearText(), typeText("ABC123"))
        onView(withId(R.id.send_button))
            .perform(click())
        onView(withId(R.id.password_edit_text))
            .check(matches(hasNoErrorText()))

        // All EditTexts are empty.
        onView(withId(R.id.name_edit_text))
            .check(matches(withText("")))
        onView(withId(R.id.email_edit_text))
            .check(matches(withText("")))
        onView(withId(R.id.password_edit_text))
            .check(matches(withText("")))
    }

    @Test
    fun mainActivityTest_clickReverseButton() {
        // Insert data into the EditTexts.
        onView(withId(R.id.name_edit_text))
            .perform(clearText(), typeText("Name"))
        onView(withId(R.id.email_edit_text))
            .perform(clearText(), typeText("name@itu.dk"))
        onView(withId(R.id.password_edit_text))
            .perform(clearText(), typeText("ABC123"))

        // Click on Revert button.
        onView(withId(R.id.send_button))
            .perform(click())

        // All EditTexts are empty.
        onView(withId(R.id.name_edit_text))
            .check(matches(withText("")))
        onView(withId(R.id.email_edit_text))
            .check(matches(withText("")))
        onView(withId(R.id.password_edit_text))
            .check(matches(withText("")))
    }

    private fun hasNoErrorText(): Matcher<View?> {
        return object : BoundedMatcher<View?, EditText>(EditText::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has no error text: ")
            }

            override fun matchesSafely(view: EditText): Boolean {
                return view.error == null
            }
        }
    }

}
