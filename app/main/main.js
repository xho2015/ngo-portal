var AppMainModule = $.ngoModule(function() {

	var ready = function() {
		AppModuleDemo1.init();
	};

	var error = function() {
		alert("load script for mainAppModule error");
	};

	function init() {
		var dependency;
		$.ajax({type: "GET", async: false, cache: false, dataType: "json",
			url: "/json/bom/list",	
			data: { token: "usersessioncode", module: "main" },
			success: function (json) {
				dependency = json;
			},
			error: function(error) {
				alert(error.status );
			}
		});
		AppBootstrap.loadBunchScript(dependency, ready, error);
	};

	return {
		init : init
	};

}());