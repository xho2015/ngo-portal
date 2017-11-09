var MAINAPP = $.ngoModule(function() {

	var ready = function() {
		MAIMUI.init();	
	};

	var fail = function(url) {
		alert("load script ("+url+") failed");
	};

	function init() {
		var depends = LIBRARY.require("lib1");
		LIBRARY.loadRetry(depends.links, ready, fail);
	};

	return {
		init : init
	};
}());