void initPhysics()  {
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
   physics.addBehavior(new AttractionBehavior(centers.get(i), 1000, .1)); 
  // physics.addConstraintToAll(constraints.get(i), physics.particles); 
 }
 else {
   //constraints.add(new SphereConstraint(centers.get(i), 20, SphereConstraint.OUTSIDE));
   secondaryconstraints.add(new SphereConstraint(centers.get(i), 40, SphereConstraint.OUTSIDE));
   physics.addBehavior(new AttractionBehavior(centers.get(i), 300, .2, .05));
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 400, .1, .05 ) );  
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 200, .1, .1) );
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