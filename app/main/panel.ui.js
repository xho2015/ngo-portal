/**
 * NGO UI library - H5 canvas based interface 
 */


var AppMainUI = (function() {	
	
	
	var resizeEvents = [];
	function registeResize(resize)
	{
		resizeEvents.push(resize);
		console.log("resize Events registed");
	}
	
	/*
	 * the only sensor of window resize event
	 **/
	function onWindowResize() {
		for (var i = 0; i < resizeEvents.length; i++) {
			var resize = resizeEvents[i];
			resize();
		}
	};
	
	function onResize() {
		canvas.width = panelscreen.innerWidth();
		canvas.height = panelscreen.innerHeight();
		BB = canvas.getBoundingClientRect();
		offsetX = BB.left;
		offsetY = BB.top;
		WIDTH = canvas.width;
		HEIGHT = canvas.height;
		
		console.log("onResize, WIDTH="+WIDTH+", HEIGHT="+HEIGHT);		
		drawMainUI();
	};
	
	function init()
	{	
		
		//create background canvas
		var bgscreen = $('#background_screen');
		$('<canvas id="backgound_canvas">')
		.css({
			position : 'absolute',
			left : 0,
			top : 0,
			width : '100%',
			height : '100%'
		}).attr({
			width : bgscreen.width(),
			height : bgscreen.height()
		}).prependTo(bgscreen);

		$(bgscreen).starfield({
			looprate : 60,
			speedX:2,
			starDensity : 0.18,
			mouseScale : 0.01,
			background : '#000007',
			seedMovement : true
		}, "backgound_canvas");	
		
		//enable star field effect
		panelscreen = $('#panel_screen');
		$('<canvas id="panel_canvas">')
		.css({
			position : 'absolute',
			left : 0,
			top : 0,
			width : '100%',
			height : '100%'
		}).attr({
			width : panelscreen.width(),
			height : panelscreen.height()
		}).prependTo(panelscreen);
		
		//initialize canvas
		canvas = document.getElementById('panel_canvas');
		ctx = canvas.getContext("2d");
		
		console.log("render App Main UI, canvas id="+canvas.id);

		// listen for mouse events
		canvas.onmousedown = myDown;
		canvas.onmouseup = myUp;
		canvas.onmousemove = myMove;	

		window.addEventListener( 'resize', onWindowResize, false );
		
		registeResize(onResize);
		onWindowResize();
	};
	
	var panelscreen;
	var canvas;
	var ctx ;
	var BB;
	var offsetX;
	var offsetY;
	var WIDTH;
	var HEIGHT;

	//drag related variables
	var dragok = false;
	var startX;
	var startY;

	//an array of objects that define different rectangles
	var rects = [];
	rects.push({
	    x: 5,
	    y: 5,
	    width: 120,
	    height: 60,
	    fill: "#040F04",
	    hover: false,
	    isDragging: false
	});
	
	rects.push({
	    x: 5,
	    y: 150,
	    width: 120,
	    height: 60,
	    fill: "#0F0F44",
	    hover: false,
	    isDragging: false
	});
	

	//draw a single rect
	function rect(x, y, w, h) {
	    ctx.beginPath();
	    ctx.rect(x, y, w, h);
	    ctx.closePath();
	    ctx.fill();
	}

	//clear the canvas
	function clear() {
	    ctx.clearRect(0, 0, WIDTH, HEIGHT);
	}

	//redraw the scene
	function drawMainUI() {
		clear();
	    // redraw each rect in the rects[] array
	    for (var i = 0; i < rects.length; i++) {
	        var r = rects[i];
	        if (r.hover){
	        	ctx.fillStyle = "#01005F";
        	}else{
        		ctx.fillStyle = r.fill;
        	}
	        rect(r.x, r.y, r.width, r.height);
	    }
	}

	//handle mousedown events
	function myDown(e) {

	    // tell the browser we're handling this mouse event
	    e.preventDefault();
	    e.stopPropagation();

	    // get the current mouse position
	    var mx = parseInt(e.clientX - offsetX);
	    var my = parseInt(e.clientY - offsetY);
	    
	    console.log("main.ui myDown  x="+mx+",y="+my);

	    // test each rect to see if mouse is inside
	    dragok = false;
	    for (var i = 0; i < rects.length; i++) {
	        var r = rects[i];
	        if (mx > r.x && mx < r.x + r.width && my > r.y && my < r.y + r.height) {
	            // if yes, set that rects isDragging=true
	            dragok = true;
	            r.isDragging = true;
	        }
	    }
	    // save the current mouse position
	    startX = mx;
	    startY = my;
	}

	//handle mouseup events
	function myUp(e) {  
	    // tell the browser we're handling this mouse event
	    e.preventDefault();
	    e.stopPropagation();

	    // clear all the dragging flags
	    dragok = false;
	    for (var i = 0; i < rects.length; i++) {
	        rects[i].isDragging = false;
	    }
	}

	//handle mouse moves
	function myMove(e) {
		
		// get the current mouse position
        var mx = parseInt(e.clientX - offsetX);
        var my = parseInt(e.clientY - offsetY);
      
	    // if we're dragging anything...
	    if (dragok) {

	        // tell the browser we're handling this mouse event
	        e.preventDefault();
	        e.stopPropagation();
	        
	        console.log("main.ui myMove  x="+mx+",y="+my);

	        // calculate the distance the mouse has moved
	        // since the last mousemove
	        var dx = mx - startX;
	        var dy = my - startY;

	        // move each rect that isDragging 
	        // by the distance the mouse has moved
	        // since the last mousemove
	        for (var i = 0; i < rects.length; i++) {
	            var r = rects[i];
	            if (r.isDragging) {
	                r.x += dx;
	                r.y += dy;
	            }
	        }
	        
	        drawMainUI();

	        //reset the starting mouse position for the next mousemove
	        startX = mx;
	        startY = my;
	    } 
	    else
	    {   
	    	var dirty = false;
	    	for (var i = 0; i < rects.length; i++) {
		        var r = rects[i];
		        if (mx > r.x && mx < r.x + r.width && my > r.y && my < r.y + r.height) {
		        	dirty = !r.hover;
		            r.hover = true;
		            console.log("hover=true");
		        } 
		        else
	        	{
		        	dirty = !r.hover;
		        	r.hover = false;
		        	console.log("hover=false");
	        	}
		    }
	    	if (dirty)
	    		drawMainUI();
	    }
	}
	
	
	return {
		init : init,
		registeResize : registeResize
	};
	
})();