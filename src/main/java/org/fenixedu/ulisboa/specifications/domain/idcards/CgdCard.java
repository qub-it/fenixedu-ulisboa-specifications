package org.fenixedu.ulisboa.specifications.domain.idcards;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class CgdCard extends CgdCard_Base {

    static {
        FenixFramework.getDomainModel().registerDeletionListener(Person.class, new DeletionListener<Person>() {

            @Override
            public void deleting(Person person) {
                for (; !person.getCgdCardsSet().isEmpty(); person.getCgdCardsSet().iterator().next().delete());
            }
        });
    }

    protected CgdCard() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public CgdCard(Person person, String mifareCode) {
        this();
        setPerson(person);
        setMifareCode(mifareCode);
        setIssueDate(new LocalDate());
        // Taking into account that there might exist 
        // situations where in the same institution a person
        // might have different cards for the student role,
        // teacher role and employee role and on others such
        // thing won't exist.
        //
        setEmployeeCard(true);
        setTeacherCard(true);
        setStudentCard(true);
    }

    @Atomic
    protected void delete() {
        setPerson(null);
        setRootDomainObject(null);
        super.deleteDomainObject();
    }

    public boolean isValid() {
        return getValidUntil() == null || getValidUntil().isAfter(new DateTime().toLocalDate());
    }

    public static CgdCard findByPerson(Person person) {
        return person.getCgdCardsSet().stream().filter(cgdCard -> cgdCard.isValid()).findFirst().orElse(null);
    }

}
