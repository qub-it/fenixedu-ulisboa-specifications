/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2016 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2016 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 *
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.FenixEDUBaseController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.userMocking",
        accessGroup = "#managers")
@RequestMapping("/userMock")
public class MockUser extends FenixEDUBaseController {

    @RequestMapping
    public String indexNoSlash(final Model model, final RedirectAttributes redirectAttributes, final HttpServletRequest request) {
        return "forward:/userMock/";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model, final RedirectAttributes redirectAttributes) {
        boolean qualityMode = Boolean.TRUE.equals(ULisboaConfiguration.getConfiguration().isQualityMode());
        boolean developmentMode = Boolean.TRUE.equals(CoreConfiguration.getConfiguration().developmentMode());

        if (!qualityMode && !developmentMode) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.mockUser.onlyWorksInQuaAndDev"), model);
        }
        return "fenixedu-ulisboa-specifications/mock/mockUser";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String index(@RequestParam(value = "username") final String username, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request, final HttpServletResponse response) {
        if (!Strings.isNullOrEmpty(username)) {
            boolean qualityMode = Boolean.TRUE.equals(ULisboaConfiguration.getConfiguration().isQualityMode());
            boolean developmentMode = Boolean.TRUE.equals(CoreConfiguration.getConfiguration().developmentMode());

            if (!qualityMode && !developmentMode) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("label.mockUser.onlyWorksInQuaAndDev"), model);
            } else {
                User user = User.findByUsername(username);
                boolean isManager = user != null ? Group.parse("#managers").isMember(user) : false;

                if (user != null && !isManager) {
                    Authenticate.login(request, response, user, "TODO: CHANGE ME");

                    if (AccessControl.getPerson() != null) {
                        Optional<MenuItem> findFirst =
                                PortalConfiguration.getInstance().getMenu().getUserMenuStream().findFirst();
                        String path = findFirst.isPresent() ? findFirst.get().getPath() : "/";
                        return redirect(path, model, redirectAttributes);
                    }
                } else {
                    addErrorMessage(ULisboaSpecificationsUtil.bundle("label.mockUser.unableToMockUser", username), model);
                }
            }
        }
        return "fenixedu-ulisboa-specifications/mock/mockUser";
    }
}
