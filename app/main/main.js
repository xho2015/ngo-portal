var MAINAPP = $.ngoModule(function() {

	var resource = new AppCommon.Map();
	var lib = new AppCommon.Map();
	var DEBUGINFO = new AppCommon.Map();
	
	var ready = function() {
		var temp = TAFFY(lib);
		DEBUGINFO.put("app.main.panel-ui-min-ngjs", temp({name:'app.main.panel-ui-min-ngjs'}).first().ver);
		MAIMUI.init();
	};

	var fail = function(url) {
		alert("load script ("+url+") failed, please refresh and try again");
	};

	function init() {
		CACHE.refresh(); //this is the only place to refresh dump cache
		lib = CACHE.load("bom.module.lib1");
		LIBRARY.loadRetry(lib, ready, fail, resource);
	};

	return {
		init : init,
		resource : resource,
		DEBUGINFO : DEBUGINFO 
	};
}());