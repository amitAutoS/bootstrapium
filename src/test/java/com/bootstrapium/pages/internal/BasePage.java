package com.bootstrapium.pages.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.google.inject.Inject;

public abstract class BasePage<T extends BasePage<T>> {

    @Inject
    protected WebDriver driver;

    @Inject
    protected WebDriverWait wait;

    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        HtmlElementLoader.populatePageObject((T) this, driver);
        try {
            waitForVisibleElements((T) this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public T get(String url) {
        driver.get(url);
        return get();
    }

    private void waitForVisibleElements(Object pageObject) throws IllegalArgumentException, IllegalAccessException {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Visible) {
                    field.setAccessible(true);
                    Object obj = field.get(pageObject);
                    WebElement element = null;
                    if (obj instanceof TypifiedElement) {
                        element = ((TypifiedElement) obj).getWrappedElement();
                    } else if (obj instanceof WebElement) {
                        element = (WebElement) obj;
                    }
                    wait.until(ExpectedConditions.visibilityOf(element));
                }
            }
        }
    }
}
