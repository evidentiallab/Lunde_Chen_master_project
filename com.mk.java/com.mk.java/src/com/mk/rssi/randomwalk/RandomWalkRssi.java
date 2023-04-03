package com.mk.rssi.randomwalk;

import java.io.File;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class RandomWalkRssi {
      public static final int repetition_limit = 1 * 1000 * 1000;
      public static final long MAX_STEPS_NUM = 100 * 1000 * 1000;
      public double angle = 0;
      public double speed = 1;
      public double sigma2 = 0.5;
      public static final double rayon_all = 35;
      public static final double rayon_rssi_70 = 20; // <= is very good
      public static final double rayon_rssi_80 = 24; // < is acceptable, >= is bad
      public static final double pi = Math.PI;
      public static final double pi_sur_deux = Math.PI / 2;
      public static final double pi_fois_deux = Math.PI * 2;
      public double x;
      public double y;
      public int repetition_count;
      public int delta;
      public static final long sleepTime = 0;
      
      public boolean flag_next_walk;
      
      private static RandomWalkRssi instance = null;
      
      public PrimitivePhone 	mPrimitivePhone;
      public PrimitivePhone2 	mPrimitivePhone2;
      public PrimitivePhone3 	mPrimitivePhone3;
      public GreenPhone 		mGreenPhone;
      public KMAveragePhone mKMAveragePhone;
      
      public long totalWalkSteps;
      
      public static final double[][] mat = 
      {
  		{0.9, 0.1}, 
  		{0.2, 0.8}
  	  };
      public double[] vector;
      public int which;
      
      public void start()
      {
    	  mGreenPhone		.initPhone();
    	  mPrimitivePhone	.initPhone();
    	  mPrimitivePhone2	.initPhone();
    	  mPrimitivePhone3	.initPhone();
    	  
    	  Constants.write("mGreenPhone.rssiUpdateRateGreen	=	" 		+ Phone.rssiUpdateRateGreen, 
    			  new File(Constants.ROOT_DIR  + "README"));
    	  
    	  while(repetition_count < repetition_limit 
//    			  && totalWalkSteps <= MAX_STEPS_NUM
    			  )
    	  {
    		  walk_Directional();
//    		  walk_Markov();
//    		  walk_Brown();
    	  }
    	  
    	  mGreenPhone		.logStatistics();
    	  mPrimitivePhone	.logStatistics();
    	  mPrimitivePhone2	.logStatistics();
    	  mPrimitivePhone3	.logStatistics();
      }
      
      public RandomWalkRssi()
      {
    	  this.repetition_count = 0;
    	  this.totalWalkSteps 	= 0L; // !!
    	  this.flag_next_walk 	= false;
    	  Constants.ROOT_DIR 	= Constants.ROOT_DIR + Constants.getNowTime("MMdd_HHmm_ss") + "_RandomWalk_" + repetition_limit + "/";
    	  
    	  this.mPrimitivePhone 		= new PrimitivePhone();
    	  this.mPrimitivePhone2 	= new PrimitivePhone2();
    	  this.mPrimitivePhone3 	= new PrimitivePhone3();
    	  this.mGreenPhone 			= new GreenPhone();
    	  this.mKMAveragePhone 		= new KMAveragePhone();
    	  this.which 				= 1;
    	  this.vector 				= mat[1];
    	  
    	  System.out.println(mPrimitivePhone.logType);
    	  System.out.println(mPrimitivePhone2.logType);
    	  System.out.println(mPrimitivePhone3.logType);
    	  System.out.println(mGreenPhone.logType);
    	  System.out.println(mKMAveragePhone.logType);
    	  
      }
	
      public synchronized static RandomWalkRssi getInstance()
      {
    	  if(instance == null)
    	  {
    		  {
    			  instance = new RandomWalkRssi();
    		  }
    	  }
    	  return instance;
      }
      
      public void walk_Brown()
      {
    	  init_Brown();
    	  System.out.println("\nInitiation finished, x = " + x + ", y = " + y);
    	  afterInitOneWalk();
    	  while(isIn_rayon_all(true))
    	  {
//    		  move_Brown();
    		  totalWalkSteps ++;
    		  move_Brown_with_judgement();
    	  }
    	  
    	  afterOneWalk();
      }
      
      public void walk_Directional()
      {
    	  init_Directional();
    	  System.out.println("\nInitiation finished, x = " + x + ", y = " + y);
    	  afterInitOneWalk();
    	  while(isIn_rayon_all(true))
    	  {
//    		  move_Directional();
    		  totalWalkSteps ++;
    		  move_Directional_with_judgement();
    	  }
    	  afterOneWalk();
      }
      
      public void walk_Markov()
      {
    	  init_Directional(); // It's OK
    	  System.out.println("\nInitiation finished, x = " + x + ", y = " + y);
    	  afterInitOneWalk();
    	  while(isIn_rayon_all(true))
    	  {
    		  totalWalkSteps ++;
    		  move_Markov_with_judgement();
    	  }
    	  afterOneWalk();
      }
      
      public void afterInitOneWalk()
      {
    	  mPrimitivePhone	.afterInitOneWalk();
    	  mPrimitivePhone2	.afterInitOneWalk();
    	  mPrimitivePhone3	.afterInitOneWalk();
    	  mGreenPhone		.afterInitOneWalk();
    	  
    	  deltaSampling();
      }
      
      public void afterOneWalk()
      {
    	  flag_next_walk = true;
    	  
    	  mPrimitivePhone	.afterOneWalk();
    	  mPrimitivePhone2	.afterOneWalk();
    	  mPrimitivePhone3	.afterOneWalk();
    	  mGreenPhone		.afterOneWalk();
    	  
    	  flag_next_walk = false;
    	  repetition_count ++;
      }
      
// ******************************************************  //  
// ******************************************************  //   
// ******************************************************  //    
      
      public void move_Directional_with_judgement()
      {
    	  move_Directional();
    	  reactOneMove();
      }
      
      public void move_Brown_with_judgement()
      {
    	  move_Brown();
    	  reactOneMove();
      }

      public void move_Markov_with_judgement()
      {
    	  move_Markov();
    	  reactOneMove();
      }
      
      public void move_Markov()
      {
    	  which = multiNomialSample(vector);
    	  if(which == 0)
    	  {
    		  vector = mat[0];
    	  }
    	  else if(which == 1) 
    	  {
    		  move_Directional();
    		  vector = mat[1];
    	  }
      }
      
      public void move_Directional()
      {
    	//可以考虑写成 angle = (new NormalDistribution(angle, sigma2)).sample(); //!!
    	  angle = (new NormalDistribution(0, sigma2)).sample();
          // !!!!!! C'est pas la peine    	  
//    	  while (angle > pi_fois_deux || angle < -pi_fois_deux) {
//    		  angle = (new NormalDistribution(0, sigma2)).sample();
//    	  }
    	  x += speed * Math.cos(angle);
    	  y += speed * Math.sin(angle);
//    	  System.out.println("(" + x + " , " + y + ")");
      }
      
      public void move_Brown()
      {
    	  angle = (new UniformRealDistribution()).sample() * 2 * pi;
    	  speed = (new UniformRealDistribution(0.5, 2)).sample();
    	  x += speed * Math.cos(angle);
    	  y += speed * Math.sin(angle);
//    	  System.out.println("(" + x + " , " + y + ")");
      }
      
      public void reactOneMove()
      {
    	  deltaSampling();
    	  mPrimitivePhone	.judge();
    	  mPrimitivePhone2	.judge();
    	  mPrimitivePhone3	.judge();
    	  mGreenPhone	 	.judge();
      }
      
      public boolean isIn_rayon_all(boolean really)
      {
    	  return x * x + y * y + (really?  0 : delta) < rayon_all * rayon_all;
      }
      
      public boolean isIn_rayon_very_strong(boolean really)
      {
    	  return x * x + y * y + (really? 0 : delta) < rayon_rssi_70 * rayon_rssi_70;
      }
      
      public boolean isIn_rayon_little_weak(boolean really)
      {
    	  return x * x + y * y + (really? 0 : delta) < rayon_rssi_80 * rayon_rssi_80;  //
      }
      
      public boolean isIn_rayon_weak(boolean really)
      {
    	  return x * x + y * y + (really? 0 : delta) >= rayon_rssi_80 * rayon_rssi_80;  //
      }
      
      public void logLocation() //可以用来展示 Phone 移动的 Trace
      {
    	  
      }
      
      public int getNowSignalStrength()
      {
    	  if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(false))
    	  {
    		  return Constants.VERY_STRONG;
    	  }
    	  else if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(false))
    	  {
    		  return Constants.LITTLE_WEAK;
    	  }
    	  else 
    	  {
    		  return Constants.WEAK;
    	  }
      }
      
      public int getNowSignalStrengthReal()
      {
    	  if(RandomWalkRssi.getInstance().isIn_rayon_very_strong(true))
    	  {
    		  return Constants.VERY_STRONG;
    	  }
    	  else if(RandomWalkRssi.getInstance().isIn_rayon_little_weak(true))
    	  {
    		  return Constants.LITTLE_WEAK;
    	  }
    	  else 
    	  {
    		  return Constants.WEAK;
    	  }
      }
      
      public void sleep()
      {
    	  try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      }
      
      public void init_Directional()
      {
    	  x = (new UniformRealDistribution()).sample() * rayon_all;
    	  y = (new UniformRealDistribution()).sample() * rayon_all;
    	  if (!isIn_rayon_all(true)) {
    		  init_Directional();
    	  }
      }
      
      public void init_Brown()
      {
    	  x = (new UniformRealDistribution()).sample() * rayon_all;
    	  y = (new UniformRealDistribution()).sample() * rayon_all;
    	  if (!isIn_rayon_all(true)) {
    		  init_Brown();
		}
      }
      
      // Yes! MultiNomial !!!
      public int rssiMultiNomial(int rss)
      {
    	  return 0; 
      }
      
      public static int rssi(double distance)
  	  {
    	  double rssi_1_meter = -34;
    	  double ksi = 85;
  		  double coefficient_n_fois_10 = (-52 - rssi_1_meter - ksi) / StrictMath.log(15);
  		  return (int) (rssi_1_meter + coefficient_n_fois_10 * StrictMath.log(distance) + ksi);
  	  }
      
      public void deltaSampling()
  	  {
  		double a = (new NormalDistribution(0, 2.5)).sample();
  		delta = (int) Math.abs(a) % 9;
  		if(a < 0)
  			delta *= -1;
//  		System.out.println("delta = " + delta);
  	  }
      
      public int multiNomialSample(double... params)
  	  {
  		  double sum = 0;
  		  double e = 0.001;
  		  for(int i = 0; i < params.length; i ++)
  		  {
  			  sum += params[i];
  		  }
  		  if(sum < 1 - e || sum > 1 + e)
  			  return -1;
  		
  		  int which = 0;
  		  double point = params[0];
  		  double random = new UniformRealDistribution().sample();   // Don't use the Random() of java, it sucks!!!
  		  while(point <= random)
  		  {
  			  which ++;
  			  point += params[which];
  		  }
  		  return which;
  	  }
      
      public static void main(String[] args)
      {
    	  getInstance().start();
      }
}
