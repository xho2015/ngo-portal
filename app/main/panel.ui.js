/**
 * NGO main panel module
 */
var PANEL = (function(my) {
	my.name = 'NGO Head navigator';
	
	//background
	my.bgcontainer = $('#background_screen');
	my.bgcanvas = $('<canvas>')
		.css({position : 'absolute',left : 0,top : 0,width : '100%',	height : '100%'})
		.attr({	id : 'backgound_canvas', width : my.bgcontainer.width(), height : my.bgcontainer.height()})
		.prependTo(my.bgcontainer);

	my.bgeffects = function() {
		// enable star field effect
		if (AppSettings.bganimate) {
			$(my.bgcontainer).starfield({framerate:AppSettings.framerate, speedX:2, starDensity:0.10,	mouseScale:0.01, background:'#000007',	seedMovement:true
			}, "backgound_canvas");
		};
	};
	
	//panel & its id
	my.panelId = 'panel_canvas';
	my.panel = $('<canvas>').addClass('panel_canvas').attr({id : my.panelId}).appendTo($('#ngcontainer'));

	//screen status
    my.screenStatus = {	isFull : false,	orientation : 0	};
    my.panelDimension = [];
    var matrix = [ [ 1600, 900 ], [ 1440, 810 ], [ 1280, 720 ],
		[ 1120, 630 ], [ 960, 540 ], [ 800, 450 ], [ 640, 360 ],
		[ 600, 340 ], [ 340, 240 ] ];
    
    //full screen checker
    my.isFullscreen = function () {
		return document.fullscreenElement || document.msFullscreenElement
				|| document.mozFullScreenElement
				|| document.webkitFullscreenElement || false;
	};
	
	my.fullScreen = function () {
		var docElm = document.documentElement;
		if (docElm.requestFullscreen) {	docElm.requestFullscreen();
		} else if (docElm.msRequestFullscreen) {docElm = document.body; docElm.msRequestFullscreen();
		} else if (docElm.mozRequestFullScreen) {docElm.mozRequestFullScreen();
		} else if (docElm.webkitRequestFullScreen) {docElm.webkitRequestFullScreen();
		} else if (docElm.webkitEnterFullscreen) {docElm.webkitEnterFullscreen();
		}
	};

	my.exitFullScreen = function () {
		if (document.exitFullscreen) {document.exitFullscreen();
		} else if (document.msExitFullscreen) {document.msExitFullscreen();
		} else if (document.mozCancelFullScreen) {document.mozCancelFullScreen();
		} else if (document.webkitCancelFullScreen) {document.webkitCancelFullScreen();
		} else if (document.webkitExitFullScreen) {document.webkitExitFullScreen();
		}
	};
    
    my.resize = function() {   	
    	//check screen full status
		if (!my.isFullscreen()) {my.screenStatus.isFull = false;
		} else {my.screenStatus.isFull = true;}
			
		// match resolution matrix by current window dimension
		var winWidth = window.innerWidth;
		var winHeight = window.innerHeight;
		
		for (m in matrix) {
			if (winWidth > matrix[m][0] && winHeight > matrix[m][1]) {
				my.panelDimension = [];
				my.panelDimension.push(matrix[m][0], matrix[m][1]);
				backgroundResize(matrix[m][0], matrix[m][1]);   	
				panelResize(matrix[m][0], matrix[m][1]);
				console.log("panel dimension change, width="+matrix[m][0]+", height="+matrix[m][1]);
				break;
			}
		}
	};
    
    function backgroundResize(w, h) {
    	$('#background_screen').width(window.innerWidth); $('#background_screen').height(window.innerHeight);
    	var bgscreen = document.getElementById('background_screen');
    	bgscreen.width = w; bgscreen.height = h;
    	
		$('#backgound_canvas').width(window.innerWidth); $('#backgound_canvas').height(window.innerHeight);
		var bgcanvas = document.getElementById('backgound_canvas');
		bgcanvas.width = w; bgcanvas.height = h;
		
		my.bgeffects();
	}
    
    function panelResize(w, h) {
		$('#'+my.panelId).width(w); $('#'+my.panelId).height(h);
		var canvas = document.getElementById(my.panelId);
		canvas.width = w; canvas.height = h;
		
		var hw = w;	var hh = Math.abs(h / 10);
		hh = hh < 40 ? 40 : hh;
		var sw = w;	var sh = h - hh;

		PANEL.header.resize(0, 0, hw, hh);
		PANEL.screen.resize(0, hh, sw, sh);
	}
	
	return my;
}(PANEL || {}));

