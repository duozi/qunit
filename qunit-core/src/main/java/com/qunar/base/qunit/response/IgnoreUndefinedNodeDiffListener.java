package com.qunar.base.qunit.response;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static org.custommonkey.xmlunit.DifferenceConstants.*;

/**
 * User: zhaohuiyu
 * Date: 8/16/12
 * Time: 4:15 PM
 */
public class IgnoreUndefinedNodeDiffListener implements DifferenceListener {
    private List<String> differents;

    public IgnoreUndefinedNodeDiffListener(List<String> differents) {
        this.differents = differents;
    }

    @Override
    public int differenceFound(Difference difference) {
        if (isIgnoreDifference(difference)) {
            return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
        }
        return RETURN_ACCEPT_DIFFERENCE;
    }

    private boolean isIgnoreDifference(Difference difference) {
        if (ignoreAttributes(difference)
                || ignoreChildren(difference)) {
            return true;
        }
        return false;
    }

    private boolean ignoreChildren(Difference difference) {
        int differenceId = difference.getId();
        if (differenceId != CHILD_NODELIST_LENGTH_ID
                && differenceId != ELEMENT_TAG_NAME_ID
                && differenceId != 22) {
            return false;
        }
        NodeDetail controlNodeDetail = difference.getControlNodeDetail();
        NodeDetail testNodeDetail = difference.getTestNodeDetail();
        Node controlNode = controlNodeDetail.getNode();
        if (controlNode == null) return true;
        NodeList controlChildren = controlNode.getChildNodes();
        NodeList testChildren = testNodeDetail.getNode().getChildNodes();
        if (differenceId == CHILD_NODELIST_LENGTH_ID) {
            int testChildrenLength = testChildren.getLength();
            int controlChildrenLength = controlChildren.getLength();
            boolean result = testChildrenLength >= controlChildrenLength;
            if (!result) {
                String message = String.format("期望有 %s 个节点,实际是 %s\n", controlChildrenLength, testChildrenLength);
                String expected = String.format("期望的为: %s", description(controlChildren));
                String actual = String.format("实际的为: %s", description(testChildren));
                differents.add(message + expected + actual);
            }
            return result;
        }
        if (differenceId == 22 || differenceId != ELEMENT_TAG_NAME_ID) {
            int length = controlChildren.getLength();
            for (int i = 0; i < length; ++i) {
                Node node = controlChildren.item(i);
                return hasNode(testChildren, node);
            }
        }
        return false;
    }

    private String description(NodeList controlChildren) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < controlChildren.getLength(); ++i) {
            sb.append(controlChildren.item(i).toString() + "\n");
        }
        return sb.toString();
    }

    private boolean hasNode(NodeList testChildren, Node controlNode) {
        int length = testChildren.getLength();
        for (int i = 0; i < length; ++i) {
            if (testChildren.item(i).getNodeName().equalsIgnoreCase(controlNode.getNodeName())) {
                return true;
            }
        }
        differents.add(String.format("期望存在节点 %s,但是实际输出中不存在", controlNode.toString()));
        return false;
    }

    private boolean ignoreAttributes(Difference difference) {
        int differenceId = difference.getId();
        if (differenceId != ELEMENT_NUM_ATTRIBUTES_ID
                && differenceId != ATTR_NAME_NOT_FOUND_ID
                && differenceId != ATTR_VALUE_ID) {
            return false;
        }
        NodeDetail controlNodeDetail = difference.getControlNodeDetail();
        NodeDetail testNodeDetail = difference.getTestNodeDetail();
        NamedNodeMap controlAttributes = controlNodeDetail.getNode().getAttributes();
        NamedNodeMap testAttributes = testNodeDetail.getNode().getAttributes();
        if (differenceId == ELEMENT_NUM_ATTRIBUTES_ID) {
            int controlAttributesLen = controlAttributes.getLength();
            int testAttributesLen = testAttributes.getLength();
            boolean result = testAttributesLen >= controlAttributesLen;
            if (!result) {
                String message = String.format("期望有 %s 个属性,实际是 %s\n", controlAttributesLen, testAttributesLen);
                String expected = String.format("期望的为: %s", description(controlAttributes));
                String actual = String.format("实际的为: %s", description(testAttributes));
                differents.add(message + expected + actual);
            }
            return result;
        }
        if (differenceId == ATTR_NAME_NOT_FOUND_ID) {
            int len = controlAttributes.getLength();
            for (int i = 0; i < len; ++i) {
                Node item = controlAttributes.item(i);
                Node testAttribute = testAttributes.getNamedItem(item.getNodeName());
                if (testAttribute == null) {
                    differents.add(String.format("期望存在属性 %s,但是实际输出中不存在", item.toString()));
                    return false;
                }
            }
            return true;
        }
        if (differenceId == ATTR_VALUE_ID) {
            differents.add(String.format("期望在 %s 位置的值是 %s,但实际得到的是 %s", controlNodeDetail.getXpathLocation()
                    , controlNodeDetail.getValue(), testNodeDetail.getValue()));
        }
        return false;
    }

    private String description(NamedNodeMap controlAttributes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < controlAttributes.getLength(); ++i) {
            Node item = controlAttributes.item(i);
            sb.append(item.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public void skippedComparison(Node node, Node node1) {
    }
}
