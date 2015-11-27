package org.fenixedu.ulisboa.specifications.task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ImportServiceRequestTypeConfiguration extends CustomTask {

    private String[] headers;
    private List<ServiceRequestTypeConfiguration> confs = new ArrayList<ServiceRequestTypeConfiguration>();

    @Override
    public void runTask() throws Exception {

        InputStream data = Bennu.class.getResourceAsStream(
                "/SRTConfiguration/MapaSRT-" + Bennu.getInstance().getInstitutionUnit().getAcronym() + ".csv");
        Scanner reader = new Scanner(data);
        //populate headers;
        headers = reader.nextLine().split("\t");
        while (reader.hasNextLine()) {
            String[] srt = reader.nextLine().split("\t");
            ServiceRequestTypeConfiguration conf = new ServiceRequestTypeConfiguration();
            conf.setSrtCode(getCode(srt[0]));
            conf.setSrtName(getName(srt[1]));
            conf.setActive(getField(srt[2]));
            conf.setEmolument(getField(srt[3]));
            conf.setNotify(getField(srt[4]));
            conf.setPrint(getField(srt[5]));
            conf.setOnline(getField(srt[6]));
            conf.setCategory(getCategory(srt[7]));
            conf.setProcessors(getProcessors(srt[8]));
            for (int i = 9; i < srt.length; i++) {
                ServiceRequestSlotEntryConfiguration slot = createSlot(i, srt[i]);
                if (slot != null) {
                    conf.addSlotConfiguration(slot);
                }
            }
            confs.add(conf);
        }
        reader.close();

        for (ServiceRequestTypeConfiguration conf : confs) {
            conf.execute();
        }

    }

    private boolean validate(String string) {
        return !Strings.isNullOrEmpty(string) && !string.equals("0");
    }

    private String getCode(String srtVal) {
        if (!validate(srtVal)) {
            throw new RuntimeException("Unexepcted data format for SRT Code.");
        }
        return srtVal;
    }

    private LocalizedString getName(String srtVal) {
        if (!validate(srtVal)) {
            throw new RuntimeException("Unexepcted data format for SRT Name.");
        }
        final JsonParser parser = new JsonParser();
        return LocalizedString.fromJson(parser.parse(srtVal));
    }

    private boolean getField(String srtVal) {
        if (!validate(srtVal)) {
            throw new RuntimeException("Unexepcted data format for SRT Field.");
        }
        return srtVal.toUpperCase().equals("Y") ? true : false;
    }

    private ServiceRequestCategory getCategory(String srtVal) {
        if (!validate(srtVal)) {
            throw new RuntimeException("Unexepcted data format for SRT Category.");
        }
        return ServiceRequestCategory.valueOf(srtVal);
    }

    private ServiceRequestSlotEntryConfiguration createSlot(int index, String slotConf) {
        if (!validate(slotConf)) {
            return null;
        }
        String[] params = slotConf.split("/");
        if (params[0].equals("0")) {
            return null;
        }
        String slot = params[0].toUpperCase().equals("Y") ? headers[index] : null;
        boolean required = params[1].toUpperCase().equals("Y") ? true : false;
        int order = Integer.valueOf(params[2]);

        if (slot != null) {
            return new ServiceRequestSlotEntryConfiguration(slot, required, order);
        } else {
            return null;
        }
    }

    private List<String> getProcessors(String srtVal) {
        if (Strings.isNullOrEmpty(srtVal)) {
            return new ArrayList<String>();
        }
        String[] processors = srtVal.split(" \\|\\| ");
        return Arrays.asList(processors);
    }

}
