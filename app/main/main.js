var AppMainModule = ngolibrary (function() {
	
	var ready = function() {
		AppModuleDemo1.init();
	};

	var error = function() {
		alert("load script for mainAppModule error");		
	};
	
	function init() {
		var dependency = {
			"resource" : [
					{
						"tid" : "1",
						"load" : "cdn|ngo",
						"name" : "threeminjs",
						"url" : "https://cdnjs.cloudflare.com/ajax/libs/three.js/87/three.min.js"
					}, {
						"tid" : "1",
						"load" : "ngo",
						"name" : "datgui",
						"url" : "/app/lib/dat.gui.min.ngjs"
					}, {
						"tid" : "1",
						"load" : "ngo",
						"name" : "common1",
						"url" : "/app/lib/common.min.ngjs"
					}, {
						"tid" : "1",
						"load" : "ngo",
						"name" : "demo1",
						"url" : "demo1.min.ngjs"
					} ]
		};

		AppBootstrap.loadScripts(dependency, ready, error);
	};

	return {
		init : init
	};

}());