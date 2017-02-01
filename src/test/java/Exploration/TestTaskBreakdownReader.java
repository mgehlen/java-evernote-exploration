package Exploration;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.zip.DataFormatException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestTaskBreakdownReader {

    private final TaskBreakdownReader taskBreakdownReader = new TaskBreakdownReader() ;

    @Test(dataProvider = "durationNotes")
    public void checkGetDuration(String sessionNotes, int duration) throws DataFormatException {

        assertThat("Session duration not correct!", taskBreakdownReader.getDuration(sessionNotes), is(duration)) ;
    }

    @Test(dataProvider = "testingNotes")
    public void checkGetTesting(String sessionNotes, int testingTime) throws DataFormatException {

        assertThat("Testing time not correct!", taskBreakdownReader.getTesting(sessionNotes), is(testingTime)) ;
    }

    @Test(dataProvider = "bugInvestigation")
    public void checkGetBugInvestigation(String sessionNotes, int bugInvestigationTime) throws DataFormatException {

        assertThat("Bug Investigation time not correct!", taskBreakdownReader.getBugInvestigation(sessionNotes), is(bugInvestigationTime)) ;
    }

    @Test(dataProvider = "sessionSetup")
    public void checkGetSessionSetup(String sessionNotes, int sessionSetupTime) throws DataFormatException {

        assertThat("Bug Investigation time not correct!", taskBreakdownReader.getSessionSetup(sessionNotes), is(sessionSetupTime)) ;
    }

    @Test(dataProvider = "charterVsOpportunity")
    public void checkOpportunityRatio(String sessionNotes, double ration) throws DataFormatException {

        assertThat("Opportunity ratio is not correct!", taskBreakdownReader.getOpportunityRatio(sessionNotes), is(ration)) ;
    }


    @Test(expectedExceptions = DataFormatException.class, dataProvider = "invalidDuration")
    public void checkBadDurationsReturnError(String sessionNotes) throws DataFormatException {

        TaskBreakdownReader taskBreakdownReader = new TaskBreakdownReader() ;
        taskBreakdownReader.getDuration(sessionNotes) ;
    }

    @Test(expectedExceptions = DataFormatException.class, dataProvider = "invalidTestingNotes")
    public void checkBadTestingTasksReturnError(String sessionNotes) throws DataFormatException {

        taskBreakdownReader.getTesting(sessionNotes) ;
    }

    @Test(expectedExceptions = DataFormatException.class, dataProvider = "invalidBugInvestigation")
    public void checkBadBugInvestigationTasksReturnError(String sessionNotes) throws DataFormatException {

        taskBreakdownReader.getBugInvestigation(sessionNotes) ;
    }

    @Test(expectedExceptions = DataFormatException.class, dataProvider = "invalidSessionSetup")
    public void checkBadSessionSetupTasksReturnError(String sessionNotes) throws DataFormatException {

        taskBreakdownReader.getSessionSetup(sessionNotes) ;
    }

    @Test(expectedExceptions = DataFormatException.class, dataProvider = "invalidCharterVsOpportunity")
    public void checkBadOpportunityRatioReturnsError(String sessionNotes) throws DataFormatException {

        taskBreakdownReader.getOpportunityRatio(sessionNotes) ;
    }

    @DataProvider
    private static Object[][] durationNotes() {

        return new Object[][] {
                {"#DURATION\n" +
                        "normal\n" +
                        "#TEST DESIGN AND EXECUTION\n",90},
                {"#DURATION\n" +
                        "medium\n" +
                        "#TEST DESIGN AND EXECUTION\n",60},
                {"#DURATION\n" +
                        "short\n" +
                        "#TEST DESIGN AND EXECUTION\n",30},
        };
    }

    @DataProvider
    private static Object[][] invalidDuration() {
        return new Object[][] {
                {"#DURATION\n" +
                        "none\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "medium,short\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "short,normal,medium\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "none\n" +
                        "#DURATION\n"},
                {"#DURATION\n" +
                        "normal\n" +
                        "#\n"},
                {"#\n" +
                        "normal\n" +
                        "#TEST DESIGN AND EXECUTION\n"},
                {"#DURATION\n" +
                        "normal\n" +
                        "#TEST DESIGN AND EXECUTION\n" + "#DURATION\n" +
                        "short\n" +
                        "#TEST DESIGN AND EXECUTION\n"}
        };
    }

    @DataProvider
    private static Object[][] testingNotes() {

        return new Object[][] {
                {"#TEST DESIGN AND EXECUTION\n" +
                        "100%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n",100},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "50%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n",50},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "0%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n",0}
        };
    }

    @DataProvider
    private static Object[][] invalidTestingNotes() {

        return new Object[][] {
                {"#TEST DESIGN AND EXECUTION\n" +
                        "101%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "40% 50%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "-5%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "abc\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "123456789012345678901234567890%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
                {"#TEST DESIGN AND EXECUTION\n" +
                        "5.5%\n" +
                        "#BUG INVESTIGATION AND REPORTING\n"},
        };
    }

    @DataProvider
    private static Object[][] bugInvestigation() {

        return new Object[][] {
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "100%\n" +
                        "#SESSION SETUP\n",100},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "50%\n" +
                        "#SESSION SETUP\n",50},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "0%\n" +
                        "#SESSION SETUP\n",0}
        };
    }

    @DataProvider
    private static Object[][] invalidBugInvestigation() {

        return new Object[][] {
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "101%\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "40% 50%\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "-5%\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "%\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "abc\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "123456789012345678901234567890%\n" +
                        "#SESSION SETUP\n"},
                {"#BUG INVESTIGATION AND REPORTING\n" +
                        "5.5%\n" +
                        "#SESSION SETUP\n"},
        };
    }

    @DataProvider
    private static Object[][] sessionSetup() {

        return new Object[][] {
                {"#SESSION SETUP\n" +
                        "100%\n" +
                        "#CHARTER VS. OPPORTUNITY\n",100},
                {"#SESSION SETUP\n" +
                        "50%\n" +
                        "#CHARTER VS. OPPORTUNITY\n",50},
                {"#SESSION SETUP\n" +
                        "0%\n" +
                        "#CHARTER VS. OPPORTUNITY\n",0}
        };
    }

    @DataProvider
    private static Object[][] invalidSessionSetup() {

        return new Object[][] {
                {"#SESSION SETUP\n" +
                        "101%\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "40% 50%\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "-5%\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "%\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "abc\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "123456789012345678901234567890%\n" +
                        "#CHARTER VS. OPPORTUNITY\n"},
                {"#SESSION SETUP\n" +
                        "5.5%\n" +
                        "#CHARTER VS. OPPORTUNITYP\n"},
        };
    }

    @DataProvider
    private static Object[][] charterVsOpportunity() {

        return new Object[][] {
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "80/20\n" +
                        "DATA FILES\n",0.2},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "100/0\n" +
                        "DATA FILES\n",0},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "0/100\n" +
                        "DATA FILES\n",1}
        };
    }

    @DataProvider
    private static Object[][] invalidCharterVsOpportunity() {

        return new Object[][] {
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "-80/20\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "80/-20\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "100/100\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "-50/-50\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "50/50 20/70\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "123456789012345678901234567890/123456789012345678901234567890\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "/100\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "/\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "50/\n" +
                        "DATA FILES\n"},
                {"#CHARTER VS. OPPORTUNITY\n" +
                        "-/-\n" +
                        "DATA FILES\n"},
        };
    }

}