/**
 * panel header sub-module
 */
PANEL.header = (function() {
	var my = {};
	my.container = new createjs.Container();
	my.square = new createjs.Shape();
	my.container.addChild(my.square);
	my.Label1 = new createjs.Text("", "17px Arial", "#eeffff");
	my.Label1.shadow = new createjs.Shadow("#090909", 1, 1, 6);
	my.container.addChild(my.Label1);
	my.container.addChild(my.Label2);
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 0.8;

	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.x = 0; my.square.y = 0;
		my.square.graphics.beginFill("#1999d8").drawRect(x, y, w, h);
		my.Label1.x = 10; my.Label1.y = (h - 16) / 2;
		my.Label1.text="NGO KidsMath " + PANEL.panelDimension[0]+"X"+PANEL.panelDimension[1];
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
		my.stage.alpha = 0.9;
		my.stage.update();
	};
	
	return my;
}());

/**
 * panel screen sub-module
 */
PANEL.screen = (function() {
	var my = {};
	my.container = new createjs.Container();
	my.square = new createjs.Shape();
	my.container.addChild(my.square);
	my.Label1 = new createjs.Text("info", "16px Arial", "#F0FFF0");
	my.container.addChild(my.Label1);
	my.enlarge = new createjs.Bitmap("/app/res/enlarge_screen.png");
	my.enlarge.shadow = new createjs.Shadow("#090909", 3, 3, 6);
	my.container.addChild(my.enlarge);
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 0.1;
	
	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.graphics.beginFill("#1F1F1F").drawRect(x, y, w, h);
		
		my.Label1.x = x; my.Label1.y = y+10;
		my.Label1.text = "Click下面图标切换到全屏模式！";
		my.enlarge.x = (w - my.enlarge.image.width) / 2;
		my.enlarge.y = (h - my.enlarge.image.height) / 2;
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
		my.stage.update();
	};

	my.enlarge.on("click", function(evt) {
		if (PANEL.isFullscreen())
			PANEL.exitFullScreen();
		else
			PANEL.fullScreen();
	});
	
	return my;
}());

var MAIMUI = (function() {

	var resizeEvents = [];
	function registeResize(resize) {
		resizeEvents.push(resize);
		console.log("resize Events registed");
	}

	/*
	 * the only sensor of window resize event
	 */
	function onWindowResize() {
		for (var i = 0; i < resizeEvents.length; i++) {
			var resize = resizeEvents[i];
			resize();
		}
	}

	function onResize() {
		PANEL.resize();
	}
	
	/*
	 * the only sensor of browser orientation change
	 */
	function onOrientationChange() {
		if (window.orientation === 180 || window.orientation === 0) {
			PANEL.screenStatus.orientation = 1;
			alert('当前为【竖屏】，请切换为【横屏】以获得更好的显示效果！');
		}
		if (window.orientation === 90 || window.orientation === -90) {
			PANEL.screenStatus.orientation = 0;
		}
	}


	/*
	 * init will be invoked when this module loaded
	 */
	function init() {
		window.addEventListener('resize', onWindowResize, false);
		window.addEventListener("onorientationchange" in window ? "orientationchange" : "resize", onOrientationChange, false);
		registeResize(onResize);
		onWindowResize();
	}

	return {
		init : init
	};
})();


