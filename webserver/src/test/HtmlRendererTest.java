package test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import webserver.HtmlRenderer;
import webserver.WebServer;

import java.io.ByteArrayOutputStream;

public class HtmlRendererTest {
    @BeforeClass
    public static void setUp() {
        HtmlRenderer.getInstance().setRootPageLocation("./src/html_resources");
    }

    @Test
    public void nullRequestTest(){
        String filePath = HtmlRenderer.getInstance().renderHtmlPage(null, null);
        Assert.assertEquals(null, filePath);
    }

    @Test(expected = Exception.class)
    public void nonNullRequestButNullOutputStreamTest(){
        HtmlRenderer.getInstance().renderHtmlPage("GET /", null);
    }

    @Test
    public void noSpecificPageRequestTest() {
        try {
            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /", new ByteArrayOutputStream());
            Assert.assertTrue(filePath.endsWith("index.html"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void indexPageRequestTest(){
        try {
            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /index.html", new ByteArrayOutputStream());
            Assert.assertTrue(filePath.endsWith("index.html"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void secondPageRequestTest(){
        try {
            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /second_page.html", new ByteArrayOutputStream());
            Assert.assertTrue(filePath.endsWith("second_page.html"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void thirdPageRequestTest(){
        try {
            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /directory/third_page.html", new ByteArrayOutputStream());
            Assert.assertTrue(filePath.endsWith("third_page.html"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void inexistentPageRequestTest(){
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            String filePath = HtmlRenderer.getInstance().renderHtmlPage("GET /random.html", stream);
            Assert.assertTrue(stream.toString().contains("404"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
