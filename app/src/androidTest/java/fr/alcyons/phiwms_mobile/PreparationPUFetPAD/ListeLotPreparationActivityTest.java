package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.alcyons.phiwms_mobile.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * NOTE: This is a basic test structure.
 * This test will likely fail because ListeLotPreparationActivity has dependencies
 * on a database (DBOpenHelper, etc.) and network requests.
 * To make this testable, you should consider:
 * 1. Using a mocking framework (like Mockito) to mock database helpers and network calls.
 * 2. Providing fake data through mocked dependencies to simulate different scenarios.
 * 3. Setting up a test database with pre-populated data.
 */
@RunWith(AndroidJUnit4.class)
public class ListeLotPreparationActivityTest {

    // This intent is necessary because the Activity expects "phPreparationLigneId" as an extra.
    // You will need to provide a valid ID that your test setup can handle (e.g., an ID that your mocked database returns data for).
    private static Intent getTestIntent() {
        Context targetContext = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(targetContext, ListeLotPreparationActivity.class);
        intent.putExtra("phPreparationLigneId", 123); // Example ID
        return intent;
    }

    @Rule
    public ActivityScenarioRule<ListeLotPreparationActivity> activityRule =
            new ActivityScenarioRule<>(getTestIntent());

    @Test
    public void testActivityDisplaysMainElements() {
        // This test will check if the main UI elements are displayed.
        // It will fail if the activity crashes during onCreate due to unmocked dependencies.
        onView(withId(R.id.intitule)).check(matches(isDisplayed()));
        onView(withId(R.id.designationProduit)).check(matches(isDisplayed()));
        onView(withId(R.id.referenceProduit)).check(matches(isDisplayed()));
        onView(withId(R.id.lancerScan)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewIsDisplayedAndItemClickable() {
        // This test checks if the RecyclerView is displayed.
        // It will fail if the activity crashes or if the view is not found.
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // This part of the test tries to click on the first item in the RecyclerView.
        // This will fail if the RecyclerView is empty. You need to provide data to the adapter for this to pass.
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}
