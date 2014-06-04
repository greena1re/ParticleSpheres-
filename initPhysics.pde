void initPhysics()  {


physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
//physics2.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM))); 
gravity = new GravityBehavior(new Vec3D(0, 1, 0)); 

centerMovements[0].trigger();
centerMovements[1].trigger();
centerMovements[2].trigger();
centerMove = new Vec3D(centerMovements[0].getValuef(), centerMovements[1].getValuef(), centerMovements[2].getValuef());
centers.add(new Vec3D(-400, -100, -150)); 
centers.add(new Vec3D(-300, 100 , -50));
centers.add(new Vec3D(0, 100, -600));
centers.add(new Vec3D(600, 400 , -150));
centers.add(new Vec3D(200, -200, -300));
centers.add(new Vec3D(150, 600,  -500));
centers.add(new Vec3D(-700, 700, -300));

repulse = new AttractionBehavior(new Vec3D(0,0,0), 300,  -.1, .2);
attract0 = new AttractionBehavior(centers.get(0).add(centerMove), 1000, .1);
attract1 = new AttractionBehavior(centers.get(1).add(centerMove), 1000, .05);
attract2 = new AttractionBehavior(centers.get(2).add(centerMove), 1000, .1);
attract3 = new AttractionBehavior(centers.get(3).add(centerMove), 1000, .1);
attract4 = new AttractionBehavior(centers.get(4).add(centerMove), 300, .1);

physics.addBehavior(gravity); 
physics.addBehavior(repulse);
physics.addBehavior(attract0);
physics.addBehavior(attract1);
physics.addBehavior(attract2);
physics.addBehavior(attract3);
physics.addBehavior(attract4);

for (int i = 0; i < centers.size(); i++){
if (i < 5){
  constraints.add(new SphereConstraint(centers.get(i), 100, SphereConstraint.OUTSIDE));

 }
 else {
   secondaryconstraints.add(new SphereConstraint(centers.get(i), 40, SphereConstraint.OUTSIDE));
   physics.addBehavior(new AttractionBehavior(centers.get(i), 300, .1, .05));
   physics.addBehavior(new AttractionBehavior(centers.get(i).add(centerMove) , 400, .1, .05 ) );  
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 300, .1, .1) );

     }
}

 // physics.addConstraintToAll(world, physics.particles);
//this is a bit hacky-- we really just want the attraction behaviors added to the arraylist
// for ( AttractionBehavior b : physics.behaviors){
//  attractors.add(b);
//        }  

particleList = new ArrayList<Particle>(); 
  float xoff = 0; float yoff = 0; float zoff = 0; 
for (int i = 0; i<NUM_PARTICLES; i++){
       if (i<NUM_PARTICLES/4) {
       particleList.add(new Particle(new Vec3D(centers.get(0).x+ random(0, 1000), centers.get(0).y + random(0,500), centers.get(0).z + random(0,-200) ),color(#0000FF),  (int)random(50,100), random(10, 11) ) ); 
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
  
  //boids.add(new Boid(new Vec3D(random(-300,300), random(-300,300), random(-400, -100) ), 3, .5, 25, 50)); 


  };  
// for (Boid b : boids) {
//   	physics.addParticle((VerletParticle) b); 
//   }



for ( Particle particle : particleList) {
physics.addParticle((VerletParticle) particle);
particle.trailsVisible= true; 
}

boundingSphere = new SphereConstraint(new Vec3D(0,0,0), 600, SphereConstraint.INSIDE);
constraints.add(boundingSphere);
physics.addConstraintToAll(boundingSphere, physics.particles); 

for (ParticleConstraint p : constraints) {
	physics.addConstraintToAll(p, physics.particles);
}

   }




 void updateParticles() {
  Vec3D grav=Vec3D.Y_AXIS.copy();
  grav.rotateX(mouseY*0.01);
  grav.rotateY(mouseX*0.01);
  gravity.setForce(grav.scaleSelf(2));
  int numP=physics.particles.size();
  if (random(1)<0.8 && numP<NUM_SPRINGS) {
    Particle p=new Particle(new Vec3D(random(-1,1)*10,-DIM,random(-1,1)*10));
  //   if (useBoundary)
     p.addConstraint(boundingSphere);
     physics.addParticle(p);
  // }
  if (numP>10 && physics.springs.size()<1400) {
    for(int i=0; i<60; i++) {
      if (random(1)<0.04) {
        Particle q= (Particle) physics.particles.get((int)random(numP));
        Particle r=q;
        while(q==r) {
          r=(Particle) physics.particles.get((int)random(numP));
        }
        physics.addSpring(new VerletSpring(q,r,REST_LENGTH, 0.0002));
      }
    }
  }
  float len=(float)numP/NUM_PARTICLES*REST_LENGTH;
  for(Iterator i=physics.springs.iterator(); i.hasNext();) {
    VerletSpring s=(VerletSpring)i.next();
    s.setRestLength(random(0.9,1.1)*len);
        }
  //physics.update();
      } 
    }
 