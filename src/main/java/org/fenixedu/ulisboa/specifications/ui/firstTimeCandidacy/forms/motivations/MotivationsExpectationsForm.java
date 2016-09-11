package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class MotivationsExpectationsForm implements CandidancyForm {

    private List<UniversityDiscoveryMeansAnswer> universityDiscoveryMeansAnswers = new ArrayList<>();
    private String otherUniversityDiscoveryMeans;
    private List<UniversityChoiceMotivationAnswer> universityChoiceMotivationAnswers = new ArrayList<>();
    private String otherUniversityChoiceMotivation;
    private boolean firstYearRegistration;
    private boolean answered;
    private boolean otherDiscoveryAnswer;
    private boolean otherChoiceAnswer;

    private List<TupleDataSourceBean> universityDiscoveryMeansAnswerValues;
    private List<TupleDataSourceBean> universityChoiceMotivationAnswerValues;
    private List<String> otherUniversityDiscoveryMeansAnswerValues;
    private List<String> otherUniversityChoiceMotivationAnswerValues;

    public MotivationsExpectationsForm() {
        setUniversityDiscoveryMeansAnswerValues(UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList()));
        setUniversityChoiceMotivationAnswerValues(UniversityChoiceMotivationAnswer.readAll().collect(Collectors.toList()));
        setOtherUniversityDiscoveryMeansAnswerValues(UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList()));
        setOtherUniversityChoiceMotivationAnswerValues(UniversityChoiceMotivationAnswer.readAll().collect(Collectors.toList()));
        updateLists();
    }

    @Override
    public void updateLists() {
        boolean otherDiscoveryAnswer = false;
        boolean otherChoiceAnswer = false;

        for (UniversityDiscoveryMeansAnswer answer : universityDiscoveryMeansAnswers) {
            if (answer.isOther()) {
                otherDiscoveryAnswer = true;
                break;
            }
        }
        setOtherDiscoveryAnswer(otherDiscoveryAnswer);

        for (UniversityChoiceMotivationAnswer answer : universityChoiceMotivationAnswers) {
            if (answer.isOther()) {
                otherChoiceAnswer = true;
                break;
            }
        }
        setOtherChoiceAnswer(otherChoiceAnswer);
    }

    public List<UniversityDiscoveryMeansAnswer> getUniversityDiscoveryMeansAnswers() {
        return universityDiscoveryMeansAnswers;
    }

    public void setUniversityDiscoveryMeansAnswers(List<UniversityDiscoveryMeansAnswer> universityDiscoveryMeansAnswers) {
        this.universityDiscoveryMeansAnswers = universityDiscoveryMeansAnswers;
    }

    public List<UniversityChoiceMotivationAnswer> getUniversityChoiceMotivationAnswers() {
        return universityChoiceMotivationAnswers;
    }

    public void setUniversityChoiceMotivationAnswers(List<UniversityChoiceMotivationAnswer> universityChoiceMotivationAnswers) {
        this.universityChoiceMotivationAnswers = universityChoiceMotivationAnswers;
    }

    public String getOtherUniversityDiscoveryMeans() {
        return otherUniversityDiscoveryMeans;
    }

    public void setOtherUniversityDiscoveryMeans(String otherUniversityDiscoveryMeans) {
        this.otherUniversityDiscoveryMeans = otherUniversityDiscoveryMeans;
    }

    public String getOtherUniversityChoiceMotivation() {
        return otherUniversityChoiceMotivation;
    }

    public void setOtherUniversityChoiceMotivation(String otherUniversityChoiceMotivation) {
        this.otherUniversityChoiceMotivation = otherUniversityChoiceMotivation;
    }

    private void populateFormValues(HttpServletRequest request) {
        for (UniversityDiscoveryMeansAnswer answer : UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList())) {
            if (request.getParameter("universityDiscoveryMeans_" + answer.getExternalId()) != null) {
                getUniversityDiscoveryMeansAnswers().add(answer);
            }
        }

        for (UniversityChoiceMotivationAnswer answer : UniversityChoiceMotivationAnswer.readAll().collect(Collectors.toList())) {
            if (request.getParameter("universityChoiceMotivation_" + answer.getExternalId()) != null) {
                getUniversityChoiceMotivationAnswers().add(answer);
            }
        }
    }

    private void populateRequestCheckboxes(HttpServletRequest request) {
        for (UniversityDiscoveryMeansAnswer answer : UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList())) {
            boolean checked = getUniversityDiscoveryMeansAnswers().contains(answer);
            request.setAttribute("universityDiscoveryMeans_" + answer.getExternalId(), checked);
        }

        for (UniversityChoiceMotivationAnswer answer : UniversityChoiceMotivationAnswer.readAll().collect(Collectors.toList())) {
            boolean checked = getUniversityChoiceMotivationAnswers().contains(answer);
            request.setAttribute("universityChoiceMotivation_" + answer.getExternalId(), checked);
        }
    }

    public boolean isFirstYearRegistration() {
        return firstYearRegistration;
    }

    public void setFirstYearRegistration(boolean firstYearRegistration) {
        this.firstYearRegistration = firstYearRegistration;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isOtherDiscoveryAnswer() {
        return otherDiscoveryAnswer;
    }

    public void setOtherDiscoveryAnswer(boolean otherDiscoveryAnswer) {
        this.otherDiscoveryAnswer = otherDiscoveryAnswer;
    }

    public boolean isOtherChoiceAnswer() {
        return otherChoiceAnswer;
    }

    public void setOtherChoiceAnswer(boolean otherChoiceAnswer) {
        this.otherChoiceAnswer = otherChoiceAnswer;
    }

    public List<TupleDataSourceBean> getUniversityDiscoveryMeansAnswerValues() {
        return universityDiscoveryMeansAnswerValues;
    }

    public void setUniversityDiscoveryMeansAnswerValues(
            List<UniversityDiscoveryMeansAnswer> universityDiscoveryMeansAnswerValues) {
        this.universityDiscoveryMeansAnswerValues = universityDiscoveryMeansAnswerValues.stream().map(a -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(a.getExternalId());
            tuple.setText(a.getDescription().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getUniversityChoiceMotivationAnswerValues() {
        return universityChoiceMotivationAnswerValues;
    }

    public void setUniversityChoiceMotivationAnswerValues(
            List<UniversityChoiceMotivationAnswer> universityChoiceMotivationAnswerValues) {
        this.universityChoiceMotivationAnswerValues = universityChoiceMotivationAnswerValues.stream().map(a -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(a.getExternalId());
            tuple.setText(a.getDescription().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<String> getOtherUniversityDiscoveryMeansAnswerValues() {
        return otherUniversityDiscoveryMeansAnswerValues;
    }

    public void setOtherUniversityDiscoveryMeansAnswerValues(
            List<UniversityDiscoveryMeansAnswer> otherUniversityDiscoveryMeansAnswerValues) {
        this.otherUniversityDiscoveryMeansAnswerValues = otherUniversityDiscoveryMeansAnswerValues.stream()
                .filter(a -> a.isOther()).map(a -> a.getExternalId()).collect(Collectors.toList());;
    }

    public List<String> getOtherUniversityChoiceMotivationAnswerValues() {
        return otherUniversityChoiceMotivationAnswerValues;
    }

    public void setOtherUniversityChoiceMotivationAnswerValues(
            List<UniversityChoiceMotivationAnswer> otherUniversityChoiceMotivationAnswerValues) {
        this.otherUniversityChoiceMotivationAnswerValues = otherUniversityChoiceMotivationAnswerValues.stream()
                .filter(a -> a.isOther()).map(a -> a.getExternalId()).collect(Collectors.toList());;
    }
}
