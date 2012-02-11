/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.object;

import are4.us.mongo.exception.DuplicateServerConfigException;
import java.util.HashMap;

/**
 *
 * @author raygao2000
 */
public class MongoServerConfigFactory {

    // keep track many MongoServers has been already defined.
    public static HashMap<String, MongoServerConfig> serverlist = new HashMap();

    public static MongoServerConfig upsertMongoServerConfig(HashMap attributes) {
        if (!MongoServerConfigFactory.serverlist.isEmpty()) {
            // does it already exist?
            if (MongoServerConfigFactory.serverlist.containsKey(attributes.get(MongoServerConfig.ALIAS).toString())) {
                return (MongoServerConfig) MongoServerConfigFactory.serverlist.get(attributes.get(MongoServerConfig.ALIAS).toString());
            } else {
                MongoServerConfig m = new MongoServerConfig(attributes);
                MongoServerConfigFactory.serverlist.put(m.getAlias(), m);
                return m;
            }
        } else {
            //empty 
            MongoServerConfig m = new MongoServerConfig(attributes);
            MongoServerConfigFactory.serverlist.put(m.getAlias(), m);
            return m;
        }
    }

    // create a new mongo server with just the name
    public static MongoServerConfig newMongoServerConfig(String alias) {
        if (MongoServerConfigFactory.getMongoServerConfig(alias) != null) {
            throw new DuplicateServerConfigException("Duplicate database exists.");
        }
        HashMap m = new HashMap();
        m.put(MongoServerConfig.ALIAS, alias);

        return upsertMongoServerConfig(m);
    }

    //return an existing mongoserver
    public static MongoServerConfig getMongoServerConfig(String alias) {
        return MongoServerConfigFactory.serverlist.get(alias);
    }

    public static int getServerConfigList() {
        return MongoServerConfigFactory.serverlist.size();
    }
    // prevent instantiation of the factory class.
    private static MongoServerConfigFactory instance = null;

}