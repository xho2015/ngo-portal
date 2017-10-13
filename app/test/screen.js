/**************************************************
    This class represents 
    1. an area where a drawing located in
    2. an area where a drawings unin located in

    NOTE: this rect is generally the invalid area in the 
        screen that needs to be re-painted 
    
***************************************************/
function Rect(x, y, width, height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    this.Reset()
    {
        this.x = -1;
        this.y = -1;
        this.width = -1;
        this.height = -1;
    };

    this.Union = function(rect) {
        var tx = this.x == -1 ? rect.x : Math.min(this.x, rect.x);
        var ty = this.y == -1 ? rect.y : Math.min(this.y, rect.y);
        
        this.width = Math.max(this.x + this.width, rect.x + rect.width) - tx;
        this.height = Math.max(this.y + this.height, rect.y + rect.height) - ty;
        this.x = tx;
        this.y = ty;
    };
}








/**************************************************
    This Drawing class represents 
    1. an drawable object 

***************************************************/
function Drawing() {
    
    this.Rect = null;
    this.Dragable = false;
    this.IsDragging = false;
    this.FillColor = "#44FF44";
    this.IsDirty = false;

    //var drawings = [];
   
    this.OnClick = function(e)
    {

    };
}








/**************************************************
    This Layer class represents 

    1. a layer is grouping some drawings which belong to the same layer
    
***************************************************/
function Layer(x, y, width, height) {

    // an array of objects that define different drawings
    var drawings = [];

    var invalid = new Rect(-1, -1, -1, -1);

    var offset = new Rect(-1, -1, -1, -1);

    this.Add = function (d)
    {
        drawings.push(d);
    };


    this.Remove = function (d)
    {
        //TODO
    };

    this.IsCurrent = function(pageX, pageY)
    {

    };

    this.OnClick = function(e)
    {

    };
}









