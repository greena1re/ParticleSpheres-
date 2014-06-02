import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import toxi.physics.*; 
import toxi.physics.behaviors.*; 
import toxi.physics.constraints.*; 
import toxi.geom.*; 
import toxi.geom.mesh.*; 
import toxi.math.*; 
import toxi.volume.*; 
import toxi.processing.*; 
import peasy.*; 
import controlP5.*; 
import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class particlesphere extends PApplet {








//import toxi.geom.Vec3D.*; 




 
 

int NUM_PARTICLES = 1000;
int NUM_CONSTRAINTS = 4; 
int DEFAULT_COLOR = 0xffAA00FF; 
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
int[] particleColors; 
Vec3D[][][] velocityVectorField;
//HashMap<VerletParticle, Integer> particleColor = new HashMap<VerletParticle, Integer>(); 
boolean constraintsVisible = false; 
boolean boundsVisible = false; 
boolean displayOn = true; 
boolean particlesVisible = true; 
AttractionBehavior selected; 

public void setup() { 
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

public void draw() {
    float x1off=0.f; 
    float y1off=0.f;
    float z1off=0.f;


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

stroke(0xff0000AA);
strokeWeight(1);
noFill();
pushMatrix();
translate(center.x, center.y, center.z);
//Sphere(100);
popMatrix();
colorMode(HSB); 
strokeWeight(random(3,12));
stroke(0xffAA00FF); 
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



fill(255);
text("framerate:  " + frameRate, 20, 480);
println("framerate  : "  + frameRate);
}


public void keyPressed()  {
  
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

public void mousePressed() {
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
public void mouseDragged() {
  if (selected!=null) {
    selected.setAttractor(new Vec3D((float)mouseX,(float)mouseY, (float)mouseX+mouseY)) ;
  }
}

// if we had a selected attractor unlock it again and kill reference
public void mouseReleased() {
  if (selected!=null) {
    selected=null;
  }
}



class Bolt {

//want to represent information / communication through some kind of electricity/ lightning effect between particles 


;}
class Node extends VerletParticle{

  Node(Vec3D pos) {
    super(pos);
  }

  // All we're doing really is adding a display() function to a VerletParticle
  public void display() {
    fill(0xff0000AA);
    strokeWeight(5);
    //lights; 
    //specular; 
    //stroke(0);
   //sphere(20);
    point(this.x, this.y, this.z);
  }
}
class Particle extends VerletParticle{
float strokeWeightp;
int colorp;
int index; 
int alphap;

  Particle(Vec3D pos, int colorp, int alphap, float strokew) {
    super(pos);
     this.colorp=colorp; 
     this.alphap =alphap; 
     this.strokeWeightp=strokew;
     index=physics.particles.size(); 
  }
   
  Particle(Vec3D pos) {
    super(pos);
     this.colorp=DEFAULT_COLOR; 
     this.strokeWeightp=DEFAULT_STROKEWEIGHT;
     this.alphap= 50; 
     index=physics.particles.size(); 
  }



  public void display() {
    strokeWeight(this.strokeWeightp);
    //lights; 
    //specular; 
    stroke(this.colorp,this.alphap);
 
    gfx.point((Vec3D)this);
  }
}
ControlP5 ui; 

public void toggleUI() {

if (displayOn)
 { displayOn = !displayOn;} 
else displayOn = true;



}
public void initPhysics()  {
physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
//physics2.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM))); 


centers.add(new Vec3D(-400, -100, -150)); 
centers.add(new Vec3D(-300, 100 , -50));
centers.add(new Vec3D(0, 100, -600));
centers.add(new Vec3D(400, 200 , -150));
centers.add(new Vec3D(200, -200, -300));
centers.add(new Vec3D(150, 600,  -500));
centers.add(new Vec3D(-700, 700, -300));

for (int i = 0; i < centers.size(); i++){
if (i < 5){
  constraints.add(new SphereConstraint(centers.get(i), 100, SphereConstraint.OUTSIDE));
   //physics.addBehavior(new GravityBehavior(centers.get(i)));  
   physics.addBehavior(new AttractionBehavior(centers.get(i), 1000, .1f)); 
  // physics.addConstraintToAll(constraints.get(i), physics.particles); 
 }
 else {
   //constraints.add(new SphereConstraint(centers.get(i), 20, SphereConstraint.OUTSIDE));
   secondaryconstraints.add(new SphereConstraint(centers.get(i), 40, SphereConstraint.OUTSIDE));
   physics.addBehavior(new AttractionBehavior(centers.get(i), 300, .2f, .05f));
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 400, .1f, .05f ) );  
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 200, .1f, .1f) );
 }
}

//physics.addBehavior(new AttractionBehavior(new Vec3D(0,0,0), 1500 ,.0005, .05 ) ); 
// constraints.add(new SphereConstraint(centers.get(0), 100, SphereConstraint.OUTSIDE));
// constraints.add(new SphereConstraint(centers.get(1), 100, SphereConstraint.OUTSIDE));
// constraints.add(new SphereConstraint(centers.get(2), 100, SphereConstraint.OUTSIDE));
// constraints.add(new SphereConstraint(centers.get(3), 100, SphereConstraint.OUTSIDE));

// // SphereConstraint sphere2 = new SphereConstraint(centers[1], 75, true);
// SphereConstraint sphere3 = new SphereConstraint(centers[2], 75, true);
// SphereConstraint sphere4 = new SphereConstraint(centers[3], 75, true);

// physics.addBehavior(new AttractionBehavior(centers.get(0), 1000, .1) );
// physics.addBehavior(new AttractionBehavior(centers.get(1), 1000, .1) );
// physics.addBehavior(new AttractionBehavior(centers.get(2), 1000, .1) );
// physics.addBehavior(new AttractionBehavior(centers.get(3), 1000, .1) );
// physics.addBehavior(new AttractionBehavior(centers.get(4), 1000, .1) ); 


// physics.addConstraintToAll(constraints.get(0), physics.particles);
// physics.addConstraintToAll(constraints.get(1), physics.particles);
// physics.addConstraintToAll(constraints.get(2), physics.particles);
// physics.addConstraintToAll(constraints.get(3), physics.particles);

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "particlesphere" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
