var MAINAPP = $.ngoModule(function() {

	var resource = new AppCommon.Map();
	var lib = new AppCommon.Map();
	var DEBUGINFO = new AppCommon.Map();
	
	var ready = function() {
		var temp = new AppCommon.Map();
		temp.putJson(lib.links, "name", "ver");
		DEBUGINFO.put("app.main.panel-ui-min-ngjs",temp.get("app.main.panel-ui-min-ngjs"));
		
		var image4 = document.createElement('img');
		image4.src = 'data:image/png;base64,'+resource.get('app.res.km-icons-4-36x36-ngpng');
		
		image4.onload = function () 
		{ 
			alert('image created, w='+image4.width);
			resource.put('image4', image4);
			alert('image loaded, w='+resource.get("image4").width);
			
			MAIMUI.init();	
		} 
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