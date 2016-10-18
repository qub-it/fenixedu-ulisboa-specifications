package org.fenixedu.ulisboa.specifications.service.reports.preprocessors;

import static org.fenixedu.qubdocs.ui.documenttemplates.AcademicServiceRequestTemplateController.QUB_INCLUDE_PREFIX;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fenixedu.qubdocs.domain.serviceRequests.AcademicServiceRequestTemplate;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.qubit.terra.docs.util.ReportGeneratorPreProcessor;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.odt.ODTConstants;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.formatter.IDocumentFormatter;

public class QubIncludePreProcessor extends ReportGeneratorPreProcessor {

    public static final Pattern PATTERN = Pattern.compile("qubInclude\\(\"(.+)\"\\)");
    public static final List<String> NODE_NAMES = Arrays.asList("office:document-content", "office:body", "office:text",
            "table:table", "table:table-row", "table:table-cell");

    private Locale locale;

    public QubIncludePreProcessor(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getEntryName() {
        return ODTConstants.CONTENT_XML_ENTRY;
    }

    @Override
    protected void visit(Document document, String entryName, FieldsMetadata fieldsMetadata, IDocumentFormatter formatter,
            Map<String, Object> sharedContext) throws XDocReportException {

        Node node = document;
        for (String nodeName : NODE_NAMES) {
            node = getNode(node.getChildNodes(), nodeName);
        }

        findIncludes(node.getChildNodes());
    }

    protected Node getNode(NodeList childrenList, String nodeName) {

        for (int i = 0; i < childrenList.getLength(); i++) {
            Node node = childrenList.item(i);
            if (node.getNodeName().equals(nodeName)) {
                return node;
            }
        }

        throw new ULisboaSpecificationsDomainException("error.unexpected.document.format");
    }

    protected void findIncludes(NodeList nodesList) {

        for (int i = 0; i < nodesList.getLength(); i++) {
            Node child = nodesList.item(i);

            if (child instanceof Text) {
                processIncludes((Text) child);
            } else {
                findIncludes(child.getChildNodes());
            }
        }
    }

    protected void processIncludes(Text node) {
        Predicate<AcademicServiceRequestTemplate> localeCondition = template -> template.getLanguage().equals(locale);

        Matcher matcher = PATTERN.matcher(node.getData());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String[] includes = matcher.group(1).split(" ");
            for (String include : includes) {
                Predicate<AcademicServiceRequestTemplate> nameCondition =
                        template -> template.getName().getContent(locale).equalsIgnoreCase(QUB_INCLUDE_PREFIX + include);

                List<AcademicServiceRequestTemplate> lst = AcademicServiceRequestTemplate.findAll()
                        .filter(nameCondition.and(localeCondition)).collect(Collectors.toList());

                if (lst.size() != 1) {
                    throw new ULisboaSpecificationsDomainException("error.DocumentPrinter.finding.include", include,
                            "" + lst.size());
                };

                String content = new String(lst.get(0).getDocumentTemplateFile().getContent());
                sb.append(content);
            }
        }
        node.setData(matcher.replaceAll("") + sb.toString());
    }

}
