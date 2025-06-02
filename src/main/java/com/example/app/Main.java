package com.example.app;

import com.example.app.Entity.MusicAlbum;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    static final String ARTIST_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[1]/td[2]/input";
    static final String TITLE_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[2]/td[2]/input";
    static final String TRACK_LEFT_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[1]/table/tbody/tr[%d]/td[2]/input";
    static final String TRACK_RIGHT_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[3]/td[2]/table/tbody/tr/td[2]/table/tbody/tr[%d]/td[2]/input";
    static final String CASE_TYPE_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[4]/td[2]/input[2]";
    static final String PAPER_FORMAT_INPUT = "/html/body/table[2]/tbody/tr/td[1]/div/form/table/tbody/tr[5]/td[2]/input[2]";
    static final String SUBMIT_BTN = "/html/body/table[2]/tbody/tr/td[1]/div/form/p/input";

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.webDriver", "C:\\Users\\egorm\\Downloads\\chromedriver-win64\\chromedriver.exe");
        Path outputDir = Paths.get("result");
        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> prefs = Map.of(
                "download.default_directory", outputDir.toString(),
                "download.prompt_for_download", false,
                "plugins.always_open_pdf_externally", true
        );
        chromeOptions.setExperimentalOption("prefs", prefs);

        WebDriver webDriver = new ChromeDriver(chromeOptions);

        try {
            String data = Files.readString(Paths.get("data/data.txt"));
            Gson gson = new Gson();
            MusicAlbum musicAlbum = gson.fromJson(data, MusicAlbum.class);

            webDriver.manage().window().maximize();
            webDriver.get("https://www.papercdcase.com/index.php");

            webDriver.findElement(By.xpath(ARTIST_INPUT)).sendKeys(musicAlbum.getArtistName());
            webDriver.findElement(By.xpath(TITLE_INPUT)).sendKeys(musicAlbum.getTitle());

            List<String> allTracks = musicAlbum.getNameSongs();

            List<String> stringListLeft = allTracks.subList(0, Math.min(8, allTracks.size()));
            List<String> stringListRight = allTracks.size() > 8 ? allTracks.subList(8, Math.min(16, allTracks.size())) : List.of();

            for (int i = 0; i < stringListLeft.size(); i++) {
                String xpath = String.format(TRACK_LEFT_INPUT, i + 1);
                webDriver.findElement(By.xpath(xpath)).sendKeys(stringListLeft.get(i));
            }

            for (int i = 0; i < stringListRight.size(); i++) {
                String xpath = String.format(TRACK_RIGHT_INPUT, i + 1);
                webDriver.findElement(By.xpath(xpath)).sendKeys(stringListRight.get(i));
            }

            WebElement webDriverElement = webDriver.findElement(By.xpath(CASE_TYPE_INPUT));
            if (!webDriverElement.isSelected()) webDriverElement.click();

            WebElement a4Paper = webDriver.findElement(By.xpath(PAPER_FORMAT_INPUT));
            if (!a4Paper.isSelected()) a4Paper.click();

            webDriver.findElement(By.xpath(SUBMIT_BTN)).click();
            Thread.sleep(5000);


        } catch (Exception e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
        } finally {
            webDriver.quit();
        }
    }
}
