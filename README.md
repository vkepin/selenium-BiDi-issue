# Remote WebDriver Issues with BiDi Capabilities on SauceLabs

This Java project demonstrates different behaviors encountered when attempting to create a remote WebDriver instance on the SauceLabs platform using Selenium's `RemoteWebDriverBuilder` with BiDi capabilities.

The project provides four distinct test cases, implemented with **JUnit 5**, to highlight these issues and their outcomes. The build platform is **Gradle** with a wrapper included.

## Prerequisites

- Java
- Gradle (Wrapper included, no need to install)
- SauceLabs account with valid credentials

## Test Cases

The following cases demonstrate the behavior of `RemoteWebDriverBuilder` under different configurations:

1. **Case 1**:
    - **Description**: Create a WebDriver instance using `RemoteWebDriverBuilder` with SauceLabs capabilities.
    - **Result**: Session could not be created.

2. **Case 2**:
    - **Description**: Create a WebDriver instance using `RemoteWebDriverBuilder` with SauceLabs capabilities and additional DevTools capabilities.
    - **Result**: Session is created, but the WebDriver does not support BiDi capabilities.

3. **Case 3**:
    - **Description**: Create a WebDriver instance using `RemoteWebDriverBuilder` without SauceLabs-specific capabilities.
    - **Result**: Success, session is created, and BiDi capabilities are supported.

4. **Case 4**:
    - **Description**: Create a WebDriver instance without using `RemoteWebDriverBuilder`, directly specifying SauceLabs capabilities.
    - **Result**: Success, session is created, and BiDi capabilities are supported.

## Project Structure

```plaintext
src
├── test
│   └── java
│       └── com.example.webdriverissues
│           └── ExampleTest.java
build.gradle
gradlew
gradlew.bat
settings.gradle
README.md
```

## How to Run the Tests

1. `git clone https://github.com/vkepin/selenium-BiDi-issue.git`
2. `export SAUCE_USERNAME=<username>`
3. `export SAUCE_API_KEY=<key>`
4. `./gradlew test`

Test results will be displayed in the console and saved in the build/reports/tests/test directory.
