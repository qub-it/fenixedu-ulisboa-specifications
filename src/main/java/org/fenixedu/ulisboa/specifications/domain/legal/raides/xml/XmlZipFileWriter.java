package org.fenixedu.ulisboa.specifications.domain.legal.raides.xml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFile;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportResultFileType;

import com.google.common.base.Strings;

public class XmlZipFileWriter {

    private static final int TIMEOUT = 2000;

    public static LegalReportResultFile write(final LegalReportRequest reportRequest,
            final RaidesRequestParameter raidesRequestParameter, final Raides raides, final LegalReportResultFile xmlResultFile) {

        if (Strings.isNullOrEmpty(((RaidesInstance) reportRequest.getLegalReport()).getPasswordToZip())) {
            return null;
        }

        try {
            long currentTimeMillis = System.currentTimeMillis();
            byte[] content = IOUtils.toByteArray(xmlResultFile.getStream());

            final String xmlTempFileName = "/tmp/A" + currentTimeMillis;
            FileWriter fw = new FileWriter(xmlTempFileName);

            IOUtils.write(content, fw);

            fw.close();

            final String zipFilename =
                    "A0" + raidesRequestParameter.getMoment() + raidesRequestParameter.getInstitutionCode() + ".zip";

            Process outprocess = new ProcessBuilder("zip", "--password", ((RaidesInstance) reportRequest.getLegalReport()).getPasswordToZip(),
                    "/tmp/" + zipFilename, xmlTempFileName).start();

            outprocess.wait(TIMEOUT);
            
            FileInputStream zippedContenrFIS = new FileInputStream("/tmp/" + zipFilename);
            byte[] zippedContent = IOUtils.toByteArray(zippedContenrFIS);
            
            zippedContenrFIS.close();
            
            return new LegalReportResultFile(reportRequest, LegalReportResultFileType.ZIP, zipFilename, zippedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
