// Create the session store called "customstore"
if (!CQ_Analytics.CustomStoreMgr) {

    CQ_Analytics.CustomStoreMgr = new CQ_Analytics.SessionStore();

    CQ_Analytics.CustomStoreMgr.STOREKEY = "CUSTOMSTORE";
    CQ_Analytics.CustomStoreMgr.STORENAME = "customstore";
    CQ_Analytics.CustomStoreMgr.data={};

    if (CQ_Analytics.ClientContextMgr){
        CQ_Analytics.ClientContextMgr.register(CQ_Analytics.CustomStoreMgr)
    }

    CQ_Analytics.CustomStoreMgr.init = function() {
        console.log("CQ_Analytics.CustomStoreMgr.init() method called. ");
        CQ_Analytics.CustomStoreMgr.data={};
    }


    CQ_Analytics.ClientContextUtils.onStoreRegistered("customstore", listen);

    //listen for the store's update event
    function listen(){
        console.log(" Listen method called. On store registration event. ");
        var customstore = CQ_Analytics.ClientContextMgr.getRegisteredStore("customstore");
        console.log(" Initialize value = ", customstore.getProperty("department"));
        customstore.addListener("update",getParameterValue(customstore));
    }

    function getParameterValue(object) {
        console.log(" getParameterValue() method initiated on update event ", object.getProperty("department"));

    }

    CQ_Analytics.CustomStoreMgr.getValue = function(service) {
        console.log("CustomStoreMgr.getValue for key ", service); 

        if (CQ_Analytics.CustomStoreMgr.data) {
            if (CQ_Analytics.CustomStoreMgr.data[service]) {
                console.log("CustomStoreMgr.getValue value = ", CQ_Analytics.CustomStoreMgr.data[service]); 
                return CQ_Analytics.CustomStoreMgr.data[service];
            }
        }
        return "";
    }
}