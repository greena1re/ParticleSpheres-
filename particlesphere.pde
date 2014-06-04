import processing.opengl.*;
import heronarts.lx.*;
import heronarts.lx.modulator.*;
import toxi.physics.*;
import toxi.physics.behaviors.*;
import toxi.physics.constraints.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.geom.Line3D;

//import toxi.geom.Vec3D.*; 
import toxi.math.*;
import toxi.volume.*;
import toxi.geom.*; 
import toxi.processing.*;
import peasy.*;
import controlP5.*; 
import java.util.Iterator; 

int NUM_PARTICLES = 1000;
int NUM_SPRINGS = 2000; 
int NUM_CONSTRAINTS = 7; 
color DEFAULT_COLOR = #4400AA; 
int DEFAULT_STROKEWEIGHT=20; 
int NUM_COLORS=4; 
int BOID_SIZE = 4; 
int REST_LENGTH = 400; 
int DIM  = 1000; 
float snapDist= 10*10; //minimum distance for dragging

//global booleans
boolean constraintsVisible = false; 
boolean boundsVisible = false; 
boolean particlesVisible = true; 
boolean framerateVisible = false; 
boolean flowFieldVisible = false; 

boolean displayOn = true; 
boolean constraintsOn = true; 
boolean attractorsOn = true; 


ToxiclibsSupport gfx; 
VerletPhysics physics, physics2; 
PeasyCam cam; 

//global arrays
ArrayList<AttractionBehavior> attractors = new ArrayList<AttractionBehavior>(); 
ArrayList<ParticleConstraint> constraints = new ArrayList<ParticleConstraint>(); 
ArrayList<ParticleConstraint> secondaryconstraints = new ArrayList<ParticleConstraint>(); 
ArrayList<Particle> particleList = new ArrayList<Particle>(); 
ArrayList<Vec3D> centers = new ArrayList<Vec3D>(); 
ArrayList<Boid> boids = new ArrayList<Boid>(); 

Matrix4x4 colorMatrix=new Matrix4x4().scale(255f/(DIM*2)).translate(DIM,DIM,DIM);

color[] particleColors = new color[NUM_COLORS]; 
Vec3D centerMove; 

VectorField flowField = new VectorField(30);
//HashMap<VerletParticle, Integer> particleColor = new HashMap<VerletParticle, Integer>(); 

SphereConstraint boundingSphere; 

GravityBehavior gravity; 

AttractionBehavior selected; 
AttractionBehavior repulse; 
AttractionBehavior attract0;
AttractionBehavior attract1; 
AttractionBehavior attract2;
AttractionBehavior attract3;
AttractionBehavior attract4;

//modulators
Click repulseClick;
SinLFO repulseRadius;

SinLFO[] centerMovements = new SinLFO[3];

SinLFO rgb; 




void setup() { 
size(1500, 500, P3D);
//colorMode(HSB, 360, 100, 100, 100);	

cam = new PeasyCam(this, 200);

gfx = new ToxiclibsSupport(this);
physics = new VerletPhysics(); 
//physics2 = new VerletPhysics(); 
 

 
particleColors[0]=#0000FF; 
particleColors[1]=#1100FF;
particleColors[2]=#2200FF;

centerMovements[0]= new SinLFO(-300, 300, 10000); 
centerMovements[1]= new SinLFO(-300, 300, 7000); 
centerMovements[2]= new SinLFO(-300, 300, 6000); 

rgb = new SinLFO(0, 255, 2000);


initPhysics(); 



repulseClick = new Click(2000);
 repulseRadius = new SinLFO(0, 1000, 9000);
repulseClick.trigger();  
repulseRadius.trigger();
rgb.trigger(); 


 }

