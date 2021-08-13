
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import java.util.*;



public class Main {

    final static String DRIVER_NAME = "webdriver.edge.driver";
    final static String DRIVER_PATH = "C:\\Users\\97254\\Downloads\\edgedriver_win64\\msedgedriver.exe";
    final static String ROOT_URL = "https://www.aac.ac.il/";
    final static int DELAY_MIL_SEC = 1000;

    public static void main(String[] args) {

        Scanner lnScanner = new Scanner(System.in);
        Scanner intScanner = new Scanner(System.in);

        System.out.println("Enter the user name: ");
        String userName = lnScanner.nextLine();
        System.out.println("Enter the password: ");
        String password = lnScanner.nextLine();

        System.setProperty(DRIVER_NAME, DRIVER_PATH);
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();

        try {
            driver.get(ROOT_URL);
            WebElement personalInfo = driver.findElement(By.linkText("מידע אישי"));
            personalInfo.click();
            WebElement usrNameField = driver.findElement(By.id("Ecom_User_ID"));
            WebElement usrPassWordField = driver.findElement(By.id("Ecom_Password"));
            usrNameField.sendKeys(userName);
            usrPassWordField.sendKeys(password);
            driver.findElement(By.id("wp-submit")).click();

            if (!driver.getTitle().equals("פורטל שירותי מידע אישי")) {
                System.out.println("Wrong user name or password");
            } else {
                WebElement moodle = driver.findElement(By.linkText("מערכת Moodle"));
                moodle.click();

                try {
                    Thread.sleep(DELAY_MIL_SEC);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                WebElement contentPage = driver.findElement(By.cssSelector("*[data-region='paged-content-page']"));
                List<WebElement> contentLinks = contentPage.findElements(By.tagName("a"));
                Map<String, String> courses = new HashMap<String, String>();

                for (WebElement link : contentLinks) {
                    if (isDesired(link)) {
                        courses.put(link.getText(), link.getAttribute("href"));
                    }
                }

                int i = 1;
                for (String courseName : courses.keySet()) {
                    System.out.println(i+" -- "+courseName+'\n');
                    i++;
                }

                boolean isSelected = false;
                do {
                    try {
                        System.out.println("Choose the course:");
                        int userChoise = intScanner.nextInt();
                        if (userChoise > i || userChoise <= 0) {
                            System.out.println("There is no such course");
                        } else {
                            isSelected = true;
                            List<String> valueList = new ArrayList<String>(courses.values());
                            driver.get(valueList.get(userChoise - 1));
                            WebElement usrMenu = driver.findElement(By.className("usermenu"));
                            usrMenu.click();
                            WebElement logOut = driver.findElement(By.cssSelector("*[data-title='logout,moodle']"));
                            logOut.click();
                            try {
                                Thread.sleep(DELAY_MIL_SEC);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            WebElement exit = driver.findElement(By.linkText("יציאה"));
                            exit.click();
                        }

                    } catch (InputMismatchException e) {
                        System.out.println("Error");
                        intScanner.nextLine();
                    }
                } while (!isSelected);
            }
        } catch (WebDriverException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDesired(WebElement link) {
        return (
                link.getAttribute("href").contains("course") &&
                link.getAttribute("href").contains("id") &&
                link.getText().contains("שם הקורס")
        );
    }
}