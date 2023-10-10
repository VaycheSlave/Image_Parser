package org.example;

import org.apache.commons.io.FileUtils; //Apache Commons IO
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите тему для картинок:");

        String query = scanner.nextLine();
        System.out.println("Сколько картинок скачать:");
        int pagesNeeded = Integer.parseInt(scanner.nextLine());

        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://images.google.com/");

        String xpathInput = "/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/textarea";
        WebElement element = webDriver.findElement(By.xpath(xpathInput));
        element.sendKeys(query);
        element.sendKeys(Keys.ENTER);

        WebElement containerElement = webDriver.findElement(By.id("islrg"));
        List<WebElement> divElements = containerElement.findElements(By.cssSelector("div[data-ved]"));

        int i = 0;
        int numberImageNow = 0;
        for (WebElement div : divElements)
        {
            i++;
            if (i % 25 != 0)
            {
                numberImageNow++;
                WebElement imgElement = div.findElement(By.tagName("img"));
                String srcValue = imgElement.getAttribute("src");
                System.out.println("src: " + srcValue);

                ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true)", imgElement);
                ArrayList<String> tabs = openImageInNewTab(webDriver, imgElement);
                sleep(1000);

                saveImage(webDriver, numberImageNow, query);

                webDriver.close();

                webDriver.switchTo().window(tabs.get(0));
                sleep(1000);
            }
            if (numberImageNow == pagesNeeded)
            {
                break;
            }
        }

        if (numberImageNow != pagesNeeded)
        {
            List<WebElement> divElements2 = containerElement.findElements(By.cssSelector("div[decode-data-ved]"));

            for (WebElement div2 : divElements2)
            {
                List<WebElement> data = div2.findElements(By.cssSelector("div[data-ved]"));
                for (WebElement date : data)
                {
                    i++;
                    if (i % 25 != 0)
                    {
                        numberImageNow++;
                        // Ищем вложенный img элемент
                        WebElement imgElement = date.findElement(By.tagName("img"));
                        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true)", imgElement);
                        ArrayList<String> tabs = openImageInNewTab(webDriver, imgElement);
                        //sleep(1000);

                        saveImage(webDriver, numberImageNow, query);

                        webDriver.close();

                        webDriver.switchTo().window(tabs.get(0));
                        sleep(1000);
                    }
                    if (numberImageNow++ == pagesNeeded)
                    {
                        break;
                    }
                }

            }
        }

        System.out.println("Images are downloaded!");
    }

    private static void saveImage(WebDriver webDriver, int numberImage, String query)
    {
        WebElement image = webDriver.findElement(By.tagName("img"));
        File picture = image.getScreenshotAs(OutputType.FILE);
        new File(query).mkdir();
        try
        {
            FileUtils.copyFile(picture, new File(query + "/" + numberImage + ".jpeg"));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    private static ArrayList<String> openImageInNewTab(WebDriver webDriver, WebElement imageElement)
    {
        // Извлекаем значение атрибута src
        String link = imageElement.getAttribute("src");
        ((JavascriptExecutor) webDriver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(1));
        webDriver.get(link);

        return tabs;
    }
}



