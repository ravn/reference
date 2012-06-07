package org.bitrepository.protocol.security.exception;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SecurityExceptionTest extends ExtendedTestCase {
    
    @Test(groups = { "regressiontest" })
    public void testSecurityException() throws Exception {
        addDescription("Test the instantiation of the exception");
        addStep("Setup", "");
        String errMsg = "TEST-ERROR";
        String causeMsg = "CAUSE-EXCEPTION";
        
        addStep("Try to throw such an exception", "Should be able to be caught and validated");
        try {
            throw new SecurityException(errMsg);
        } catch(Exception e) {
            Assert.assertTrue(e instanceof SecurityException);
            Assert.assertEquals(e.getMessage(), errMsg);
            Assert.assertNull(e.getCause());
        }
        
        addStep("Throw the exception with an embedded exception", "The embedded exception should be the same.");
        try {
            throw new SecurityException(errMsg, new IllegalArgumentException(causeMsg));
        } catch(Exception e) {
            Assert.assertTrue(e instanceof SecurityException);
            Assert.assertEquals(e.getMessage(), errMsg);
            Assert.assertNotNull(e.getCause());
            Assert.assertTrue(e.getCause() instanceof IllegalArgumentException);
            Assert.assertEquals(e.getCause().getMessage(), causeMsg);
        }
    }
    
}
