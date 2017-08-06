package co.otaoto.confirm

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.transition.TransitionManager
import android.widget.ToggleButton
import co.otaoto.R
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfirmActivityTest {
    companion object {
        const val SECRET = "That's my secret, Captain"
        const val SLUG = "three-word-slug"
        const val KEY = "1234567890ABCDEF"
        const val URL = "https://otaoto.co/gate/$SLUG/$KEY"
    }

    @Rule
    @JvmField
    val activityTestRule = object : ActivityTestRule<ConfirmActivity>(ConfirmActivity::class.java) {
        override fun getActivityIntent(): Intent {
            return ConfirmActivity.newIntent(InstrumentationRegistry.getTargetContext(), SECRET, SLUG, KEY)
        }
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun smokeTest() {
        val linkText = onView(hasLinks())
        linkText.check(matches(withText(URL)))
        linkText.perform(click())
        val clipboardManager = activityTestRule.activity
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        assertEquals(URL, clipboardManager.primaryClip.getItemAt(0).text)
        assertEquals(activityTestRule.activity.getString(R.string.link_clipboard_label),
                clipboardManager.primaryClipDescription.label)

        val toggleButton = onView(instanceOf(ToggleButton::class.java))
        val secretText = onView(withId(R.id.confirm_secret_text))
        toggleButton.check(matches(isNotChecked()))
        secretText.check(matches(withEffectiveVisibility(Visibility.GONE)))

        toggleButton.perform(click())
        TransitionManager.endTransitions(activityTestRule.activity.rootView)
        toggleButton.check(matches(isChecked()))
        secretText.check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        toggleButton.perform(click())
        TransitionManager.endTransitions(activityTestRule.activity.rootView)
        toggleButton.check(matches(isNotChecked()))
        secretText.check(matches(withEffectiveVisibility(Visibility.GONE)))

        val anotherButton = onView(withText(R.string.confirm_create_another))
        anotherButton.perform(click())
        val secretSubmit = onView(withText(R.string.secret_submit))
        secretSubmit.check(matches(isDisplayed()))
    }
}