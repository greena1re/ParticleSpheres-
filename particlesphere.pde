import processing.opengl.*;

import toxi.physics.*;
import toxi.physics.behaviors.*;
import toxi.physics.constraints.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
//import toxi.geom.Vec3D.*; 
import toxi.math.*;
import toxi.volume.*;
import toxi.processing.*;
import peasy.*;
import controlP5.*; 
import java.util.Iterator; 

int NUM_PARTICLES = 1000;
int NUM_CONSTRAINTS = 4; 
color DEFAULT_COLOR = #AA00FF; 
int DEFAULT_STROKEWEIGHT=1; 
int DIM  = 1000; 
float snapDist= 10*10; //minimum distance for dragging
ToxiclibsSupport gfx; 
VerletPhysics physics, physics2; 
PeasyCam cam; 
ArrayList<ParticleConstraint> constraints = new ArrayList<ParticleConstraint>(); 
ArrayList<ParticleConstraint> secondaryconstraints = new ArrayList<ParticleConstraint>(); 

ArrayList<Particle> particleList = new ArrayList<Particle>(); 
ArrayList<Vec3D> centers = new ArrayList<Vec3D>(); 
color[] particleColors; 
Vec3D[][][] velocityVectorField;
//HashMap<VerletParticle, Integer> particleColor = new HashMap<VerletParticle, Integer>(); 
boolean constraintsVisible = false; 
boolean boundsVisible = false; 
boolean displayOn = true; 
boolean particlesVisible = true; 
AttractionBehavior selected; 

void setup() { 
size(1500, 500, P3D);
colorMode(HSB, 360, 100, 100, 100);	

cam = new PeasyCam(this, 200);

gfx = new ToxiclibsSupport(this);
physics = new VerletPhysics(); 
//physics2 = new VerletPhysics(); 
initPhysics(); 

//lights();

particleList = new ArrayList<Particle>(); 
for (int i = 0; i<NUM_PARTICLES; i++){
       if (i<NUM_PARTICLES/4) {
       particleList.add(new Particle(new Vec3D(centers.get(0).x+ random(0, 1000), centers.get(0).y + random(0,500), centers.get(0).z + random(0,-200) ) ) ) ;
        for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
       }
      else if  (i < NUM_PARTICLES/2) {
     particleList.add(new Particle( new Vec3D(centers.get(1).x+ random(-200,200 ), centers.get(1).y + random(-200,200), centers.get(1).z + random(-200,200) ))) ;
       for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
     }
      else if  (i < 2*NUM_PARTICLES/3) {
     particleList.add(new Particle( new Vec3D(centers.get(2).x+ random(-200,200 ), centers.get(2).y + random(-200,200), centers.get(2).z + random(-200,200) ))) ;
              for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
     }
      else if (i <4*NUM_PARTICLES/5) { 
     particleList.add(new Particle( new Vec3D(centers.get(5).x + random(-200,200), centers.get(5).y + random(-200,200), centers.get(5).z + random(-200,200)).getNormalizedTo(random(20, 80) )));
     particleList.get(i).addConstraint(secondaryconstraints.get(0));
   ;  }
     else { 
     particleList.add(new Particle( new Vec3D(centers.get(6).x + random(-200,200), centers.get(6).y + random(-200,200), centers.get(6).z + random(-200,200)).getNormalizedTo(random(20, 80) )));
     particleList.get(i).addConstraint(secondaryconstraints.get(1)); 
   ;  }

 };  
// for (int i = 0; i <NUM_PARTICLES;i++){
// physics.addParticle( (VerletParticle) new Particle(
// 	new Vec3D(random(0, 1000), random(-200,200), random(0, -200) ) ,
//  ( i%3==0 ? #0000FF : #AAOOFF),  random(20,80), random(1,3) ) );
// }

for ( Particle particle : particleList) {
physics.addParticle((VerletParticle) particle);
}




 }

void draw() {
    float x1off=0.; 
    float y1off=0.;
    float z1off=0.;


background(0);
physics.update(); 
//physics2.update(); 



if (boundsVisible) {
stroke(255, 120);
strokeWeight(1);
noFill();
box(physics.getWorldBounds().getExtent().x*2);
}

if (constraintsVisible) {
for (Vec3D center : centers)
{

stroke(#0000AA);
strokeWeight(1);
noFill();
pushMatrix();
translate(center.x, center.y, center.z);
//Sphere(100);
popMatrix();
colorMode(HSB); 
strokeWeight(random(3,12));
stroke(#AA00FF); 
gfx.point(center);
   }

} 


 if (particlesVisible)
{   
	for (Particle p : particleList)  {
	// strokeWeight(1);
	// stroke(#8800FF, 40); 
	// gfx.point(p);
	p.display(); 
    }
}

//popMatrix();
for (Particle  s : particleList){

	s.display(); 
}
fill(255);
text("framerate:  " + frameRate, 20, 480);
println("framerate  : "  + frameRate);
}


void keyPressed()  {
  
if (key == 'c')  {
      if (constraintsVisible) constraintsVisible = !constraintsVisible; 
      else if (!constraintsVisible) constraintsVisible = true; 
      }

if (key == 'w'){ 
     if (boundsVisible) boundsVisible = !boundsVisible;
      else if (!boundsVisible) boundsVisible = true; 
   	  }

if (key == 'p')  {
    if (particlesVisible) particlesVisible = !particlesVisible;
        else if (!particlesVisible) particlesVisible = true; 
      }
 }

void mousePressed() {
  selected=null;
  Vec3D mousePos=new Vec3D(mouseX,mouseY, 0);
  for(Iterator i=physics.behaviors.iterator(); i.hasNext();) {
    AttractionBehavior p=(AttractionBehavior)i.next();
 
    if (p.getAttractor().distanceToSquared(mousePos)<snapDist)  {
      selected=p;
      break;
    }
  }
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



