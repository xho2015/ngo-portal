var AppMain = $.ngoModule(function() {

	var ready = function() {
		initUI();		
	};

	var error = function() {
		alert("load script for main AppModule error");
	};

	function init() {
		var depends;
		$.ajax({
			type : "GET",
			async : false,
			cache : false,
			dataType : "json",
			url : "/json/bom/list",
			data : {
				token : "ses001",
				module : "main"
			},
			success : function(json) {
				depends = json;
			},
			error : function(error) {
				alert(error.status);
			}
		});
		AppBootstrap.loadBulkScript(depends, ready, error);
	}
	;

	function initUI() {
		AppMainUI.init();
	};

	return {
		init : init
	};

}());