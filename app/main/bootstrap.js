var mainBootstrap = (function() {

	var OkCallback = function() {
		mainAppModule.init();
	};

	var FailCallback = function() {
		alert("load script error, retry="+retryUrl);
		if (retryUrl) {
			var tempUrl = retryUrl;
			retryUrl = null;
			loadScript1(retryId+"-retry", tempUrl, OkCallback, FailCallback);
		}	
	};
	
	var retryUrl = null;
	var retryId = null;

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

	function loadScript(sid, url) {
		var array = url.split('|');
		if (array.length > 1){
			loadScript1(sid, array[0], OkCallback, FailCallback);
			retryUrl = array[1]; retryId = sid;
		} else {
			loadScript1(sid, url, OkCallback, FailCallback);	
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
	
	return {
		loadScript : loadScript
	};

})();

mainBootstrap.loadScript("mainlibs", "main.min.ngjs");
