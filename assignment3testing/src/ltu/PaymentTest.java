package ltu;

import static java.lang.Integer.parseInt;
import static ltu.CalendarFactory.getCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.Calendar;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.io.IOException;


public class PaymentTest
{
    private final int fullTimeIncomeReq = 85813;
    private final int partTimeIncomeReq = 128722;
    private final int partTimeStudyRate = 50;
    private final int fullTimeStudyRate = 100;
    private final int fullTimePayoutAmount = 9904;
    private final int partTimePayoutAmount = 4960;
    private final int fullCourseCompletion = 100;
    private final int noCourseCompletion = 0;
    private final String AgeBelowReq = "20200615-5441";
    private final String AgeAboveReq = "19400615-5441";
    private final String AgeWithinReq = "20010615-5441";
    private ICalendar calendar= getCalendar();

    private PaymentImpl payment;

    @Before
    public void setUp() {
        // set up environment before each test
        try {
            payment = new PaymentImpl(calendar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUp() {
        // clean up after each test
        payment = null;
    }
    
    @Test
    public void fullTimeStudent()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals(fullTimePayoutAmount, result);
    }

    @Test
    public void partTimeStudent()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq-1, partTimeStudyRate, fullCourseCompletion);
        assertEquals(partTimePayoutAmount, result);
    }
        
    @Test
    public void ageTooLow()
    {
        int result = payment.getMonthlyAmount(AgeBelowReq, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals(0, result);
    }

    @Test
    public void incomeTooHigh()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq+1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals(0, result);
    }

    @Test
    public void lowCompletion()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq-1, fullTimeStudyRate, noCourseCompletion);
        assertEquals(0, result);
    }

    @Test
    public void lastWeekdayOfTheMonth()
    {
        String paymentDateString = payment.getNextPaymentDay();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate paymentDate = LocalDate.parse(paymentDateString, format);
        DayOfWeek dow = paymentDate.getDayOfWeek();
        assertTrue(dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY);
    }

    @Test
    public void ageRequirement_Under20_NotEligible() {
        // [ID: 101] Must be at least 20
        String personId = "20060101-1234"; // 19 years old
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("Student younger than 20 should not receive loan or subsidy", 0, result);
    }

    @Test
    public void ageRequirement_Exactly20_Eligible() {
        // Boundary: Age at 20
        String personId = "20050101-1234"; // 20 years old
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("20-year-old should receive both loan and subsidy", fullTimePayoutAmount, result);
    }

    @Test
    public void ageRequirement_At56_EntitledSubsidy() {
        // [ID: 102] No subsidy when they turn 56
        String personId = "19690101-1234"; // 56 years old 1969
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("At 56, only subsidy should be paid", 2816, result);
    }

    @Test
    public void ageRequirement_above56_NoSubsidy() {
        // Boundary: Above 56 not entitled to subsidy
        String personId = "19680101-1234"; // 57 years old
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("Older than 56 should not receive support", 0, result);
    }

    @Test
    public void ageRequirement_At47_NoLoanOnlySubsidy() {
        // [ID: 103] No loan from the year they turn 47
        String personId = "19780101-1234"; // 47 years old
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("At 47, only subsidy should be paid", 2816, result);
    }

    @Test
    public void ageRequirement_At47_LoanOnlySubsidy() {
        // Boundary: Loan still available at 46
        String personId = "19790101-1234"; // 46 years old
        int result = payment.getMonthlyAmount(personId, maxIncomeReq-1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("At 46, both subsidy and loan should be paid", 9904, result);
    }

    @Test
    public void incomeRequirement_FullTimeAboveLimit_NotEligible() {
        // [ID: 301] Max 85 813 for full time
        String personId = "19900101-1234"; // valid age
        int result = payment.getMonthlyAmount(personId, maxIncomeReq+1, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("Full-time student with income > 85 813 should get 0", 0, result);
    }

    @Test
    public void incomeRequirement_FullTimeAtLimit_Eligible() {
        // Exactly at 85 813
        String personId = "19900101-1234";
        int result = payment.getMonthlyAmount(personId, maxIncomeReq, fullTimeStudyRate, fullCourseCompletion);
        assertEquals("Full-time student at income limit should still be eligible", fullTimePayoutAmount, result);
    }

    @Test
    public void incomeRequirement_PartTimeAboveLimit_NotEligible() {
        // [ID: 302] Max 128 722 for part time
        String personId = "19900101-1234";
        int result = payment.getMonthlyAmount(personId, 128723, partTimeStudyRate, fullCourseCompletion);
        assertEquals("Part-time student with income > 128 722 should get 0", 0, result);
    }

    @Test
    public void incomeRequirement_PartTimeAtLimit_Eligible() {
        // Exactly at 128 722
        String personId = "19900101-1234";
        int result = payment.getMonthlyAmount(personId, 128722, partTimeStudyRate, fullCourseCompletion);
        assertEquals("Part-time student at income limit should still be eligible", partTimePayoutAmount, result);
    }

    //201
    @Test
    public void lessThanHalfTimeStudyGetsNoSubsidy() throws Exception 
    {
        int studyRate = 25;               // less than half-time
        int completionRatio = 100;        // valid completion

        int result = payment.getMonthlyAmount(AgeWithinReq,  maxIncomeReq - 1, studyRate, completionRatio);
        assertEquals("Student studying less than half-time should not receive any subsidy",0, result);
    }

    //202
    @Test
    public void halfTimeStudyGetsHalfSubsidy() throws Exception 
    {
        int studyRate = 75;               // <100 but >=50 --> half-time
        int completionRatio = 100;        // valid completion

        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq - 1, studyRate, completionRatio);

        int expected = partTimePayoutAmount;
        assertEquals("Student studying less than full time but at least half-time should receive 50% loan + 50% subsidy",expected, result);
    }

    //203
    @Test
    public void FullTimeStudentGetsFullSubsidy() 
    {
        int completionRatio = fullCourseCompletion; // full time

        int result = payment.getMonthlyAmount(AgeWithinReq, maxIncomeReq - 1, fullTimeStudyRate, completionRatio);

        assertEquals("Full-time student should receive 100% subsidy",fullTimePayoutAmount, result);
    }

    //401
    @Test
    public void CompletionBelow50_NoLoanOrSubsidy()
     {
        int completionRatio = 40;            // less than half-time

        int result = payment.getMonthlyAmount(AgeWithinReq,  maxIncomeReq - 1, fullTimeStudyRate, completionRatio);

        assertEquals("Student with completion ratio < 50% should not receive any loan or subsidy",0, result);
    }

}

