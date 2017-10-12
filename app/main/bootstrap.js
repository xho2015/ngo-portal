$.extend({ngoModule: function(module) {
		$(function() {
			if (module.init) {	module.init();	}
		});
		return module;}
});



var AppBootstrap = $.ngoModule(function() {
	
	function init() {
		loadScript("mainlibs", "main.min.ngjs");
	};

	var ok = function() {
		//do things when script loaded
	};
	
	var fail = function() {
		alert("load resource failed!");
	};
	
	var retry = function() {
		alert("load script error, retry="+retryUrl);
		if (retryInfo.Url) {
			var tempUrl = retryInfo.Url;
			retryInfo.Url = null;
			loadScript1(retryInfo.Id, tempUrl, ok, fail);
		}
	};
	var retryInfo = {
		Url : null,
		Id : null
	};	

	function scriptExist(sid) {
		// validate existence
		var x = document.getElementById(sid);
		if (x)
			return x;
	};

	function createScript(sid) {
		var s = scriptExist(sid);
		if (s)
			return s;

		// Adding the script tag to the head as suggested before
		var head = document.getElementsByTagName('head')[0];
		var script = document.createElement('script');
		script.id = sid;
		script.type = 'text/javascript';
		return script;
	};

	function loadScript(sid, url, cbo, cbf) {
		var array = url.split('|');
		if (array.length > 1){
			retryInfo.Url = array[1]; retryInfo.Id = sid + ".retry";
			loadScript1(sid, array[0], cbo == null? ok : cbo, cbf == null ? fail : cbf);
		} else {
			loadScript1(sid, url, cbo == null? ok : cbo, cbf == null ? fail : cbf);	
		}
	};


	function loadScript1(sid, url, ok, error) {
		var script = createScript(sid);
		
		//TODO: remove this debug mode
		var debugUrl = AppSettings.debug ? url.replace('min.ngjs', 'js') : url;
		script.src = debugUrl;
		script.async = false;

		// Then bind the event to the callback function.
		// There are several events for cross browser compatibility.
		script.onreadystatechange = ok;
		script.onload = ok;
		script.onerror = error;

		// Fire the loading
		var head = document.getElementsByTagName('head')[0];
		head.appendChild(script);
	};

	
	// -------below for loading  Bulk of scripts
	var bulkState = {
		remains: 0,
		ok : null,
		fail : null
	};
	
	var okBulk = function() {
		bulkState.remains--;
		if (bulkState.remains == 0)
			bulkState.ok();
	};
	
	var errorBulk = function() {
		alert("bulk load script error, state="+bulkState);
		bulkState.fail();
	};
	
	function loadBulkScript(json, ok, error) {
		var resource = json.links;
		bulkState.ok = ok;
		bulkState.fail = error;
		bulkState.remains = 0;
		for (var r in resource) {
			bulkState.remains++;
		}
		for (var r in resource) {
			loadScript(resource[r].name, resource[r].url+"?"+resource[r].ver, okBulk, errorBulk);
		}		
	};
	
	return {
		init : init,
		loadScript : loadScript,
		loadBulkScript: loadBulkScript
	};
}());
