package org.fenixedu.ulisboa.specifications.domain.curricularRules;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.AnyCurricularCourseExceptionsExecutorLogic;
import org.fenixedu.bennu.core.domain.Bennu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Sets;

abstract public class AnyCurricularCourseExceptionsInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AnyCurricularCourseExceptionsInitializer.class);

    @Atomic
    public static void init() {

        AnyCurricularCourseExceptionsExecutorLogic.configure();
        AnyCurricularCourseExceptionsConfiguration.init();

        final String acronym = Bennu.getInstance().getInstitutionUnit().getAcronym().toUpperCase();
        logger.info("Init for " + acronym);

        final URL inputFileUrl =
                AnyCurricularCourseExceptionsInitializer.class.getClassLoader().getResource(
                        "bootstrap/AnyCurricularCourseExceptionsInitializer/" + acronym + ".csv");
        if (inputFileUrl == null) {
            logger.info("Init not found for " + acronym);
            return;
        }

        final Set<CompetenceCourse> competenceCourses = findCompetenceCourses(inputFileUrl);
        if (competenceCourses.isEmpty()) {
            logger.info("No CompetenceCourses found, done");
        } else {
            AnyCurricularCourseExceptionsConfiguration.getInstance().getCompetenceCoursesSet().clear();
            AnyCurricularCourseExceptionsConfiguration.getInstance().getCompetenceCoursesSet().addAll(competenceCourses);
            logger.info("Cleared current configuration and added found CompetenceCourses, done");
        }
    }

    private static Set<CompetenceCourse> findCompetenceCourses(final URL inputFileUrl) {
        final Set<CompetenceCourse> result = Sets.newHashSet();

        for (final List<String> line : parseCSV(inputFileUrl, '\t', false)) {
            if (!line.isEmpty()) {
                final String code = line.get(0);
                final CompetenceCourse competenceCourse = CompetenceCourse.find(code);
                if (competenceCourse == null) {
                    logger.info("Not Found [CompetenceCourse][CODE][{}]", code);
                } else {
                    logger.info("Found [CompetenceCourse][CODE][{}]", code);
                    result.add(competenceCourse);
                }
            }
        }

        return result;
    }

    private static List<List<String>> parseCSV(final URL url, final char delimiter, final Boolean skipFirstLine) {
        final List<List<String>> result = new LinkedList<List<String>>();

        if (url != null) {

            try {
                final LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));

                String line = null;
                int index = 0;

                while ((line = reader.readLine()) != null) {
                    if (index == 0 && skipFirstLine) {
                        index++;
                        continue; //skip first line
                    }
                    result.add(parseLineCSV(line, delimiter));
                }
            } catch (final Throwable t) {
                throw new RuntimeException(t);
            }

        }

        return result;
    }

    private static List<String> parseLineCSV(final String line, final char delimiter) {
        final List<String> result = new Vector<String>();

        if (StringUtils.isNotBlank(line)) {
            StringBuffer current = new StringBuffer();
            boolean inquotes = false;

            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (inquotes) {
                    if (ch == '\"') {
                        inquotes = false;
                    } else {
                        current.append(ch);
                    }
                } else {
                    if (ch == '\"') {
                        inquotes = true;
                        if (current.length() > 0) {
                            // if this is the second quote in a value, add a
                            // quote
                            // this is for the double quote in the middle of a
                            // value
                            current.append('\"');
                        }
                    } else if (ch == delimiter) {
                        result.add(current.toString().trim());
                        current = new StringBuffer();
                    } else {
                        current.append(ch);
                    }
                }
            }

            result.add(current.toString().trim());
        }

        return result;
    }

}
