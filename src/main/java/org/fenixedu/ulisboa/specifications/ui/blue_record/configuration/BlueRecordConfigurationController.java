package org.fenixedu.ulisboa.specifications.ui.blue_record.configuration;

import java.io.IOException;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.FenixeduQubdocsReportsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.bluerecord.BlueRecordConfiguration;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.blueRecordConfiguration",
        accessGroup = "logged")
@RequestMapping(BlueRecordConfigurationController.CONTROLLER_URL)
public class BlueRecordConfigurationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/configuration";

    private void setBlueRecordConfigurationBean(final BlueRecordConfigurationBean bean, final Model model) {
        bean.updateLists();
        model.addAttribute("blueRecordConfigurationBeanJson", getBeanJson(bean));
        model.addAttribute("blueRecordConfigurationBean", bean);
        model.addAttribute("blueRecordConfiguration", BlueRecordConfiguration.getInstance());
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + _READ_URI;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI)
    public String read(final Model model) {
        setBlueRecordConfigurationBean(new BlueRecordConfigurationBean(BlueRecordConfiguration.getInstance()), model);
        return "fenixedu-ulisboa-specifications/blueRecord/configuration/read";
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI, method = RequestMethod.GET)
    public String update(final Model model) {
        setBlueRecordConfigurationBean(new BlueRecordConfigurationBean(BlueRecordConfiguration.getInstance()), model);

        return "fenixedu-ulisboa-specifications/blueRecord/configuration/update";
    }

    private static final String _UPDATE_POSTBACK_URI = "/update/postback";
    public static final String UPDATE_POSTBACK_URL = CONTROLLER_URL + _UPDATE_POSTBACK_URI;

    @RequestMapping(value = _UPDATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createPostBack(
            @RequestParam(value = "bean", required = true) final BlueRecordConfigurationBean bean, final Model model) {
        setBlueRecordConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @RequestMapping(value = _UPDATE_URI, method = RequestMethod.POST)
    public String update(@RequestParam(value = "bean", required = true) final BlueRecordConfigurationBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {

            update(bean);

            setBlueRecordConfigurationBean(bean, model);
            return redirect(READ_URL, model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(FenixeduQubdocsReportsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
        }
        return update(model);
    }

    @Atomic
    private void update(final BlueRecordConfigurationBean bean) throws IOException {
        BlueRecordConfiguration configuration = BlueRecordConfiguration.getInstance();

        configuration.getExclusiveDegreesSet().clear();

        for (Degree degree : bean.getDegrees()) {
            configuration.addExclusiveDegrees(degree);
        }

        configuration.setIsCgdFormToFill(bean.getIsCgdFormToFill());

    }

}
