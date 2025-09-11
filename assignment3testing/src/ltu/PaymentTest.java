package ltu;

import static java.lang.Integer.parseInt;
import static ltu.CalendarFactory.getCalendar;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.io.IOException;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;

public class PaymentTest
{
    private final int fullIncome = 85813;
    private final int partTime = 50;
    private final int fullTime = 100;
    private final int fullAmount = 9904;
    private final int partAmount = 4960;
    private final int fullCompletion = 100;
    private final int noCompletion = 0;
    private final String AgeBelowReq = "20200615-5441";
    private final String AgeAboveReq = "19400615-5441";
    private final String AgeWithinReq = "20010615-5441";
    private ICalendar calendar= getCalendar();

    private PaymentImpl payment;

    @Before
    public void setUp() {
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
        int result = payment.getMonthlyAmount(AgeWithinReq, fullIncome-1, fullTime, fullCompletion);
        assertEquals(fullAmount, result);
    }

    @Test
    public void partTimeStudent()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, fullIncome-1, partTime, fullCompletion);
        assertEquals(partAmount, result);
    }
        
    @Test
    public void ageTooLow()
    {
        int result = payment.getMonthlyAmount(AgeBelowReq, fullIncome-1, fullTime, fullCompletion);
        assertEquals(0, result);
    }

    @Test
    public void incomeTooHigh()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, fullIncome+1, fullTime, fullCompletion);
        assertEquals(0, result);
    }

    @Test
    public void lowCompletion()
    {
        int result = payment.getMonthlyAmount(AgeWithinReq, fullIncome-1, fullTime, noCompletion);
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
