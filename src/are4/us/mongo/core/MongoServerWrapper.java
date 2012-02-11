/*
 * Raymond Gao, Are4Us technologies, http://are4.us
 * Copyright 2012
 */
package are4.us.mongo.core;

import are4.us.mongo.object.MongoServerConfigFactory;
import are4.us.mongo.object.MongoServerConfig;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import are4.us.mongo.exception.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author raygao2000
 */
public class MongoServerWrapper {

    private Mongo mongo;
    public HashMap<String, DBObject> dbc_indexes = new HashMap<String, DBObject> ();
    public HashMap<String, DBCollection> dbcs = new HashMap<String, DBCollection> () ;


    public MongoServerWrapper(MongoServerConfig mconfig) throws java.net.UnknownHostException, AuthenticationException {
        String server = mconfig.getServer();
        int port = mconfig.getPort();
        this.mongo = new Mongo(server, port);

        //log in if username and password are provided
        if ((mconfig.getUsername() != null) & (mconfig.getPassword() != null)
                && !mconfig.getUsername().isEmpty() && !mconfig.getPassword().isEmpty()) {
            if (!this.admin_auth(mconfig.getUsername(), mconfig.getPassword().toCharArray())) {
                throw new AuthenticationException("bad username/password");
            }
        }
    }

    /*
     * constructor param Mongo
     */
    public MongoServerWrapper(Mongo m) {
        this.mongo = m;
    }

    /*
     * constructor with localhost and no username/password
     */
    public MongoServerWrapper() throws java.net.UnknownHostException {
        MongoServerConfig msc = MongoServerConfigFactory.newMongoServerConfig("localhost");
        msc.setServer("localhost");
        msc.setPort(27017);
        mongo = new Mongo(msc.getServer(), msc.getPort());
    }

    /*
     * Authenticate @param username, password
     */
    public boolean admin_auth(String username, char[] password) {
        DB adminDB = getMongo().getDB("admin");
        return adminDB.authenticate(username, password);
    }

    /*
     * return a hashmap of database with this mongo server connection
     */
    public HashMap<String, DB> getDBs() {
        List<String> db_names = getMongo().getDatabaseNames();
        HashMap<String, DB> dbs = new HashMap<String, DB>();
        for (String db_name : db_names) {
            dbs.put(db_name, getMongo().getDB(db_name));
        }
        return dbs;
    }

    /*
     * return a hashmap of collectio associated with a DB.
     */
    public HashMap<String, DBCollection> getCollections(DB db) {
        Set<String> collection_names = db.getCollectionNames();
        HashMap<String, DBCollection> dbcollection = new HashMap<String, DBCollection>();
        for (String collection_name : collection_names) {
            dbcollection.put(collection_name, db.getCollection(collection_name));
            //prevent HashMap overwrite of data with the same name, hence put it in an extended name space
            dbcs.put(db.getName() + "/" + collection_name.toString(), db.getCollection(collection_name));
        }
        return dbcollection;
    }

    /*
     * return a hashmap of collectio associated with a DB.
     */
    public HashMap<String, DBObject> getCollectionIndexes(DBCollection coll) {
        List<DBObject> indexes = coll.getIndexInfo();
        HashMap<String, DBObject> collection_indexes = new HashMap<String, DBObject>();
        for (DBObject index : indexes) {
            collection_indexes.put(index.toString(), index);
            //prevent HashMap overwrite of data with the same name, hence put it in an extended name space
            dbc_indexes.put(coll.getDB().getName() + "/" + coll.getName() + "/" + index.toString(), index);
        }
        return collection_indexes;
    }

    public float getDBSize (DB db) {
        float dbsize = (Float) Float.parseFloat(db.getStats().get("storageSize").toString());        
        return dbsize;        
    }   
    
    public float getCollectionSize (DBCollection dbcoll) {
        float dbsize = (Float) Float.parseFloat(dbcoll.getStats().get("storageSize").toString());                
        return dbsize;        
    }   
    
    public float calculateCollectionToDBRatio (DBCollection dbcoll) {
        float ratio = getCollectionSize(dbcoll) / getDBSize(dbcoll.getDB());
        return ratio;
    }
    
    //get Server Status
    public Map getServerStats(DB db) {
        Map stats = db.command("serverStatus").toMap();
        //TODO This should be replaced with a logger.....
        System.out.println("******Memory status: " + stats.get("mem"));
        return stats;
        //memory -> stats.get("mem")
        // virtual memory => stats.get("mem").get("virtual")
        // resident => stats.get("mem").get("resident")
        // ....
        //network -> stats.get("network")
        //connection -> stats.get("connection")
        
        //same as > db.runCommand("serverStatus") 
        // or db.serverStatus()
        //see http://api.mongodb.org/java/current/com/mongodb/DB.html
        //and http://www.mongodb.org/display/DOCS/Checking+Server+Memory+Usage
        
        //particularly, http://www.mongodb.org/display/DOCS/serverStatus+Command
        //  bit -> 32 or 64
        //  resident --> megabyte
        //  virtual  --> megabyte
        //  support  --> bool for supporting extended memory info
        //  mapped   --> Megabytes of data mapped by the database
        
    }    
    
    public void printDetails() {
        try {
            System.out.println(getMongo());

            HashMap<String, DB> dbs = getDBs();
            for (DB db : dbs.values()) {
                System.out.println("<" + db.getName() + ">");
                System.out.println("\n");
                System.out.println("[" + showCollectionName(db) + "]");

                System.out.println("--------------------------------");
                for (DBCollection dbcollection : getCollections(db).values()) {
                    System.out.println(" *** " + db.getName() + "/" + dbcollection + " has " + dbcollection.getCount() + " documents.");
                    System.out.println("   " + dbcollection.getStats().toString() + "   ");
                    System.out.println("================================");

                    System.out.println("########## Index ##############");
                    HashMap<String, DBObject> indexes = this.getCollectionIndexes(dbcollection);

                    for (DBObject index : indexes.values()) {
                        System.out.println(index);
                    }

                    //print documents in a collection
                    DBCursor cur = dbcollection.find(); 
                    System.out.println("########## Documents ##########");
                    while (cur.hasNext()) {
                        System.out.println("   " + cur.next() + "   ");
                    }
                    System.out.println("================================");
                System.out.println("-------------------------------");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public String showCollectionName(DB db) {
        String collection_names = "";
        
        for (DBCollection collection : getCollections(db).values()) {
            collection_names += collection + ", ";
            //TODO remove the last ","
        }

        return collection_names;
    }

    /**
     * @return the mongo
     */
    public Mongo getMongo() {
        return mongo;
    }

}
