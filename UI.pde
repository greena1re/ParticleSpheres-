ControlP5 ui;

void initGUI() {

	fill(#FFFFFF); 
  ui = new ControlP5(this);
  ui.setAutoDraw(false);
  ui.addSlider("ConstraintSize",300,1200,sphereRadius,50,50,100,14).setLabel("ConstraintSize");
  ui.addSlider("Num particles",100,500,NUM_PARTICLES,50,150,100,14).setLabel("Num Particles");


   ui.addToggle("showPhysics",particlesVisible,20,60,14,14).setLabel("show particles");
  // ui.addToggle("isWireFrame",isWireFrame,20,100,14,14).setLabel("wireframe");
  // ui.addToggle("isClosed",isClosed,20,140,14,14).setLabel("closed mesh");
  // ui.addToggle("toggleBoundary",useBoundary,20,180,14,14).setLabel("use boundary");

  // ui.addBang("initPhysics",20,240,28,28).setLabel("restart");
}

void gui() {
  hint(DISABLE_DEPTH_TEST);
  cam.beginHUD();
  ui.draw();
  cam.endHUD();
  hint(ENABLE_DEPTH_TEST);
}