void draw() {
  

 float x1off=0.; 
 float y1off=0.;
  float z1off=0.;

if (repulseClick.click()) {
	println("repulseClick");
	repulse.setStrength(-50);
	repulse.setRadius(2000);
}

// float r =repulseRadius.getValuef();
// float s= -repulseRadius.getValuef();
// repulse.setStrength(s);
// repulse.setRadius(r);

// attract0.getAttractor().set(centers.get(0).x+centerMovements[0].getValuef(), centers.get(0).y+centerMovements[1].getValuef(), centers.get(0).z + centerMovements[2].getValuef());
// attract1.getAttractor().set(centers.get(1).x+centerMovements[0].getValuef(), centers.get(1).y+centerMovements[1].getValuef(), centers.get(1).z + centerMovements[2].getValuef());
// attract2.getAttractor().set(centers.get(2).x+centerMovements[0].getValuef(), centers.get(2).y+centerMovements[1].getValuef(), centers.get(2).z + centerMovements[2].getValuef());
// attract3.getAttractor().set(centers.get(3).x+centerMovements[0].getValuef(), centers.get(3).y+centerMovements[1].getValuef(), centers.get(3).z + centerMovements[2].getValuef());
// attract4.getAttractor().set(centers.get(4).x+centerMovements[0].getValuef(), centers.get(4).y+centerMovements[1].getValuef(), centers.get(4).z + centerMovements[2].getValuef());



background(#010101); 
//background(#0A0096);
physics.update(); 
updateParticles(); 
flowField.run(); 




if (boundsVisible) {
stroke(255, 120);
strokeWeight(1);
noFill();
box(physics.getWorldBounds().getExtent().x*2);
}

if (constraintsVisible) {
for (AttractionBehavior b : attractors)
      { 
strokeWeight(random(3,12));
stroke(#FFFFFF); 
gfx.point(b.getAttractor());
   }
  }  
 
   ambientLight(rgb.getValuef(), 0, rgb.getValuef()); 
   directionalLight(40, 0, 180, -1, -1, 1);
  spotLight(60, 0, 200, 1000, 500, 0, -1, -1, -1, 60, 2); 
 if (particlesVisible)
{   colorMode(RGB);
	for (Boid p : boids)  {

 //   flowField.applyForce((VerletParticle) p);
	//  p.run(boids); 
}
for (VerletParticle particle : physics.particles) {
   strokeWeight(10);
	 stroke(#0000FF, 80); 
   flowField.applyForce(particle); 

	gfx.point((Vec3D)particle);
	 //specular(); 
	 // if (abs(p.x)>2*width/3 || abs(p.y)> 2*height/3){
	 // 	p.isActive=false; 
	 // } 
	
	//particle.display(); 
    }
}

if (flowFieldVisible){

   flowField.draw(); 

}




if  ( framerateVisible){
fill(255);
text("framerate:  " + frameRate, 20, 480);
println("framerate  : "  + frameRate); 
  }
}

void keyPressed()  {
  
if (key == 'c')  {
      if (constraintsVisible) constraintsVisible = !constraintsVisible; 
      else if (!constraintsVisible) constraintsVisible = true; 
      ///println("constraintstoggle");
      }

if (key == 'w'){ 
     if (boundsVisible) boundsVisible = !boundsVisible;
      else if (!boundsVisible) boundsVisible = true; 
   	  }

if (key == 'p')  {
    if (particlesVisible) particlesVisible = !particlesVisible;
        else if (!particlesVisible) particlesVisible = true; 
      }

if (key == 'f')  {
    if (framerateVisible) framerateVisible = !framerateVisible;
        else if (!framerateVisible) framerateVisible = true; 
   }

if (key == 'v')   {
	if (flowFieldVisible) flowFieldVisible = !flowFieldVisible;
        else if (!flowFieldVisible) flowFieldVisible = true; 
   }

if (key == 'r')   {
	if (constraintsOn) constraintsOn = !constraintsOn;  //println("constraints:  ON"); 
		else if (!constraintsOn) constraintsOn = true; //println("constraints: OFF");  
   }


if (key == 'a')   {
	if (attractorsOn) {
		attractorsOn = !attractorsOn; 
		println("attractors: OFF");
	   for (ParticleBehavior p : physics.behaviors) { physics.removeBehavior(p); }
       }

      else if (!attractorsOn)  {
          attractorsOn = true;  
          println("attractors: ON");
         for (AttractionBehavior b: attractors) { physics.addBehavior(b);}    
    }
  }
}


void mousePressed() {
  selected=null;
  Vec3D mousePos=new Vec3D(mouseX,mouseY, 0);
  // for(Iterator i=physics.behaviors.iterator(); i.hasNext();) {
  //   AttractionBehavior p= (AttractionBehavior)i.next();
 
  //   if (p.getAttractor().distanceToSquared(mousePos)<snapDist)  {
  //     selected=p;
    //   break;
    // }
 // }  //broken  because of multiple polymorphism on behaviors 
}

// only react to mouse dragging events if we have a selected attractor
void mouseDragged() {
  if (selected!=null) {
    selected.setAttractor(new Vec3D((float)mouseX,(float)mouseY, (float)mouseX+mouseY)) ;
  }
}

// if we had a selected attractor unlock it again and kill reference
void mouseReleased() {
  if (selected!=null) {
    selected=null;
  }
}



