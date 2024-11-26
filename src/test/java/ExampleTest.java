import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.ContinueRequestParameters;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class ExampleTest
{
    private static final String USERNAME = "";
    private static final String ACCESS_KEY = "";
    private static final String SAUCE_URI_FORMAT = "https://%s:%s@ondemand.eu-central-1.saucelabs.com/wd/hub";
    private static final String SAUCE_URI = "https://ondemand.eu-central-1.saucelabs.com/wd/hub";

    private WebDriver driver;

    @BeforeEach
    void setUp() throws MalformedURLException
    {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    void testRemoteWDBuilderNoSession() throws MalformedURLException
    {
        // Case 1: Unable to create test session when WD created by RemoteWebDriverBuilder with sauceOptions
        // Error message:
        // org.openqa.selenium.SessionNotCreatedException: Could not start a new session.
        // Unable to parse remote response: Keys invalidly mentioned in both alwaysMatch and firstMatch sections in W3C capabilities: set(['sauce:options'])

        ChromeOptions options = createBaseOptions();
        HashMap<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", USERNAME);
        sauceOptions.put("accessKey", ACCESS_KEY);
        sauceOptions.put("name", "Unable to create session test");
        options.setCapability("sauce:options", sauceOptions);

        ClientConfig cc = ClientConfig.defaultConfig()
                .baseUri(URI.create(String.format(SAUCE_URI_FORMAT, USERNAME, ACCESS_KEY)))
                .connectionTimeout(Duration.of(3, ChronoUnit.MINUTES));

        driver = RemoteWebDriver.builder()
                .config(cc)
                .oneOf(options)
                .build();

        // No need to call Augmenter.augment here as it was called in the RemoteWebDriverBuilder.build method.
        // Left it here for debug purposes
        // driver = new Augmenter().augment(driver);

        doSomeBiDiMagic();
        driver.get("https://google.com");
        driver.quit();
    }

    @Test
    void testRemoteWDBuilderSessionCreatedNoBiDi() throws MalformedURLException
    {
        // Case 2: Same as Case 1, but add sauceOption devTools=true. Session created but RemoteDriver doesn't have BiDi capability
        // Error message:
        // java.lang.IllegalArgumentException: WebDriver instance must support BiDi protocol

        ChromeOptions options = createBaseOptions();
        HashMap<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", USERNAME);
        sauceOptions.put("accessKey", ACCESS_KEY);
        sauceOptions.put("name", "No BiDi remote driver");
        sauceOptions.put("devTools", "true");
        options.setCapability("sauce:options", sauceOptions);

        ClientConfig cc = ClientConfig.defaultConfig()
                .baseUri(URI.create(String.format(SAUCE_URI_FORMAT, USERNAME, ACCESS_KEY)))
                .connectionTimeout(Duration.of(3, ChronoUnit.MINUTES));

        driver = RemoteWebDriver.builder()
                .config(cc)
                .oneOf(options)
                .build();

        // No need to call Augmenter.augment here as it was called in the RemoteWebDriverBuilder.build method.
        // Left it here for debug purposes
        // driver = new Augmenter().augment(driver);

        doSomeBiDiMagic();
        driver.get("https://google.com");
        driver.quit();
    }

    @Test
    void testRemoteWDBuilderWithoutSauceCapabilities() throws MalformedURLException
    {
        // Case 3: BiDi RemoteDriver created successfully without SauceLabs capabilities.

        ChromeOptions options = createBaseOptions();

        ClientConfig cc = ClientConfig.defaultConfig()
                .baseUri(URI.create(String.format(SAUCE_URI_FORMAT, USERNAME, ACCESS_KEY)))
                .connectionTimeout(Duration.of(3, ChronoUnit.MINUTES));

        driver = RemoteWebDriver.builder()
                .config(cc)
                .oneOf(options)
                .build();

        // No need to call Augmenter.augment here as it was called in the RemoteWebDriverBuilder.build method.
        // Left it here for debug purposes
        // driver = new Augmenter().augment(driver);

        doSomeBiDiMagic();
        driver.get("https://google.com");
        driver.quit();
    }

    @Test
    void testCreateRemoteWithoutBuilder() throws MalformedURLException
    {
        // Case 4: BiDi RemoteDriver created successfully with SauceLabs capabilities when RemoteWebDriverBuilder is not used

        ChromeOptions options = createBaseOptions();
        HashMap<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", USERNAME);
        sauceOptions.put("accessKey", ACCESS_KEY);
        sauceOptions.put("name", "Success BiDi without RemoteWebDriverBuilder");
        options.setCapability("sauce:options", sauceOptions);

        driver = new RemoteWebDriver(URI.create(SAUCE_URI).toURL(), options);

        // Required to use Augmenter here
        driver = new Augmenter().augment(driver);

        doSomeBiDiMagic();
        driver.get("https://google.com");
        driver.quit();
    }

    private void doSomeBiDiMagic()
    {
        try(Network network = new Network(driver))
        {
            network.addIntercept(new AddInterceptParameters(InterceptPhase.BEFORE_REQUEST_SENT));
            network.onBeforeRequestSent(
                    beforeRequestSent -> {
                        System.out.println("Request sent to url -> " + beforeRequestSent.getRequest().getUrl());
                        network.continueRequest(
                                new ContinueRequestParameters(beforeRequestSent.getRequest().getRequestId()));
                    });
        }
    }

    private ChromeOptions createBaseOptions()
    {
        ChromeOptions options = new ChromeOptions();
        options.setBrowserVersion("latest");
        options.setPlatformName("Windows 11");
        options.setCapability("webSocketUrl", true);
        return options;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
