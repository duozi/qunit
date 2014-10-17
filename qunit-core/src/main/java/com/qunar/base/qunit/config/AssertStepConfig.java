package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.ConfigElement;
import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.AssertStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.util.CloneUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/*
<assert>
    <body>{'ret':true}</body>
</assert>
    equals
<assert>
{'ret':true}
</assert>
 */
@ConfigElement(defaultProperty = AssertStepConfig.BODY_TAG_NAME)
public class AssertStepConfig extends StepConfig {
    public static final String BODY_TAG_NAME = "body";

    @Property(defaultValue = "")
    private String desc;

    @Property
    private String body;

    @Element
    List<KeyValueStore> params;

    @Override
    public StepCommand createCommand() {
        appendBodyIfExists();
        return new AssertStepCommand(CloneUtil.cloneKeyValueStore(params), desc);
    }

    private void appendBodyIfExists() {
        if (StringUtils.isNotBlank(body)) {
            params.add(new KeyValueStore(BODY_TAG_NAME, body));
        }
    }


}
