package org.apereo.cas.web.flow;

import org.apereo.cas.api.PasswordlessUserAccount;
import org.apereo.cas.authentication.mfa.TestMultifactorAuthenticationProvider;
import org.apereo.cas.web.support.WebUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.test.MockFlowExecutionContext;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link DetermineMultifactorPasswordlessAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Import({
    ThymeleafAutoConfiguration.class,
    BaseWebflowConfigurerTests.SharedTestConfiguration.class
})
@Tag("Webflow")
@TestPropertySource(properties = {
    "cas.authn.passwordless.accounts.simple.casuser=casuser@example.org",
    "cas.authn.passwordless.multifactorAuthenticationActivated=true",
    "cas.authn.mfa.globalProviderId=" + TestMultifactorAuthenticationProvider.ID
})
public class DetermineMultifactorPasswordlessAuthenticationActionTests extends BasePasswordlessAuthenticationActionTests {
    @Autowired
    @Qualifier("determineMultifactorPasswordlessAuthenticationAction")
    private Action determineMultifactorPasswordlessAuthenticationAction;
    
    @Test
    public void verifyAction() throws Exception {
        TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);

        val exec = new MockFlowExecutionContext(new MockFlowSession(new Flow(CasWebflowConfigurer.FLOW_ID_LOGIN)));
        val context = new MockRequestContext(exec);
        val request = new MockHttpServletRequest();
        val account = new PasswordlessUserAccount("casuser", "email", "phone", "casuser", Map.of(), false);
        WebUtils.putPasswordlessAuthenticationAccount(context, account);
        
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        assertEquals(TestMultifactorAuthenticationProvider.ID, determineMultifactorPasswordlessAuthenticationAction.execute(context).getId());

    }
}
