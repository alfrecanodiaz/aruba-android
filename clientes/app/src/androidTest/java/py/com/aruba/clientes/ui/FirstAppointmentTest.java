package py.com.aruba.clientes.ui;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import py.com.aruba.clientes.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FirstAppointmentTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void firstAppointmentTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnAccept), withText("INICIAR SESIÓN"),
                        childAtPosition(
                                allOf(withId(R.id.llContentButtons),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnAccept), withText("INICIAR SESIÓN"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                2)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnAcept), withText("Aceptar"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.rlContent),
                        childAtPosition(
                                allOf(withId(R.id.recyclerView),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        relativeLayout.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.llProfileMan),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btnAcept), withText("CONTINUAR"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction switch_ = onView(
                allOf(withId(R.id.idSwService),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        switch_.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.btnContinue),
                        childAtPosition(
                                allOf(withId(R.id.rlContentTotal),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction relativeLayout2 = onView(
                allOf(withId(R.id.rlContentDate),
                        childAtPosition(
                                allOf(withId(R.id.rlFilter),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.mdtp_ok), withText("Aceptar"),
                        childAtPosition(
                                allOf(withId(R.id.mdtp_done_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction relativeLayout3 = onView(
                allOf(withId(R.id.rlContentTime),
                        childAtPosition(
                                allOf(withId(R.id.rlFilter),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        relativeLayout3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.mdtp_ok), withText("Aceptar"),
                        childAtPosition(
                                allOf(withId(R.id.mdtp_done_background),
                                        childAtPosition(
                                                withId(R.id.mdtp_time_picker_dialog),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.btnAcept), withText("ACEPTAR"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction relativeLayout4 = onView(
                allOf(withId(R.id.rlContentDate),
                        childAtPosition(
                                allOf(withId(R.id.rlFilter),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.mdtp_ok), withText("Aceptar"),
                        childAtPosition(
                                allOf(withId(R.id.mdtp_done_background),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction relativeLayout5 = onView(
                allOf(withId(R.id.rlContentTime),
                        childAtPosition(
                                allOf(withId(R.id.rlFilter),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        relativeLayout5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.mdtp_ok), withText("Aceptar"),
                        childAtPosition(
                                allOf(withId(R.id.mdtp_done_background),
                                        childAtPosition(
                                                withId(R.id.mdtp_time_picker_dialog),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.llItem),
                        childAtPosition(
                                allOf(withId(R.id.recyclerView),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        linearLayout2.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.btnContinue), withText("SELECCIONAR PROFESIONAL"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        3),
                                2),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.btnContinue),
                        childAtPosition(
                                allOf(withId(R.id.rlContentTotal),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatImageView2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.btnPaid), withText("PAGAR"),
                        childAtPosition(
                                allOf(withId(R.id.rlContentTotal),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.btnAcept), withText("ACEPTAR"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton12.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
