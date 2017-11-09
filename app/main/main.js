var MAINAPP = $.ngoModule(function() {

	var resource = new AppCommon.Map();
	
	var ready = function() {
		MAIMUI.init();	
	};

	var fail = function(url) {
		alert("load script ("+url+") failed");
	};

	function init() {
		var depends = LIBRARY.require("lib1");
		LIBRARY.loadRetry(depends.links, ready, fail, resource);
	};

	return {
		init : init,
		resource : resource
	};
}());