/**************************************************
This class represents a screen which
	1. render any sorts of drawings
	2. accept and dispatch user event to drawings

	TODO:
	-----------------------------------
	1. add double screen buffering for canvas rendering
		https://stackoverflow.com/questions/2795269/does-html5-canvas-support-double-buffering
        
        NEEDS TO CONFIRM: it is stated that Double-buffering is un-needed for canvas 
            (it does it automatically) and adversely harms your performance.

    2. drag and drop on canvas and distingush click
        http://jsfiddle.net/m1erickson/qm9Eb/

    3. requestAnimationFrame
        https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame
***************************************************/
function Screen() {

    //---SINGLETON
    if (Screen.prototype.singleton) {
      return Screen.prototype.singleton;
    }
    Screen.prototype.singleton = this;

	//---PUBLIC properties
    this.width = 0;
    this.height = 0;


    //---PRIVATE properties
    var canvas = document.getElementById("screen");
    var context = canvas.getContext("2d");

    //4K X 4K buffered canvas, this limitation is try to suit for some mobile browsers
    Screen.prototype.bufferCanvas = document.createElement("canvas");
    Screen.prototype.MAX_CWIDTH = 4096;
    Screen.prototype.MAX_CHEIGHT = 4096;   
    Screen.prototype.bufferCanvas.width = Screen.prototype.MAX_CWIDTH ;
    Screen.prototype.bufferCanvas.height = Screen.prototype.MAX_CHEIGHT;   
    Screen.prototype.bufferContext = Screen.prototype.bufferCanvas.getContext("2d");
    
    // drag related variables
    var dragOk = false;
    var startX, startY;
    var offsetX = 0;
    var offsetY = 0;
    // click detection
    var clickOk = 0;

    // an array of objects that define different drawings
    var layers = [];
   
    //---PUBLIC methods
    this.RegisterEvent = function() {
    	//register windows resize event
    	$(window).on('resize', function(){
    	   Screen.prototype.singleton.Resize();
		});

        //register screen/canvas click event        
        /*$(this.htmlCanvas).on('click', function(e){
           gScreen.OnClick(e);
        }); */

        // Register drag & drop event
        canvas.onmousedown = OnDown;
        canvas.onmouseup = OnUp;
        canvas.onmousemove = OnMove;
    };

    /************************ 
    Screen click event 
    *************************/
    var OnClick = function(e) {
    	
        //var mx = Math.floor((e.pageX - $(canvas).offset().left) / 20);
        //var my = Math.floor((e.pageY - $(canvas).offset().top) / 20);

        //dispatch click event to current layer
        for (var i = layers.length -1; i > 0; i--) {
            var l = layers[i];
            if (l.IsCurrent(e.pageX, e.pageY)){
                l.OnClick(e);
            }
        }

        //render the buffered canvas onto the original canvas element
        Redraw(); 
    };

    /************************ 
    Screen mouse down event 
    *************************/
    var OnDown = function (e) {
        
        // tell the browser we're handling this mouse event
        e.preventDefault();
        e.stopPropagation();

        clickOk = 1;

        // get the current mouse position
        var mx = parseInt(e.clientX - offsetX);
        var my = parseInt(e.clientY - offsetY);

console.log("onDown  x="+mx+",y="+my);

        // test each rect to see if mouse is inside
        dragOk = false;
        for (var i = 0; i < drawings.length; i++) {
            var r = drawings[i];
            if (mx > r.x && mx < r.x + r.width && my > r.y && my < r.y + r.height) {
                // if yes, set that rects isDragging=true
                dragOk = true;
                r.isDragging = true;
            }
        }

        // save the current mouse position
        startX = mx;
        startY = my;
    }

    /************************ 
    Screen mouse up event 
    *************************/
    var OnUp = function (e) { 

        // tell the browser we're handling this mouse event
        e.preventDefault();
        e.stopPropagation();

console.log("OnUp");

        // handle click
        if (clickOk == 1 )
        {
            clickOk = 0;
            OnClick(e);
        }

        // clear all the dragging flags
        dragOk = false;
        for (var i = 0; i < drawings.length; i++) {
            drawings[i].isDragging = false;
        }
    }


    /************************ 
    Screen mouse move event 
    *************************/
    var OnMove = function (e) {

        //detect click event are not in drag
        if (clickOk == 1 )
            clickOk = 0;

        // if we're dragging anything...
        if (dragOk) {

            // tell the browser we're handling this mouse event
            e.preventDefault();
            e.stopPropagation();

            // get the current mouse position
            var mx = parseInt(e.clientX - offsetX);
            var my = parseInt(e.clientY - offsetY);

console.log("OnMove  x="+mx+",y="+my);

            // calculate the distance the mouse has moved
            // since the last mousemove
            var dx = mx - startX;
            var dy = my - startY;

            // move each rect that isDragging 
            // by the distance the mouse has moved
            // since the last mousemove
            for (var i = 0; i < drawings.length; i++) {
                var r = drawings[i];
                if (r.isDragging) {
                    r.x += dx;
                    r.y += dy;
                }
            }

            // redraw the scene with the new rect positions
            Redraw();

            // reset the starting mouse position for the next mousemove
            startX = mx;
            startY = my;
        }
    }

    /************************ 
    Screen area invalid 
    *************************/
    var Invalid = function(x, y, w, h) {
        // clear the canvas
        bufferContext.clearRect(x, y, w, h);
        context.clearRect(x, y, w, h);
    }


    /************************ 
    redraw drawables 
    *************************/
    var Redraw = function() {
    	
        //TODO: distingush invalid area
        Invalid(0,0,Screen.prototype.singleton.width, 
            Screen.prototype.singleton.height);

        // redraw each rect in the rects[] array
        for (var i = 0; i < drawings.length; i++) {
            var r = drawings[i];
            bufferContext.fillStyle = r.fill;
            //rect(r.x, r.y, r.width, r.height);
            bufferContext.beginPath();
            bufferContext.rect(r.x, r.y, r.width, r.height);
            bufferContext.closePath();
            bufferContext.fill();
        }
        
        //TODO: distingush painting area
        Paint(0,0,Screen.prototype.singleton.width, 
            Screen.prototype.singleton.height);      
    };

    /************************ 
    Paint invalid area in screen 
    *************************/
    var Paint = function(x, y, w, h) {
        //render the buffered canvas onto the original canvas element
        //refer api: drawImage(image, sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight)
        context.drawImage(bufferCanvas, x, y, w, h, x, y, w, h); 
    }

    /************************ 
    Screen size change 
    *************************/
    this.Resize = function() {
        this.width = window.innerWidth;;
        this.height = window.innerHeight;

        canvas.width = this.width;
        canvas.height = this.height;

        bufferCanvas.width = this.width;
        bufferCanvas.height = this.height;
        
        Redraw();
    };
}



























