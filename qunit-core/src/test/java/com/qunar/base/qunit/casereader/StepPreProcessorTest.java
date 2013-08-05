package com.qunar.base.qunit.casereader;

import com.qunar.base.qunit.preprocessor.StepPreProcessor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 9/26/12
 */
public class StepPreProcessorTest {
    @Test
    public void should_prepare_step() throws DocumentException {
        String xml = "<case>" +
                "<template id=\"temp2\">\n" +
                " <print />\n" +
                "</template>" +
                "<step ref=\"temp2\" />" +
                "</case>";
        Document doc = loadXml(xml);
        StepPreProcessor processor = new StepPreProcessor();
        Element step = doc.getRootElement().element("step");
        List<Node> nodes = processor.prepare(doc, step);
        assertThat(nodes.size(), is(1));
    }

    @Test
    public void should_prepare_step_given_step_occur_twice() throws DocumentException {
        String xml = "<case>" +
                "<template id=\"temp2\">\n" +
                " <print />\n" +
                "</template>" +
                "<step ref=\"temp2\" />" +
                "</case>";
        Document doc = loadXml(xml);
        StepPreProcessor processor = new StepPreProcessor();
        Element step = doc.getRootElement().element("step");
        List<Node> nodes1 = processor.prepare(doc, step);
        List<Node> nodes2 = processor.prepare(doc, step);

        assertThat(nodes1.size(), is(1));
        assertThat(nodes2.size(), is(1));

    }

    private Document loadXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader stream = new StringReader(xml);
        return reader.read(stream);
    }
}
