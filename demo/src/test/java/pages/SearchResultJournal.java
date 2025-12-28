package pages;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SearchResultJournal {
    WebElement journalWebElement;
    long journalTimestamp;
    
    public SearchResultJournal(WebElement webElement) throws ParseException {
        journalWebElement = webElement;
        journalTimestamp = setJournalLastUpdateDate();
    }

    public String getJournalName() {
        return journalWebElement.findElement(By.cssSelector("h3 > a")).getText();
    }

    public long getJournalLastUpdateDate() {
        return journalTimestamp;
    }

    private long setJournalLastUpdateDate() throws ParseException {
        String dateTextRaw = journalWebElement.findElement(By.cssSelector("aside > ul > li:nth-of-type(1)")).getText();
        
        String[] textArray = dateTextRaw.split(" ");

        String monthName;
        String dayNumber = textArray[textArray.length - 3];
        String yearNumber = textArray[textArray.length - 1];
        if (textArray[textArray.length - 2].equals("Sept")) {monthName = "Sep";}
        else { monthName = textArray[textArray.length - 2]; }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM")
                                                            .withLocale(Locale.ENGLISH);

        // Parse the input string into a temporary accessor object
        TemporalAccessor temporalAccessor = inputFormatter.parse(monthName);

        // Extract the month value (an integer from 1 to 12)
        //int monthNumber = temporalAccessor.get(java.time.temporal.ChronoField.MONTH_OF_YEAR);
        String monthNumber = String.format("%02d", temporalAccessor.get(java.time.temporal.ChronoField.MONTH_OF_YEAR));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(yearNumber + "-" + monthNumber + "-" + dayNumber );
        
        //Timestamp timestamp = new Timestamp(parsedDate.getTime());
        // Format the integer into a two-digit string with leading zeros if necessary
        return parsedDate.getTime();
        }
    }
