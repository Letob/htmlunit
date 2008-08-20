/*
 * Copyright (c) 2002-2008 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.html;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 * Tests for elements inside {@link HtmlNoScript}.
 *
 * @version $Revision: 3075 $
 * @author Ahmed Ashour
 */
public class HtmlNoScriptTest extends WebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testGetElementById() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    alert(document.getElementById('second'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "    <input type='text' id='first' name='textfield'/>\n"
            + "    <noscript>\n"
            + "    <input type='text' id='second' name='button'/>\n"
            + "    </noscript>\n"
            + "</body></html>";

        final String[] expectedAlerts = {"null"};
        final List<String> collectedAlerts = new ArrayList<String>();
        loadPage(htmlContent, collectedAlerts);
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testChildNodes() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    var noscript = document.getElementById('myDiv' ).childNodes.item(0);\n"
            + "    alert(noscript.childNodes.length);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "    <div id='myDiv'><noscript>\n"
            + "        <input type='text' name='button'/>\n"
            + "      </noscript></div>\n"
            + "</body></html>";

        final String[] expectedAlerts = {"0"};
        final List<String> collectedAlerts = new ArrayList<String>();
        loadPage(htmlContent, collectedAlerts);
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testJavaScript() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title>\n"
            + "<script>\n"
            + "  alert(1);\n"
            + "</script>\n"
            + "<noscript>\n"
            + "  <script>\n"
            + "    alert(2);\n"
            + "  </script>\n"
            + "</noscript>\n"
            + "</head><body>\n"
            + "</body></html>";

        final String[] expectedAlerts = {"1"};
        final List<String> collectedAlerts = new ArrayList<String>();
        loadPage(htmlContent, collectedAlerts);
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testFormValues() throws Exception {
        final String htmlContent
            = "<html><body>\n"
            + "<form name='item' method='post'>\n"
            + "  <noscript>\n"
            + "    <input type=hidden name='__webpage_no_js__' value='1'>\n"
            + "  </noscript>\n"
            + "  <input type=hidden name='myParam' value='myValue'>\n"
            + "  <input type='submit'>\n"
            + "</form>\n"
            + "</body></html>";

        final HtmlPage firstPage = loadPage(htmlContent);
        final HtmlForm form = firstPage.getForms().get(0);
        final HtmlPage secondPage = (HtmlPage) form.submit((SubmittableElement) null);

        final MockWebConnection mockWebConnection = getMockConnection(secondPage);
        assertEquals(1, mockWebConnection.getLastParameters().size());
        assertTrue(secondPage.asXml().indexOf("__webpage_no_js__") > -1);
    }
}