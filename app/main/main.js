var AppMainModule = $.ngoModule(function() {

	var ready = function() {
		AppModuleDemo1.init();
	};

	var error = function() {
		alert("load script for mainAppModule error");
	};

	var dependency = {
		"links" : [ {
			"version" : "1",
			"name" : "threeminjs",
			"url" : "/app/lib/three.min.ngjs"
		}, {
			"version" : "1",
			"name" : "datgui",
			"url" : "/app/lib/dat.gui.min.ngjs"
		}, {
			"version" : "1",
			"name" : "common1",
			"url" : "/app/lib/common.min.ngjs"
		}, {
			"version" : "1",
			"name" : "demo1",
			"url" : "demo1.min.ngjs"
		} ]
	};

	function init() {
		$.ajax({type: "GET", async: false, cache: false, dataType: "json",
			url: "/json/bom",	
			data: { token: "CHANGEIT", module: "main" },
			success: function (json) {
				dependency = json;
			}
		});
		AppBootstrap.loadBunchScript(dependency, ready, error);
	};

	return {
		init : init
	};

}());