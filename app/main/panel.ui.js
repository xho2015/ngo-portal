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

	//background effects
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
    
    //panel dimensions
    my.dimension = {
    	width: 0,  	height: 0, 
    	nwidth:0,  	nheight: 0,
    	swidth: 0, 	sheight: 0,
    	awidth: 0, 	aheight: 0
    };
    
    //screen resolution matrix
    var pmatrix = [[ 1600, 900 ], [ 1440, 810 ], [ 1280, 720 ],
		[ 1120, 630 ], [ 960, 540 ], [ 800, 450 ], [ 640, 360 ],
		[ 600, 340 ], [ 340, 240 ] ];

    //header resolution matrix
    var hmatrix = [[ 1600, 80 ], [ 1440, 80 ], [ 1280, 80 ],
		[ 1120, 60 ], [ 960, 60 ], [ 800, 40 ], [ 640, 40 ],
		[ 600, 40 ], [ 340, 40 ] ];
    
    //resolution matrix
    var amatrix = [[1600, '320,820' ], [ 1440, '320,730' ], [ 1280, '320,640' ],
		[ 1120, '240,570' ], [ 960, '240,480' ], [ 800, '180,410' ], [ 640, '180,320' ],
		[ 600, '180,300' ], [ 340, '0,0' ]];
    
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
		} else if (docElm.webkitEnterFullscreen) {docElm.webkitEnterFullscreen();}
	};

	my.exitFullScreen = function () {
		if (document.exitFullscreen) {document.exitFullscreen();
		} else if (document.msExitFullscreen) {document.msExitFullscreen();
		} else if (document.mozCancelFullScreen) {document.mozCancelFullScreen();
		} else if (document.webkitCancelFullScreen) {document.webkitCancelFullScreen();
		} else if (document.webkitExitFullScreen) {document.webkitExitFullScreen();	}
	};
    
    my.resize = function() { 
    	//check screen full status
		if (!my.isFullscreen()) {my.screenStatus.isFull = false;
		} else {my.screenStatus.isFull = true;}
			
		// match resolution matrix by current window dimension
		var winWidth = window.innerWidth;
		var winHeight = window.innerHeight;
		
		for (m in pmatrix) {
			if (winWidth > pmatrix[m][0] && winHeight > pmatrix[m][1]) {
				backgroundResize(pmatrix[m][0], pmatrix[m][1]);   	
				panelResize(pmatrix[m][0], pmatrix[m][1]);
				console.log("panel dimension change, width="+pmatrix[m][0]+", height="+pmatrix[m][1]);
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
    	//resize canvas
		$('#'+my.panelId).width(w); $('#'+my.panelId).height(h);
		var canvas = document.getElementById(my.panelId);
		canvas.width = w; canvas.height = h;
		
		//reset dimension
		my.dimension.width = w;
		my.dimension.height = h;
		my.dimension.hwidth = w;
		for (var h in hmatrix)
		{ if (hmatrix[h][0] == w) {my.dimension.hheight = hmatrix[h][1];break;}}
		var adim;
		for (var a in amatrix)
		{ if (amatrix[a][0] == w) {adim = amatrix[a][1].split(',');break;}}
		my.dimension.awidth = parseInt(adim[0]);
		my.dimension.aheight = parseInt(adim[1]);
		my.dimension.swdith = w;
		my.dimension.sheight = my.dimension.aheight;
		var sy = my.dimension.hheight;
		var ax = w - my.dimension.awidth;
		var ay = sy;
		//sub object resize
		PANEL.screen.resize(0, sy, my.dimension.swdith, my.dimension.sheight);
		PANEL.assist.resize(ax, ay, my.dimension.awidth, my.dimension.aheight);
		PANEL.header.resize(0, 0, my.dimension.hwidth, my.dimension.hheight);
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
	
	my.enlarge = new createjs.Bitmap("/app/res/icons_enlarge40.png");
	my.enlarge.shadow = new createjs.Shadow("#090909", 1, 1, 2);
	my.container.addChild(my.enlarge);
	
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 1;
	
	my.enlarge.on("click", function(evt) {
		if (PANEL.isFullscreen())
			PANEL.exitFullScreen();
		else
			PANEL.fullScreen();
	});
	
	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.x = 0; my.square.y = 0;
		my.square.graphics.beginFill("#1999d8").drawRect(x, y, w, h);
		my.Label1.x = 10; my.Label1.y = (h - 16) / 2;
		my.Label1.text="NGO KidsMath " + PANEL.dimension.width+"X"+PANEL.dimension.height+","+PANEL.dimension.hheight+","+PANEL.dimension.awidth+"X"+PANEL.dimension.aheight;
		my.enlarge.x = (w - my.enlarge.image.width - 20);
		my.enlarge.y = (h - my.enlarge.image.height) / 2;
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
		my.stage.alpha = 0.9;
		my.stage.update();
	};
	return my;
}());

/**
 * panel assist sub-module
 */
PANEL.assist = (function() {
	var my = {};
	my.container = new createjs.Container();
	my.square = new createjs.Shape();
	my.container.addChild(my.square);
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 0.9;
	
	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.graphics.beginFill("#1999d8").drawRect(x, y, w, h);
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
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
	
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 0.6;
	
	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.graphics.beginFill("#1F1F1F").drawRect(x, y, w, h);
		my.Label1.x = x; my.Label1.y = y+10;
		my.Label1.text = "ver.13 Just toggle Enlarge 图标切换到全屏模式！";
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
		my.stage.update();
	};
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
			alert('当前为竖屏，请切换为横屏以获得更好的显示效果！');
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


