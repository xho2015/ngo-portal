var mainAppModule = (function() {

	function init() {
		var dependency = {
			"resource" : [
					{
						"tid" : "1",
						"load" : "cdn|ngo",
						"name" : "threeminjs",
						"url" : "https://cdnjs.cloudflare.com/ajax/libs/three.js/87/three1.min.js|/app/lib/three.min.ngjs"
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

		var resource = dependency.resource;
		for ( var i in resource) {
			var loads = resource[i].load.split('|');
			var ok = -1;
			for ( var l in loads) {
				if (loads[l] == 'cdn' && ok == -1)
					ok = mainBootstrap.loadScript(resource[i].name,
							resource[i].url);
				if (loads[l] == 'ngo' && ok == -1)
					ok = mainBootstrap.loadScript(resource[i].name,
							resource[i].url);
			}
		}
	}
	;

	return {
		init : init
	};

})();