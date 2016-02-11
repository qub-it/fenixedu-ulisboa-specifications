package org.fenixedu.ulisboa.specifications.domain.ects;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class GradingTableSettings extends GradingTableSettings_Base {

    private static Integer MIN_SAMPLE_SIZE = 30;
    private static Integer MIN_PAST_YEARS = 3;

    private GradingTableSettings() {
        super();
        setBennu(Bennu.getInstance());
    }

    private GradingTableSettings(Integer minSampleSize, int minPastYears) {
        this();
        setMinSampleSize(minSampleSize);
        setMinPastYears(minPastYears);
    }

    @Atomic
    private static GradingTableSettings getInstance() {
        GradingTableSettings settings = Bennu.getInstance().getGradingTableSettings();
        if (settings == null) {
            settings = new GradingTableSettings(MIN_SAMPLE_SIZE, MIN_PAST_YEARS);
        }
        return settings;
    }

    public static int getMinimumSampleSize() {
        return getInstance().getMinSampleSize() != null ? getInstance().getMinSampleSize() : MIN_SAMPLE_SIZE;
    }

    public static void setMinimumSampleSize(int sampleSize) {
        getInstance().setMinSampleSize(sampleSize);
    }

    public static int getMinimumPastYears() {
        return getInstance().getMinPastYears() != null ? getInstance().getMinPastYears() : MIN_PAST_YEARS;
    }

    public static void setMinimumPastYears(int pastYears) {
        getInstance().setMinPastYears(pastYears);
    }

}
