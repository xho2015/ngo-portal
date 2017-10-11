var AppMain = $.ngoModule(function() {

	var ready = function() {
		initUI();
		AppG1M1Demo1.init();	
	};

	var error = function() {
		alert("load script for main AppModule error");
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
		AppBootstrap.loadBulkScript(dependency, ready, error);
	};
	
	function initUI()
	{
		var canvas = $('<canvas id="panel_canvas">')
		.css({position: 'absolute', left: 0, top: 0, width: '100%', height: '100%'})
		.attr({width: $('#panel_screen').width(), height: $('#panel_screen').height()})
		.prependTo($('#panel_screen'));
		
		$('#panel_screen').starfield({
            starDensity: 0.07,
            mouseScale: 0.1,
            seedMovement: true
        });
	};

	return {
		init : init
	};

}());