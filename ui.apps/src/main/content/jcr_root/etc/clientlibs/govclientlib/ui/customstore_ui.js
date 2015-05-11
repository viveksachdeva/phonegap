/*
if (CQ_Analytics.CustomStoreMgr ) {

    // HTML template
		
	CQ_Analytics.CustomStoreMgr.template =
        "<input class='customstore-input' type='text' id='customstore-input-dept' name='gov-dept' value='%key%'>";

    CQ_Analytics.CustomStoreMgr.templateRenderer = function(key) {
        console.log("CustomStoreMgr.templateRenderer | Starting.........");

         var template = CQ_Analytics.CustomStoreMgr.template;
         return template.replace(/%key%/g, key);
     }
 
    CQ_Analytics.CustomStoreMgr.renderer = function(store, divId) {

        // first load data
        // CQ_Analytics.CustomStoreMgr.loadData();
        console.log("CustomStoreMgr.renderer | Starting.........");

        $CQ("#" + divId).children().remove();

        var templateRenderer = CQ_Analytics.CustomStoreMgr.templateRenderer;

        // Set title
        $CQ("#" + divId).addClass("cq-cc-customstore");
 
        var data = this.getJSON();

        if (data) {
            for (var i in data) {
                if (typeof data[i] === 'object') {
                    $CQ("#" + divId).append(templateRenderer(data[i].key));
                }
            }
        }
		
		$CQ(".customstore-input").change(function(){
            console.log("CustomStoreMgr.renderer | customstore-input value changed .........");

			var value = "";
			var key = $CQ(this).attr("name");

			var newValue = $CQ(this).attr("value");
			CQ_Analytics.CustomStoreMgr.setTraitValue(key,newValue);
            CQ_Analytics.CustomStoreMgr.fireEvent("update");
			CQ_Analytics.ProfileDataMgr.fireEvent("update");
		}); 
    }
	
	
 
    CQ_Analytics.CustomStoreMgr.setTraitValue = function(trait, newValue) {
        console.log("CustomStoreMgr.setTraitValue | key ={0} and value = {1} .........", trait, newValue);

        var data = CQ_Analytics.CustomStoreMgr.data;
        if (data) {
            data[trait] = newValue;
        }
    };
 
}
*/