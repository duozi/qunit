package com.qunar.base.qunit.transport.zookeeper;

import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.common.zookeeper.ChangedEvent;
import qunar.tc.common.zookeeper.NodeListener;
import qunar.tc.common.zookeeper.ZKClient;
import qunar.tc.common.zookeeper.ZKClientCache;
import qunar.tc.qmq.common.Disposable;

import java.util.List;

import static qunar.tc.qmq.utils.Constants.CONSUMER_SUBJECT_ROOT;

/**
 * User: zhaohuiyu
 * Date: 4/2/13
 * Time: 7:04 PM
 */
public class ZKObserver implements Disposable {
    private final static Logger logger = LoggerFactory.getLogger(ZKObserver.class);

    private final static Logger consumerLostLogger = LoggerFactory.getLogger("consumerLost");

    private final static int zkPathParts = 3;

    private final static int zkGroupPathParts = 2;
    private final static int prefixIndex = 0;
    private final static int consumerGroupIndex = 1;

    private final static int consumerAddressIndex = 2;

    private final EventBus eventBus = new EventBus("consumer_changed");

    private final SubjectNodeChangedListener subjectNodeChangedListener;
    private final GroupNodeChangedListener groupNodeChangedListener;
    private final AddressNodeChangedListener addressNodeChangedListener;
    private final ZKClient client;

    public ZKObserver(String zkAddress) {
        this.subjectNodeChangedListener = new SubjectNodeChangedListener();
        this.groupNodeChangedListener = new GroupNodeChangedListener();
        this.addressNodeChangedListener = new AddressNodeChangedListener();
        client = ZKClientCache.get(zkAddress);
    }

    public void start() {
        try {
            int start = 0;
            listenChildren(CONSUMER_SUBJECT_ROOT, start, subjectNodeChangedListener, groupNodeChangedListener, addressNodeChangedListener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void listenChildren(String parentPath, int layer, NodeListener... listener) throws Exception {
        List<String> children = client.listenChildrenPath(parentPath, listener[layer], true);
        for (String child : children) {
            if (isAddress(layer)) {
                String[] addressParts = splitPath(child);
                if (addressParts.length == zkPathParts) {
                    NewConsumerEvent event = new NewConsumerEvent(addressParts[prefixIndex],
                            addressParts[consumerGroupIndex],
                            addressParts[consumerAddressIndex]);
                    eventBus.post(event);
                }
            } else {
                if (isGroup(layer)) {
                    String[] addressParts = splitPath(child);
                    if (addressParts.length == zkGroupPathParts) {
                        NewGroupEvent event = new NewGroupEvent(addressParts[prefixIndex],
                                addressParts[consumerGroupIndex]);
                        eventBus.post(event);
                    }
                }
                listenChildren(child, layer + 1, listener);
            }

        }
    }

    private boolean isGroup(int layer) {
        return layer == 1;
    }

    private boolean isAddress(int layer) {
        return layer == 2;
    }

    private String[] splitPath(String path) {
        String addressPath = path.substring(CONSUMER_SUBJECT_ROOT.length());
        return StringUtils.split(addressPath, "/");
    }

    public void onConsumerChanged(Object listener) {
        eventBus.register(listener);
    }

    public void removeListener(Object listener) {
        eventBus.unregister(listener);
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    private class SubjectNodeChangedListener implements NodeListener {
        @Override
        public void nodeChanged(ZKClient sender, ChangedEvent e) throws Exception {
            if (e.getType().equals(ChangedEvent.Type.CHILD_ADDED)) {
                String subject = e.getPath();
                sender.listenChildrenPath(subject, groupNodeChangedListener, false);
            }
        }
    }

    private class GroupNodeChangedListener implements NodeListener {
        @Override
        public void nodeChanged(ZKClient sender, ChangedEvent e) throws Exception {
            if (e.getType().equals(ChangedEvent.Type.CHILD_ADDED)) {
                String group = e.getPath();
                sender.listenChildrenPath(group, addressNodeChangedListener, false);
            } else if (e.getType().equals(ChangedEvent.Type.CHILD_REMOVED)) {
                String[] addressParts = splitPath(e.getPath());
                if (addressParts.length == zkGroupPathParts) {
                    RemovedGroupEvent event = new RemovedGroupEvent(addressParts[prefixIndex],
                            addressParts[consumerGroupIndex]);
                    eventBus.post(event);
                }
            }
        }
    }

    private class AddressNodeChangedListener implements NodeListener {
        @Override
        public void nodeChanged(ZKClient sender, ChangedEvent e) throws Exception {
            logger.info("node changed------- {}", e.getType().name());
            String address = e.getPath();
            final String[] addressParts = splitPath(address);
            if (addressParts.length != zkPathParts) return;
            if (e.getType().equals(ChangedEvent.Type.CHILD_ADDED)) {
                consumerLostLogger.info("Consumer online: {}", e.getPath());

                NewConsumerEvent event = new NewConsumerEvent(addressParts[prefixIndex],
                        addressParts[consumerGroupIndex],
                        addressParts[consumerAddressIndex]);
                eventBus.post(event);
            } else if (e.getType().equals(ChangedEvent.Type.CHILD_REMOVED)) {
                consumerLostLogger.info("Consumer offline: {}", e.getPath());

                RemovedConsumerEvent event = new RemovedConsumerEvent(addressParts[prefixIndex],
                        addressParts[consumerGroupIndex],
                        addressParts[consumerAddressIndex]);
                eventBus.post(event);
            }
        }
    }
}
