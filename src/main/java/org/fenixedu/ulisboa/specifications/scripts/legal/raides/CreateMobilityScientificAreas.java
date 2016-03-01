package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import java.util.List;
import java.util.Locale;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityScientificArea;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;

public class CreateMobilityScientificAreas extends CustomTask {

    
    private static class Bean {
        String code;
        LocalizedString name;

        Bean(String code, String pt, String en) {
            this.code = code;
            
            LocalizedString name = new LocalizedString(ULisboaConstants.DEFAULT_LOCALE, pt);
            name = name.with(new Locale("EN"), en);
            
            this.name = name;
        }
    }
    
    // @formatter:off
    public List<Bean> MOB_SCIENT_AREAS = Lists.newArrayList(
            new Bean("10", "Formação de professores/formadores e ciências da educação", "Formação de professores/formadores e ciências da educação"),
            new Bean("11", "Artes", "Artes"),
            new Bean("12", "Humanidades", "Humanidades"),
            new Bean("13", "Ciências sociais e do comportamento", "Ciências sociais e do comportamento"),
            new Bean("14", "Informação e jornalismo", "Informação e jornalismo"),
            new Bean("15", "Ciências empresariais", "Ciências empresariais"),
            new Bean("16", "Direito", "Direito"),
            new Bean("17", "Ciências da vida", "Ciências da vida"),
            new Bean("18", "Ciências físicas", "Ciências físicas"),
            new Bean("19", "Matemática e estatística", "Matemática e estatística"),
            new Bean("20", "Informática", "Informática"),
            new Bean("21", "Engenharia e técnicas afins", "Engenharia e técnicas afins"),
            new Bean("22", "Indústrias transformadoras", "Indústrias transformadoras"),
            new Bean("23", "Arquitectura e construção", "Arquitectura e construção"),
            new Bean("24", "Agricultura, silvicultura e pescas", "Agricultura, silvicultura e pescas"),
            new Bean("25", "Ciências veterinárias", "Ciências veterinárias"),
            new Bean("26", "Saúde", "Saúde"),
            new Bean("27", "Serviços sociais", "Serviços sociais"),
            new Bean("28", "Serviços pessoais", "Serviços pessoais"),
            new Bean("29", "Serviços de transporte", "Serviços de transporte"),
            new Bean("30", "Protecção do ambiente", "Protecção do ambiente"),
            new Bean("31", "Serviços de segurança", "Serviços de segurança"),
            new Bean("32", "Desconhecido ou não especificado", "Desconhecido ou não especificado")
    );
    // @formatter:on

    @Override
    public void runTask() throws Exception {
        for (Bean bean : MOB_SCIENT_AREAS) {
            MobilityScientificArea.create(bean.code, bean.name);
        }
    }

}
