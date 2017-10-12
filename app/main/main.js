var AppMain = $.ngoModule(function() {

	var ready = function() {
		initUI();		
	};

	var error = function() {
		alert("load script for main AppModule error");
	};

	function init() {
		var dependency;
		$.ajax({
			type : "GET",
			async : false,
			cache : false,
			dataType : "json",
			url : "/json/bom/list",
			data : {
				token : "usersessioncode",
				module : "main"
			},
			success : function(json) {
				dependency = json;
			},
			error : function(error) {
				alert(error.status);
			}
		});
		AppBootstrap.loadBulkScript(dependency, ready, error);
	}
	;

	function initUI() {
		AppMainUI.init();
	};

	return {
		init : init
	};

}());