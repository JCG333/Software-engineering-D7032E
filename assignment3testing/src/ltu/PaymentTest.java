package ltu;

import static org.junit.Assert.*;

import org.junit.Test;

public class PaymentTest
{
    @Test
    public void testSilly()
    {
        assertEquals(1, 1);
    }

<<<<<<< Updated upstream
=======
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

        // Assert
        assertEquals("Full-time student should receive 100% subsidy",fullTimePayoutAmount, result);
    }

    //401
    @Test
    public void test_ID204_CompletionBelow50_NoLoanOrSubsidy()
     {
        int completionRatio = 40;            // less than half-time

        int result = payment.getMonthlyAmount(AgeWithinReq,  maxIncomeReq - 1, fullTimeStudyRate, completionRatio);

        assertEquals("Student with completion ratio < 50% should not receive any loan or subsidy",0, result);
}







    

>>>>>>> Stashed changes
}
