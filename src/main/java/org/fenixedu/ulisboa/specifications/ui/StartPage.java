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

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.spring.FenixEDUBaseController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.startPageRedirector",
        accessGroup = "logged")
@RequestMapping("/startPage")
public class StartPage extends FenixEDUBaseController {

    @RequestMapping()
    public String indexNoSlash(Model model, RedirectAttributes redirectAttributes) {
        return index(model, redirectAttributes);
    }

    @RequestMapping("/")
    public String index(Model model, RedirectAttributes redirectAttributes) {
        if (AccessControl.getPerson() != null) {
            Optional<MenuItem> findFirst = PortalConfiguration.getInstance().getMenu().getUserMenuStream().findFirst();
            String path = findFirst.isPresent() ? findFirst.get().getPath() : "/";
            return redirect(path, model, redirectAttributes);
        }
        throw BennuCoreDomainException.resourceNotFound("startPage asked but no logged user");
    }
}
