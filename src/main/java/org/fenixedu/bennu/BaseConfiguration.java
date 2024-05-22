package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "com.qubit.qubEdu.module.base", bundles = BaseConfiguration.BUNDLE)
public class BaseConfiguration {

    public static final String BUNDLE = "BaseResources";

}