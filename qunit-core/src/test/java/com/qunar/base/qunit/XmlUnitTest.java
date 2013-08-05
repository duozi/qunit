package com.qunar.base.qunit;

import com.qunar.base.qunit.response.IgnoreUndefinedNodeDiffListener;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * User: zhaohuiyu
 * Date: 7/2/12
 * Time: 5:15 PM
 */
public class XmlUnitTest {
    @Test
    public void should_pass_given_two_same_xml() throws IOException, SAXException {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        Diff diff = new Diff(" <user> <name>admin</name><pwd>12345</pwd></user> ",
                " <user><pwd>12345</pwd><name>admin</name></user> ");
        assertTrue(diff.similar());

    }

    @Test
    public void should_pass_given_not_defined_attribute_in_expected() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        Diff diff = new Diff("<book><content>test</content></book>",
                "<book><content page=\"100\">test</content></book>");
        diff.overrideDifferenceListener(differentListener);
        assertTrue(diff.similar());
    }

    @Test
    public void should_pass_given_not_defined_node_in_expected() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        Diff diff = new Diff("<book><content>test</content></book>",
                "<book><title>test</title><content>test</content></book>");
        diff.overrideDifferenceListener(differentListener);
        Assert.assertTrue(diff.similar());
    }

    @Test
    public void should_not_pass_given_defined_attribute_in_expected_but_not_defined_in_actual() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        Diff diff = new Diff("<book><content page=\"100\">test</content></book>",
                "<book><content>test</content></book>");
        diff.overrideDifferenceListener(differentListener);
        assertFalse(diff.similar());
    }

    @Test
    public void should_not_pass_given_defined_node_in_expected_but_not_defined_in_actual() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        Diff diff = new Diff("<book><title>test</title><content>test</content></book>",
                "<book><content>test</content></book>");
        diff.overrideDifferenceListener(differentListener);
        assertFalse(diff.similar());
    }

    @Test
    public void should_not_pass_given_not_same_attribute_value() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        Diff diff = new Diff("<book><content title=\"书名\">test</content></book>",
                "<book><content title=\"书名1\">test</content></book>");
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        diff.overrideDifferenceListener(differentListener);
        assertFalse(diff.similar());
    }

    @Test
    public void should_not_pass_given_not_same_node_value() throws IOException, SAXException {
        prepare_xml_unit();
        List<String> differents = new ArrayList<String>();
        Diff diff = new Diff("<book><content title=\"书名\">test</content></book>",
                "<book><content title=\"书名\">test1</content></book>");
        DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
        diff.overrideDifferenceListener(differentListener);
        assertFalse(diff.similar());
    }

    private void prepare_xml_unit() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
    }
}
