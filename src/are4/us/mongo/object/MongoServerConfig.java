/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.object;

import java.util.HashMap;

/**
 *
 * @author raygao2000
 */
public class MongoServerConfig {
    
    // attribute of the MongoServerConfig
    private String bServerDisabled;
    private String alias;
    private String server;
    private int port;
    private String username;
    private String password;
    private boolean useReplicaSet;
    private String replicaServerSet;
    private String replicaSetName;
    private boolean useSSH;
    
    //constant of attributes
    public static final String ALIAS = "alias";
    public static final String SERVER = "server";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USE_REPLICA_SET = "use_replica_set";
    public static final String REPLICA_SERVER_SET = "replica_server_set";
    public static final String REPLICA_SET_NAME = "replica_set_name";
    public static final String USE_SSH = "use_ssh";
    public static final String bSERVER_DISABLED = "bserver_disabled";

    //Constructor
    public MongoServerConfig(HashMap attributes) {
        
        if (attributes.containsKey(ALIAS)) {
            setAlias(attributes.get(ALIAS).toString());
        }
        if (attributes.containsKey(SERVER)) {
            setServer(attributes.get(SERVER).toString());
        }
        if (attributes.containsKey(PORT)) {
            setPort(Integer.parseInt(attributes.get(PORT).toString()));
        }
        if (attributes.containsKey(USERNAME)) {
            setUsername(attributes.get(USERNAME).toString());
        }
        if (attributes.containsKey(PASSWORD)) {
            setPassword(attributes.get(PASSWORD).toString());
        }
        if (attributes.containsKey(USE_REPLICA_SET)) {
            setUseReplicaSet(new Boolean(attributes.get(USE_REPLICA_SET).toString()));

            if (attributes.containsKey(REPLICA_SERVER_SET)) {
                setReplicaServerSet(attributes.get(REPLICA_SERVER_SET).toString());
            }
            if (attributes.containsKey(REPLICA_SET_NAME)) {
                setReplicaSetName(attributes.get(REPLICA_SET_NAME).toString());
            }
            if (attributes.containsKey(USE_SSH)) {
                setUseSSH(new Boolean(attributes.get(USE_SSH).toString()));
            }
        }
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the useReplicaSet
     */
    public boolean isUseReplicaSet() {
        return useReplicaSet;
    }

    /**
     * @param useReplicaSet the useReplicaSet to set
     */
    public void setUseReplicaSet(boolean useReplicaSet) {
        this.useReplicaSet = useReplicaSet;
    }

    /**
     * @return the replicaServerSet
     */
    public String getReplicaServerSet() {
        return replicaServerSet;
    }

    /**
     * @param replicaServerSet the replicaServerSet to set
     */
    public void setReplicaServerSet(String replicaServerSet) {
        this.replicaServerSet = replicaServerSet;
    }

    /**
     * @return the replicaSetName
     */
    public String getReplicaSetName() {
        return replicaSetName;
    }

    /**
     * @param replicaSetName the replicaSetName to set
     */
    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    /**
     * @return the useSSH
     */
    public boolean isUseSSH() {
        return useSSH;
    }

    /**
     * @param useSSH the useSSH to set
     */
    public void setUseSSH(boolean useSSH) {
        this.useSSH = useSSH;
    }

    /**
     * @return the bServerDisabled
     */
    public String getbServerDisabled() {
        return bServerDisabled;
    }

    /**
     * @param bServerDisabled the bServerDisabled to set
     */
    public void setbServerDisabled(String bServerDisabled) {
        this.bServerDisabled = bServerDisabled;
    }
}
