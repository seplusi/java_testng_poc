package pages;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import resources.objects.CommonPage;

public class SearchResultPage extends CommonPage{

    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public void wait4ElementAttr2Be(String element, String attr, String value) {
        long init_ts = Instant.now().getEpochSecond();
        boolean flag = false;

        while ( Instant.now().getEpochSecond() <= init_ts + timeout & !flag ) {
            try {
                if (getElementAttr(element, attr).equals(value)) {
                    flag = true;
                    continue;
                }
                sleep(500);
            } catch (StaleElementReferenceException e) {
                sleep(500);
            }
        }
        if ( !flag ) {throw new TimeoutException("Attribute " + attr + " never became " + value);}
    }

    public List<SearchResultJournal> getAllJournalsInResuls(String elementsText, String text) throws ParseException {
        List<SearchResultJournal> resultJournal = new ArrayList<>();
        List<WebElement> allJournals = waitDriver.ignoring(StaleElementReferenceException.class).until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(prop.getProperty(elementsText).split("\\|")[0])));

        for (int i = 0; i < allJournals.size(); i++) {
            resultJournal.add(new SearchResultJournal(allJournals.get(i)));
        }

        return resultJournal;
    }
}
