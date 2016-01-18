package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import java.util.List;
import java.util.Locale;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgrammeLevel;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityScientificArea;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;

public class CreateMobilityProgrammeLevels extends CustomTask {

    private static class Bean {
        String code;
        LocalizedString name;
        boolean otherLevel;

        Bean(String code, String pt, String en, boolean otherLevel) {
            this.code = code;
            
            LocalizedString name = new LocalizedString(ULisboaConstants.DEFAULT_LOCALE, pt);
            name = name.with(new Locale("EN"), en);
            
            this.name = name;
            
            this.otherLevel = otherLevel;
        }
    }
    
    // @formatter:off
    public List<Bean> MOB_PROG_LEVELS = Lists.newArrayList(
            new Bean("1", "Nível de licenciatura (1º ciclo do Processo de Bolonha)", "Nível de licenciatura (1º ciclo do Processo de Bolonha)", false),
            new Bean("2", "Nível de mestrado (2º ciclo do Processo de Bolonha)", "Nível de mestrado (2º ciclo do Processo de Bolonha)", false),
            new Bean("3", "Nível de doutoramento (3º ciclo do Processo de Bolonha)", "Nível de doutoramento (3º ciclo do Processo de Bolonha)", false),
            new Bean("4", "Outro", "Outro", true),
            new Bean("5", "Estágio", "Estágio", false));
    // @formatter:on
    
    @Override
    public void runTask() throws Exception {
        for (Bean bean : MOB_PROG_LEVELS) {
            MobilityProgrammeLevel.create(bean.code, bean.name, bean.otherLevel);
        }
    }

}
