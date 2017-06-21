package com.qdigo.deq.testapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jpj on 2017/1/10.
 */

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private static final String EMAIL = "Peter";
    private static final String PASSWORD = "123";

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void sayHello(){
        onView(withId(R.id.email)).perform(typeText(EMAIL), closeSoftKeyboard()); //line 1

        onView(withId(R.id.password)).perform(typeText(PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.email_sign_in_button)).perform(click());
    }
}
