package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations;

import org.fenixedu.bennu.IBean;

public class AnswerOptionForm implements IBean {

    private String id;
    private String text;
    private String code;
    private String parentId;

    public AnswerOptionForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

}
