var MAINAPP = $.ngoModule(function() {

	var resource = new AppCommon.Map();
	var lib = new AppCommon.Map();
	var DEBUGINFO = new AppCommon.Map();
	
	var ready = function() {
		var temp = new AppCommon.Map();
		temp.putJson(lib.payload, "name", "ver");
		DEBUGINFO.put("app.main.panel-ui-min-ngjs",temp.get("app.main.panel-ui-min-ngjs"));
		MAIMUI.init();
	};

	var fail = function(url) {
		alert("load script ("+url+") failed, please refresh and try again");
	};

	function init() {
		CACHE.refresh();
		
		lib = JSONG.require("bom.module.lib1");
		LIBRARY.loadRetry(lib.payload, ready, fail, resource);
	};

	return {
		init : init,
		resource : resource,
		lib : lib,
		DEBUGINFO : DEBUGINFO 
	};
}());