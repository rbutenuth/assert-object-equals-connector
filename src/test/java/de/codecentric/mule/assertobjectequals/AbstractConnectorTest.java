package de.codecentric.mule.assertobjectequals;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.el.ExpressionLanguageExtension;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.MuleRegistry;
import org.mule.el.mvel.MVELExpressionLanguage;
import org.mule.expression.DefaultExpressionManager;

public class AbstractConnectorTest {
    protected AssertObjectEqualsConnector aoec;

    @Before
    public void before() throws InitialisationException {
        aoec = new AssertObjectEqualsConnector();
        aoec.setMuleContext(createMuleContext());
    }

    @After
    public void after() {
        aoec = null;
    }

    protected MuleEvent createEvent(Object payload) {
        SimpleMock<MuleEvent> eventMock = new SimpleMock<>(MuleEvent.class);
        SimpleMock<MuleMessage> messageMock = new SimpleMock<>(MuleMessage.class);
        eventMock.storeResult(messageMock.getMockObject(), "getMessage");
        messageMock.storeResult(payload, "getPayload");
        return eventMock.getMockObject();
    }

    protected MuleContext createMuleContext() throws InitialisationException {
        SimpleMock<MuleContext> contextMock = new SimpleMock<>(MuleContext.class);
        contextMock.storeResult(createRegistry(), "getRegistry");
        contextMock.storeResult(createConfiguration(), "getConfiguration");
        contextMock.storeResult(createExpressionManager(contextMock.getMockObject()), "getExpressionManager");
        return contextMock.getMockObject();
    }

    protected MuleRegistry createRegistry() {
        SimpleMock<MuleRegistry> registryMock = new SimpleMock<>(MuleRegistry.class);
        registryMock.storeResult(new ArrayList<Object>(), "lookupObjectsForLifecycle", ExpressionLanguageExtension.class);
        return registryMock.getMockObject();
    }

    protected ExpressionManager createExpressionManager(MuleContext muleContext) throws InitialisationException {
        DefaultExpressionManager manager = new DefaultExpressionManager();
        MVELExpressionLanguage expressionLanguage = new MVELExpressionLanguage(muleContext);
        expressionLanguage.initialise();
        manager.setExpressionLanguage(expressionLanguage);
        return manager;
    }

    protected MuleConfiguration createConfiguration() {
        SimpleMock<MuleConfiguration> configurationMock = new SimpleMock<>(MuleConfiguration.class);
        configurationMock.storeResult("UTF-8", "getDefaultEncoding");
        return configurationMock.getMockObject();
    }
}
