import object.mongo.are4.us.MongoServerConfigFactory
import object.mongo.are4.us.MongoServerConfig
import exception.mongo.are4.us.DuplicateServerConfigException

scenario "Create a new MongoServerConfig", {
 given "None MongoServerConfig Exists",{
 }
 when "creates a new MongoServerConfig",{
    ms = MongoServerConfigFactory.newMongoServerConfig("empty")
 }
 then "a mongo server should be returned.",{
    ms.shouldNotBe null
 }
}

scenario "Create a duplicate named MongoServerConfig", {
 given "One MongoServerConfig already exists",{
    msold = MongoServerConfigFactory.newMongoServerConfig("existing")
}
 when "creates a new MongoServer with duplicate name",{
    dupFab = {
        msnew = MongoServerConfigFactory.newMongoServerConfig("existing")
    }   
 }
 then "an exception should raise",{
    ensureThrows( DuplicateServerConfigException ) {
            dubFab
    }
 }
}
