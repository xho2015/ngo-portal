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
		var canvas = $('<canvas id="panel_canvas">').css({
			position : 'absolute',
			left : 0,
			top : 0,
			width : '100%',
			height : '100%'
		}).attr({
			width : $('#panel_screen').width(),
			height : $('#panel_screen').height()
		}).prependTo($('#panel_screen'));

		$('#panel_screen').starfield({
			looprate : 20,
			starDensity : 0.08,
			mouseScale : 0.01,
			background : '#00000F',
			seedMovement : true
		}, "panel_canvas");
		
		AppMainUI.init("panel_canvas");
		
		AppG1M1Demo1.init();
	};

	return {
		init : init
	};

}());