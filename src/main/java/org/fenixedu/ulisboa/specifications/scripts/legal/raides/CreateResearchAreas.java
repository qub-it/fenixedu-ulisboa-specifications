package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import java.util.List;
import java.util.Locale;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.student.ResearchArea;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;

public class CreateResearchAreas extends CustomTask {

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
    public List<Bean> RESEAR_AREAS = Lists.newArrayList(
            new Bean("11", "Matemática", "Matemática"),
            new Bean("12", "Ciências da computação e da informação", "Ciências da computação e da informação"),
            new Bean("13", "Física", "Física"),
            new Bean("14", "Química", "Química"),
            new Bean("15", "Ciências da terra e ciências do ambiente", "Ciências da terra e ciências do ambiente"),
            new Bean("16", "Ciências biológicas", "Ciências biológicas"),
            new Bean("17", "Outras ciências naturais", "Outras ciências naturais"),
            new Bean("21", "Engenharia civil", "Engenharia civil"),
            new Bean("22", "Engenharia electrotécnica, electrónica e informática", "Engenharia electrotécnica, electrónica e informática"),
            new Bean("23", "Engenharia mecânica", "Engenharia mecânica"),
            new Bean("24", "Engenharia química", "Engenharia química"),
            new Bean("25", "Engenharia dos materiais", "Engenharia dos materiais"),
            new Bean("26", "IEngenharia médica", "Engenharia médica"),
            new Bean("27", "Engenharia do ambiente", "Engenharia do ambiente"),
            new Bean("28", "Biotecnologia ambiental", "Biotecnologia ambiental"),
            new Bean("29", "Biotecnologia industrial", "Biotecnologia industrial"),
            new Bean("210", "Nanotecnologia", "Nanotecnologia"),
            new Bean("211", "Outras ciências da engenharia e tecnologias", "Outras ciências da engenharia e tecnologias"),
            new Bean("31", "Medicina básica", "Medicina básica"),
            new Bean("32", "Medicina clínica", "Medicina clínica"),
            new Bean("33", "Ciências da saúde", "Ciências da saúde"),
            new Bean("34", "Biotecnologia médica", "Biotecnologia médica"),
            new Bean("35", "Outras ciências médicas", "Outras ciências médicas"),
            new Bean("41", "Agricultura, silvicultura e pescas", "Agricultura, silvicultura e pescas"),
            new Bean("42", "Ciência animal e dos lacticínios", "Ciência animal e dos lacticínios"),
            new Bean("43", "Ciências veterinárias", "Ciências veterinárias"),
            new Bean("44", "Biotecnologia agrária e alimentar", "Biotecnologia agrária e alimentar"),
            new Bean("45", "Outras ciências agrárias", "Outras ciências agrárias"),
            new Bean("51", "Psicologia", "Psicologia"),
            new Bean("52", "Economia e gestão", "Economia e gestão"),
            new Bean("53", "Ciências da educação", "Ciências da educação"),
            new Bean("54", "Sociologia", "Sociologia"),
            new Bean("55", "Direito", "Direito"),
            new Bean("56", "Ciências políticas", "Ciências políticas"),
            new Bean("57", "Geografia económica e social", "Geografia económica e social"),
            new Bean("58", "Ciências da comunicação", "Ciências da comunicação"),
            new Bean("59", "Outras ciências sociais", "Outras ciências sociais"),
            new Bean("61", "História e arqueologia", "História e arqueologia"),
            new Bean("62", "Línguas e literaturas", "Línguas e literaturas"),
            new Bean("63", "Filosofia, ética e religião", "Filosofia, ética e religião"),
            new Bean("64", "Artes", "Artes"),
            new Bean("65", "Outras humanidades", "Outras humanidades")
    );
    // @formatter:on
    
    @Override
    public void runTask() throws Exception {
        for (Bean bean : RESEAR_AREAS) {
            ResearchArea.create(bean.code, bean.name);
        }
    }
    
}
