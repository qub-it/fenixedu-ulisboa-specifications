package org.fenixedu.ulisboa.specifications.ui.degrees.precedence;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;

public class DegreePrecedencesBean implements IBean {

    private DegreeType degreeType;

    private List<TupleDataSourceBean> degreeTypeDataSource;

    public DegreePrecedencesBean() {
        this.degreeTypeDataSource = Lists.newArrayList();
        this.degreeTypeDataSource.add(ULisboaConstants.SELECT_OPTION);

        this.degreeTypeDataSource.addAll(DegreeType.all()
                .map(d -> new TupleDataSourceBean(d.getExternalId(), d.getName().getContent())).collect(Collectors.toList()));
    }

    public List<Degree> getSelectedDegrees() {
        if (degreeType == null) {
            return Lists.newArrayList();
        }

        final List<Degree> result = Lists.newArrayList(degreeType.getDegreeSet());

        Collections.sort(result, Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);

        return result;
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(final DegreeType degreeType) {
        this.degreeType = degreeType;
    }
    
    public List<TupleDataSourceBean> getDegreeTypeDataSource() {
        return degreeTypeDataSource;
    }
    
}
