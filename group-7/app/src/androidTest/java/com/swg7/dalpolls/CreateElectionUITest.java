package com.swg7.dalpolls;


//import android.content.Context;
//import android.content.Intent;

import android.app.Activity;
import android.view.WindowManager;
import android.widget.DatePicker;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Root;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import androidx.test.espresso.intent.Intents;
//import androidx.test.espresso.intent.rule.IntentsTestRule;
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateElectionUITest extends NonPersistentUITest {

    public CreateElectionUITest() {
        super(NewElection.class);
    }

    @BeforeClass
    public static void setUpClass() {
    }


    @Test
    public void CreateValidElectionTest() {

        //Type Election Title
        onView(withId(R.id.editElectionTitle)).perform(typeText("New Election"), closeSoftKeyboard());
        //Type Election Start Date
        enterDate(R.id.editElectionStartDate, 2020,1,1);
        //Type Election End Date
        enterDate(R.id.editElectionEndDate,2020,4,1);

        onView(withId(R.id.btnElectionSave)).perform(click());
        assert(mActivityTestRule.getActivityResult().getResultCode() == Activity.RESULT_CANCELED);

    }

    @Test
    public void ValidElectionToggleTest() {
        onView(withId(R.id.toggleVisable)).perform(click());
        onView(withId(R.id.toggleVisable)).check(matches(isChecked()));
        onView(withId(R.id.toggleVotable)).check(matches(isNotChecked()));
        onView(withId(R.id.toggleResults)).check(matches(isNotChecked()));
        onView(withId(R.id.btnElectionSave)).perform(click());
        assert(mActivityTestRule.getActivityResult().getResultCode() == Activity.RESULT_CANCELED);
    }

    @Test
    public void InvalidElectionToggleTest() {
        onView(withId(R.id.toggleVotable)).perform(click());
        //assert that only votable is checked
        onView(withId(R.id.toggleVisable)).check(matches(isNotChecked()));
        onView(withId(R.id.toggleVotable)).check(matches(isChecked()));
        onView(withId(R.id.toggleResults)).check(matches(isNotChecked()));

        onView(withId(R.id.toggleResults)).perform(click());
        //assert that only Results is checked
        onView(withId(R.id.toggleVisable)).check(matches(isNotChecked()));
        onView(withId(R.id.toggleVotable)).check(matches(isChecked()));
        onView(withId(R.id.toggleResults)).check(matches(isChecked()));

        onView(withId(R.id.toggleVisable)).perform(click());
        //assert that only visable is checked
        onView(withId(R.id.toggleVisable)).check(matches(isChecked()));
        onView(withId(R.id.toggleVotable)).check(matches(isChecked()));
        onView(withId(R.id.toggleResults)).check(matches(isChecked()));
        onView(withId(R.id.btnElectionSave)).perform(click());
        assert(mActivityTestRule.getActivityResult().getResultCode() == Activity.RESULT_CANCELED);
    }

    @Test
    public void InvalidElectionTitleTest() {
        //Type Election Start Date
        enterDate(R.id.editElectionStartDate, 2020,1,1);
        //Type Election End Date
        enterDate(R.id.editElectionEndDate,2020,4,1);

        onView(withId(R.id.btnElectionSave)).perform(click());
        toastExists(R.string.invalid_title_toast_message);
    }

    @Test
    public void BackwardsElectionDateTest() {

        //Type Election Title
        onView(withId(R.id.editElectionTitle)).perform(typeText("New Election"), closeSoftKeyboard());
        //Type Election Start Date
        enterDate(R.id.editElectionStartDate, 2020,4,1);
        //Type Election End Date
        enterDate(R.id.editElectionEndDate,2020,1,1);

        onView(withId(R.id.btnElectionSave)).perform(click());

        toastExists(R.string.invalid_date_toast_message);
    }


    /**
     * Test that once the create election button is pressed,
     * an activity with an extra key "election" is launched.
     */
/*    @Test
    public void CreateElectionActivityLaunchedTest(){
        onView(withId(R.id.create_election_fab)).perform(click());
        intended(hasExtraWithKey("election"));
    }
*/
    /**
     * Test that once the create election button is pressed,
     * an activity with an extra key "question" is launched.
     */

  /*  @Test
    public void CreateQuestionActivityLaunchedTest(){
        onView(withId(R.id.create_question_fab)).perform(click());
        intended(hasExtraWithKey("question"));
    }
    */private class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            return root.getWindowLayoutParams().get().type == WindowManager.LayoutParams.TYPE_TOAST;
        }

    }

    public void toastExists(int resID){
        onView(withText(resID)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    private void enterDate(int resId, int year, int month, int date) {
        onView(withId(resId)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year,month,date));
        onView(withText("OK")).perform(click());
    }

    @Test
    public void previewElection(){
        onView(withId(R.id.btnElectionPreview)).perform(click());

        //intended(/*to be filled*/);
    }

    @Test
    public void cloneElection() {
        onView(withId(R.id.btnElectionClone)).perform(click());
        toastExists(R.string.clone_election_toast_message);
    }

}
