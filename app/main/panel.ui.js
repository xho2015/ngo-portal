/**
 * NGO main panel user interface. ver6 houxuyong@hotmail.com
 */
var PANEL = (function(my) {
	my.name = 'NGO Panel GUI ver.8';
	
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
    	hwidth:0,  	hheight: 0,
    	swidth: 0, 	sheight: 0,
    	awidth: 0, 	aheight: 0
    };
    
    //screen resolution matrix
    var pmatrix = [[ 1600, 900 ], [ 1440, 810 ], [ 1280, 720 ],
		[ 1120, 630 ], [ 960, 540 ], [ 800, 450 ], [ 640, 360 ],
		[ 600, 340 ], [ 340, 240 ] ];

    //header resolution matrix
    var hmatrix = new AppCommon.Map().putArray([[ 1600, 80 ], [ 1440, 80 ], [ 1280, 80 ],
		[ 1120, 60 ], [ 960, 60 ], [ 800, 40 ], [ 640, 40 ],
		[ 600, 40 ], [ 340, 40 ]]);
    
    //property resolution matrix
    var amatrix = new AppCommon.Map().putArray([[1600, '320,820' ], [ 1440, '320,730' ], [ 1280, '320,640' ],
		[ 1120, '240,570' ], [ 960, '240,480' ], [ 800, '180,410' ], [ 640, '180,320' ],
		[ 600, '180,300' ], [ 340, '0,0' ]]);
    
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
				console.log("panel dimension changed, width="+pmatrix[m][0]+", height="+pmatrix[m][1]);
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
		my.dimension.hheight = hmatrix.get(w);
		var adim = amatrix.get(w).split(',');
		my.dimension.awidth = parseInt(adim[0]);
		my.dimension.aheight = parseInt(adim[1]);
		my.dimension.swdith = w;
		my.dimension.sheight = my.dimension.aheight;
		var sy = my.dimension.hheight;
		var ax = w - my.dimension.awidth;
		var ay = sy;
		//sub object resize
		PANEL.screen.resize(0, sy, my.dimension.swdith, my.dimension.sheight);
		PANEL.property.resize(ax, ay, my.dimension.awidth, my.dimension.aheight);
		PANEL.header.resize(0, 0, my.dimension.hwidth, my.dimension.hheight);
	}
	return my;
}(PANEL || {}));

/**
 * panel header sub-module
 */
PANEL.header = (function() {
	var style = {
		PADDING: 16
	};
	var my = {};
	
	my.hoverIdx = -1;
	my.container = new createjs.Container();
	my.square = new createjs.Shape();
	my.container.addChild(my.square);
	my.Label1 = new createjs.Text("", "17px Arial", "#eeffff");
	my.Label1.shadow = new createjs.Shadow("#090909", 1, 1, 6);
	my.container.addChild(my.Label1);

	my.hover = new createjs.Shape();
	my.container.addChild(my.hover);

	my.enlarge = new createjs.Bitmap(MAINAPP.resource.get("app.res.km-icons-3-36x36-ngpng"));
	my.enlarge.shadow = new createjs.Shadow("#090909", 1, 1, 1);
	my.container.addChild(my.enlarge);
	
	my.property = new createjs.Bitmap(MAINAPP.resource.get("app.res.km-icons-2-36x36-ngpng"));
	my.property.shadow = new createjs.Shadow("#090909", 1, 1, 1);
	my.container.addChild(my.property);
	
	my.profile = new createjs.Bitmap(MAINAPP.resource.get("app.res.km-icons-4-36x36-ngpng"));
	my.profile.shadow = new createjs.Shadow("#090909", 1, 1, 1);
	my.container.addChild(my.profile);
	
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);

	function selectEffect(x, y)	{
		var idx2 = my.container.getChildIndex(my.hover);
		my.container.removeChild(my.hover);
		my.hover = new createjs.Shape();
		my.container.addChildAt(my.hover, idx2);
		my.hover.graphics.beginFill("#5e5e5e").drawRect(x - style.PADDING/2, 0, 40+style.PADDING, PANEL.dimension.hheight);
		my.stage.update();
	};
	
	my.property.on("click", function(evt) {
		PANEL.property.hide();
		selectEffect(this.x, this.y);
	});
	
	my.enlarge.on("click", function(evt) {
		if (PANEL.isFullscreen())
			PANEL.exitFullScreen();
		else
			PANEL.fullScreen();
		selectEffect(this.x, this.y);
	});
	
	my.profile.on("click", function(evt) {
		selectEffect(this.x, this.y);
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
		my.property.x = (w - PANEL.dimension.hheight - style.PADDING/2);
		my.property.y = (h - my.property.image.height) / 2;
		my.property.hitArea  = new createjs.Shape();
		my.property.hitArea.graphics.beginFill("#FFF000").drawRect(0,0,40,40);
        
		my.enlarge.x = (my.property.x - my.enlarge.image.width - style.PADDING);
		my.enlarge.y = my.property.y;
		my.enlarge.hitArea  = new createjs.Shape();
		my.enlarge.hitArea.graphics.beginFill("#FFF000").drawRect(0,0,40,40);
		
		my.profile.x = (my.enlarge.x - my.profile.image.width - style.PADDING);
		my.profile.y = my.enlarge.y;
		my.profile.hitArea  = new createjs.Shape();
		my.profile.hitArea.graphics.beginFill("#FFF000").drawRect(0,0,40,40);
         
		my.stage.drawRect = new createjs.Rectangle(x, y, w, h);
		my.stage.update();
	};
	return my;
}());

/**
 * panel property sub-module
 */
PANEL.property = (function() {
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
	
	my.hide = function() {
		my.container.visible = !my.container.visible;
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
	my.Label1 = new createjs.Text("", "16px Arial", "#F0FFF0");
	my.container.addChild(my.Label1);
	
	my.stage = new createjs.Stage(PANEL.panelId);
	my.stage.addChild(my.container);
	my.stage.alpha = 0.3;
	
	my.resize = function(x, y, w, h) {
		var idx = my.container.getChildIndex(my.square);
		my.container.removeChild(my.square);
		my.square = new createjs.Shape();
		my.container.addChildAt(my.square, idx);
		my.square.graphics.beginFill("#1F1F1F").drawRect(x, y, w, h);
		my.Label1.x = x; my.Label1.y = y+10;
		my.Label1.text = MAINAPP.DEBUGINFO.toString();
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
			//try trigger fullscreen won't work for security reason
			//https://stackoverflow.com/questions/24878297/javascript-trigger-fullscreen
			PANEL.screenStatus.orientation = 0;
		}
	}

	/*
	 * will be invoked when this module loaded
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


