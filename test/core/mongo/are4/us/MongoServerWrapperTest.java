/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.mongo.are4.us;

import are4.us.mongo.core.MongoServerWrapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.util.HashMap;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author raygao2000
 */
public class MongoServerWrapperTest {

    public MongoServerWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testOne() throws java.net.UnknownHostException {
        MongoServerWrapper mcls = new MongoServerWrapper();
        mcls.admin_auth("<username>", "<password>".toCharArray());
        mcls.printDetails();
        //MongoServerConfig newone = MongoServerConfigFactory.newMongoServerConfig("old");
    }
}
