package org.fenixedu.ulisboa.specifications.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Strings;

public class UpdateServiceRequestTypeConfigurations extends CustomTask {

    private String[] headers;
    private List<ServiceRequestTypeConfiguration> confs = new ArrayList<ServiceRequestTypeConfiguration>();

    @Override
    public void runTask() throws Exception {

        File data = new File("/home/diogo/Documents/MapaSRT.csv");
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
            for (int i = 8; i < srt.length; i++) {
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
        String[] localizedVal = srtVal.split("/");
        return new LocalizedString(new Locale("pt", "PT"), localizedVal[0]).with(new Locale("en", "GB"), localizedVal[1]);
    }

    private boolean getField(String srtVal) {
        if (!validate(srtVal)) {
            throw new RuntimeException("Unexepcted data format for SRT Field.");
        }
        return srtVal.toUpperCase().equals("S") ? true : false;
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
        String slot = params[0].toUpperCase().equals("S") ? headers[index] : null;
        boolean required = params[1].toUpperCase().equals("S") ? true : false;
        int order = Integer.valueOf(params[2]);

        if (slot != null) {
            return new ServiceRequestSlotEntryConfiguration(slot, required, order);
        } else {
            return null;
        }
    }

}
