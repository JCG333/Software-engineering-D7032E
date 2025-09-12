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
    private final int maxIncomeReq = 85813;
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
    

}
