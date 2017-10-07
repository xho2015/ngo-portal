function ngolibrary(module) {
	$(function() {
		if (module.init) {
			module.init();
		}
	});
	return module;
};

		
var AppBootstrap = ngolibrary(function() {
	
	function init() {
		loadScript("mainlibs", "main.min.ngjs");
	};

	var OkCallback = function() {
		//mainAppModule.init();
	};
	
	var FailCallback = function() {
		alert("resource loading failed!");
	};

	
	//-----retry & callback
	var retry = {
		Url : "",
		Id : ""
	};
	
	var RetryCallback = function() {
		alert("load script error, retry="+retryUrl);
		if (retryUrl) {
			var tempUrl = retryUrl;
			retryUrl = null;
			loadScript1(retryId, tempUrl, OkCallback, FailCallback);
		}
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

	function loadScript(sid, url, ok, fail) {
		var array = url.split('|');
		if (array.length > 1){
			retry.Url = array[1]; retry.Id = sid + ".retry";
			loadScript1(sid, array[0], ok == null? OkCallback : ok, fail == null ? RetrtyCallback : fail);
		} else {
			loadScript1(sid, url, ok == null? OkCallback : ok, fail == null ? FailCallback : fail);	
		}
	};


	function loadScript1(sid, url, callback, errorCallback) {
		var script = createScript(sid);
		script.src = url;
		script.async = false;

		// Then bind the event to the callback function.
		// There are several events for cross browser compatibility.
		script.onreadystatechange = callback;
		script.onload = callback;
		script.onerror = errorCallback;

		// Fire the loading
		var head = document.getElementsByTagName('head')[0];
		head.appendChild(script);
	};

	
	//-------below for loading whole bunch of scripts
	var bunchState = {
		remains: 0,
		ok : null,
		fail : null
	};
	
	var okBunchCallback = function() {
		bunchState.remains--;
		if (bunchState.remains == 0)
			bunchState.ok();
	};
	
	var errorBunchCallback = function() {
		alert("bunch load script error, state="+bunchState);
		bunchState.fail();
	};
	
	
	function loadScriptBunch(json, ok, error) {
		var resource = json.resource;
		bunchState.ok = ok;
		bunchState.fail = error;
		
		for (var r in resource) {
			bunchState.remains++;
		}

		for (var r in resource) {
			loadScript(resource[r].name, resource[r].url, okBunchCallback, errorBunchCallback);
		}		
	};
	
	return {
		init : init,
		loadScript : loadScript,
		loadScripts: loadScriptBunch
	};

}());
