package Exploration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * has several methods to retrieve the data in a specific task from the break down list.
 */
class TaskBreakdownReader {

    private static final Map<String, Integer> durationMapping  ;
    static
    {
        durationMapping = new HashMap<>();
        durationMapping.put("normal", 90);
        durationMapping.put("medium", 60);
        durationMapping.put("short",30);
    }

    private static final String DURATION = "#DURATION" ;
    private static final String TESTING = "#TEST DESIGN AND EXECUTION";
    private static final String BUG_INVESTIGATION = "#BUG INVESTIGATION AND REPORTING";
    private static final String SETUP = "#SESSION SETUP";
    private static final String CHARTER_VS_OPPORTUNITY = "#CHARTER VS. OPPORTUNITY";
    private static final String DATA_FILES = "DATA FILES" ;

    public int getDuration(String sessionNotes) throws DataFormatException {

        return findSessionDuration(getTaskSection(sessionNotes, DURATION, TESTING) ) ;
    }

    public int getTesting(String sessionNotes) throws DataFormatException {

        return findValidTimeForSection(getTaskSection(sessionNotes, TESTING,BUG_INVESTIGATION)) ;
    }

    public int getBugInvestigation(String sessionNotes) throws DataFormatException {

        return findValidTimeForSection(getTaskSection(sessionNotes, BUG_INVESTIGATION, SETUP)) ;
    }

    public int getSessionSetup(String sessionNotes) throws  DataFormatException {

        return findValidTimeForSection(getTaskSection(sessionNotes, SETUP, CHARTER_VS_OPPORTUNITY)) ;
    }

    public double getOpportunityRatio(String sessionNotes) throws DataFormatException{

        return findOpportunityRatio(getTaskSection(sessionNotes, CHARTER_VS_OPPORTUNITY, DATA_FILES)) ;

    }

    private double findOpportunityRatio(String CharterVsOppSection) throws DataFormatException {

        Matcher matcher = Pattern.compile("-?\\d+/-?\\d+").matcher(CharterVsOppSection);
        double ratio = -1 ;

        while (matcher.find())
        {
            if (ratio != -1) {
                throw new DataFormatException("More than one entry for charter vs opportunity") ;
            }

            String ratioTerm = matcher.group(0) ;

            double charter = Double.valueOf(ratioTerm.substring(0,ratioTerm.indexOf("/"))) ;
            double opportunity = Double.valueOf(ratioTerm.substring(ratioTerm.indexOf("/") + 1)) ;

            if (!isRatioValid(charter,opportunity)) {
                throw new DataFormatException("invalid format for charter vs opportunity ratio") ;
            }

            ratio = calculateRatio(charter,opportunity) ;
        }

        if (ratio == -1) {
            throw new DataFormatException("invalid data for charta vs. opportunity ratio") ;
        }

        return ratio ;
    }

    private boolean isRatioValid(double charter, double opportunity) {

        return (charter >= 0 && opportunity >= 0) &&
                (charter + opportunity == 100) ;

    }

    private double calculateRatio(double charter, double opportunity) {

        if (opportunity == 0 && charter == 100) {
            return 0.0 ;
        }

        if (opportunity == 100 && charter == 0) {
            return 1.0;
        }

        return opportunity / (opportunity + charter);
    }

    private int findValidTimeForSection(String section) throws DataFormatException {

        Matcher matcher = Pattern.compile("-?\\d+").matcher(section);
        Integer sectionTime = -1 ;

        while (matcher.find())
        {
            if (sectionTime != -1) {
                throw new DataFormatException("More than one timing for this section") ;
            }

            try {
                sectionTime = Integer.valueOf(matcher.group(0)) ;
            } catch (NumberFormatException e) {
                throw new DataFormatException("entry for section time is not a valid number") ;
            }
        }

        if (sectionTime < 0 || sectionTime > 100) {
            throw new DataFormatException("invalid data for section timing.") ;
        }

        return sectionTime ;
    }

    private String getTaskSection(String sessionNotes, String startSection, String nextSection) throws DataFormatException {

        if (isTaskSectionValid(sessionNotes,startSection,nextSection)) {

            return sessionNotes.substring(sessionNotes.indexOf(startSection),
                    sessionNotes.indexOf(nextSection)) ;
        }

        throw new DataFormatException("Invalid format in your task breakdown section") ;
    }

    private boolean isTaskSectionValid(String sessionNotes, String startSection, String nextSection) {

        return (sessionNotes.contains(startSection) && sessionNotes.contains(nextSection) &&
           (sessionNotes.indexOf(startSection) < sessionNotes.indexOf(nextSection)) &&
           (sessionNotes.indexOf(startSection) == sessionNotes.lastIndexOf(startSection)) &&
           (sessionNotes.indexOf(nextSection) == sessionNotes.lastIndexOf(nextSection))) ;

    }

    private int findSessionDuration(String durationInformation) throws DataFormatException {

        int durationInMinutes = 0;
        for (Map.Entry<String, Integer> durationUnit : durationMapping.entrySet())
        {
            if (durationInformation.contains(durationUnit.getKey())) {
                if (durationInMinutes != 0) {
                    throw new DataFormatException("More than one duration indicator in your duration section") ;
                }

                durationInMinutes = durationUnit.getValue() ;
            }
        }

        if (durationInMinutes > 0) {
            return durationInMinutes ;
        }

        throw new DataFormatException("No duration indicator your note.") ;
    }
}
