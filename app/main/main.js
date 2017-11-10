var MAINAPP = $.ngoModule(function() {

	var resource = new AppCommon.Map();
	var lib = new AppCommon.Map();
	var DEBUGINFO = new AppCommon.Map();
	
	var ready = function() {
		var temp = new AppCommon.Map();
		temp.putJson(lib.links, "name", "ver");
		DEBUGINFO.put("app.main.panel-ui-min-ngjs",temp.get("app.main.panel-ui-min-ngjs"));
		MAIMUI.init();
	};

	var fail = function(url) {
		alert("load script ("+url+") failed");
	};

	function init() {
		lib = LIBRARY.require("lib1");
		LIBRARY.loadRetry(lib.links, ready, fail, resource);
	};

	return {
		init : init,
		resource : resource,
		lib : lib,
		DEBUGINFO : DEBUGINFO 
	};
}());