package org.fenixedu.ulisboa.specifications.domain.services;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.treasury.services.integration.FenixEDUTreasuryPlatformDependentServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class AuditingServices {

    public static String getCreatorUsername(final Object input) {
        if(input == null) {
            return null;
        }
        
        return FenixEDUTreasuryPlatformDependentServices.readVersioningCreatorUsername(input);
    }

    public static DateTime getCreationDate(final Object input) {
        if(input == null) {
            return null;
        }
        
        return FenixEDUTreasuryPlatformDependentServices.readVersioningCreationDate(input);
    }

    public static String getUpdaterUsername(final Object input) {
        if(input == null) {
            return null;
        }
        
        return FenixEDUTreasuryPlatformDependentServices.readVersioningUpdatorUsername(input);
    }

    public static DateTime getUpdateDate(final Object input) {
        if(input == null) {
            return null;
        }
        
        return FenixEDUTreasuryPlatformDependentServices.readVersioningUpdateDate(input);
    }

    static final private String SINGLE_SPACE = " ";

    static public String getAuditInfo(final Object input) {
        return getAuditInfoCreation(input) + SINGLE_SPACE + getAuditInfoUpdate(input);
    }

    static private String getAuditInfoCreation(final Object input) {
        final String username = getAuditInfoUsername(getCreatorUsername(input));
        final DateTime date = getCreationDate(input);
        return getAuditInfo("auditInfo.creation.msg.empty", "auditInfo.creation.msg.prefix", username, date);
    }

    static private String getAuditInfoUpdate(final Object input) {
        final String username = getAuditInfoUsername(getUpdaterUsername(input));
        final DateTime date = getUpdateDate(input) == null ? null : getUpdateDate(input);
        return getAuditInfo("auditInfo.update.msg.empty", "auditInfo.update.msg.prefix", username, date);
    }

    static private String getAuditInfoUsername(final String input) {
        String result = input;

//        if (StringUtils.isNotBlank(input) && result.contains(".") && !result.contains("@")) {
//            result = StringUtils.substringAfterLast(result, ".");
//        }

        return StringUtils.abbreviate(result, 60);
    }

    static private String getAuditInfo(final String msgEmpty, final String msgPrefix, final String username,
            final DateTime date) {
        String result = StringUtils.EMPTY;

        if (StringUtils.isBlank(username) && date == null) {
            result = ULisboaSpecificationsUtil.bundle(msgEmpty);

        } else {

            result = ULisboaSpecificationsUtil.bundle(msgPrefix);

            if (!StringUtils.isBlank(username)) {
                result += SINGLE_SPACE;
                result += ULisboaSpecificationsUtil.bundle("auditInfo.label.by");
                result += SINGLE_SPACE + username;
            }

            if (date != null) {
                result += SINGLE_SPACE;
                result += ULisboaSpecificationsUtil.bundle("auditInfo.label.in");

                String toString = date.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                toString = toString.replace(" 00:00:00", StringUtils.EMPTY);

                result += SINGLE_SPACE + toString;
            }
        }

        return result + ".";
    }

}
