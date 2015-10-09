package org.fenixedu.ulisboa.specifications.dto;

import java.util.List;

import org.fenixedu.bennu.TupleDataSourceBean;

public interface DataSourceProvider {

    public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean);
}
