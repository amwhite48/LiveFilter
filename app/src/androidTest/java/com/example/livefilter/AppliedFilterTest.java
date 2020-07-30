package com.example.livefilter;

// import com.google.common.truth.Truth.assertThat;
import com.example.livefilter.models.AppliedFilter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
// import static com.google.common.truth.Truth8.assertThat;
import org.junit.Test;

public class AppliedFilterTest {


    // tests if get effects list method returns all effects
    @Test
    public void getEffectsListTest() {
        AppliedFilter appliedFilter = new AppliedFilter();
        assertThat(appliedFilter.getEffectsList(), is(new String[]{"brightness", "contrast", "hue", "grayscale", "sepia", "saturation"}));
    }

    // tests filterApplied method that checks if effect has been applied
    @Test
    public void filterAppliedTest() {
        AppliedFilter appliedFilter = new AppliedFilter();

        assertThat(appliedFilter.filterApplied("brightness"), is(false));

        appliedFilter.addFilter("brightness");
        assertThat(appliedFilter.filterApplied("brightness"), is(true));

        appliedFilter.addFilter("contrast");
        assertThat(appliedFilter.filterApplied("contrast"), is(true));

    }

    // tests addFilter method
    @Test
    public void addFilterTest() {
        AppliedFilter appliedFilter = new AppliedFilter();

        // add filters and check that they applied
        appliedFilter.addFilter("sepia");
        assertThat(appliedFilter.filterApplied("sepia"), is(true));

        appliedFilter.addFilter("contrast");
        assertThat(appliedFilter.filterApplied("contrast"), is(true));
    }

    // tests adjustFilter method
    @Test
    public void adjustFilterTest() {
        AppliedFilter appliedFilter = new AppliedFilter();
        appliedFilter.addFilter("brightness");
        appliedFilter.addFilter("contrast");
        // by default brightness filter value should be 50
        assertThat(appliedFilter.getFilterValue("brightness"), is(50));
        // by default contrast filter value should be 25
        assertThat(appliedFilter.getFilterValue("contrast"), is(25));

        // set contrast filter value to 100
        appliedFilter.adjustFilter("contrast", 100);
        assertThat(appliedFilter.getFilterValue("contrast"), is(100));

        // set brightness filter value to 0
        appliedFilter.adjustFilter("brightness", 0);
        assertThat(appliedFilter.getFilterValue("brightness"), is(0));
        // make sure contrast is still the same
        assertThat(appliedFilter.getFilterValue("contrast"), is(100));

    }


    // ToDo: test exceptions on AppliedFilter when I throw them 


}
