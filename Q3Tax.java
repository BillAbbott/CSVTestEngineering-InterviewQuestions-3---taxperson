
/*  
  This is a simple Java program to answer Comcast’s Question 3
  Call this file “Q3Tax.java"

Q3:
Calculate the total cost of an item including tax based on whether it is considered 
a necessary or luxury item. The tax rate for basic necessities is 1%, the tax rate 
for luxury items is 9%. For simplicity of implementation, all transactions are 
measured in cents (pennies).

 */  

import java.util.Locale;
import java.lang.Math.*;

public class Q3Tax  {

   static Boolean verbose = new Boolean( false );

   private static final double LUXRATE = 0.09; // 9 percent
   static final double NECESS_RATE = 0.01; // 1 percent
   static final String LUXNAME = "Luxury";
   static final String NECESS_NAME = "Necessity";


   // Your program begins with a call to main().
   public static void main(String args[])  {

      Boolean luxury = false;   // default non-luxury
      Integer price = new Integer( 0 );
      Double taxRate = new Double (0.0);
      String category = new String ("");
      String sArgsZipContent = "Please enter Luxury or Necessity (LNln) and price in cents, such as 'L 66595' separated by whitespace";

      if (verbose) System.out.println("args.length " + args.length );
      if (0 < args.length )  {
          sArgsZipContent = args[0];
      }
      if (verbose) System.out.println("Hello " + sArgsZipContent );

      for (int count = 0; count < args.length; count++ ) {
         category = new String( args[ count ].toUpperCase(Locale.ENGLISH) );
         count++; // loop uses args[n] and args[n+1]
         if (category.contains( new String( "L" ))) {
            luxury= new Boolean(true); // will autoboxing take care of this?             
         } else {
            luxury= new Boolean(false);             
         } // if category.contains... else...

         if ( count < args.length  ) {
            price = new Integer(args[ count ]); 
            if (luxury) {
                 taxRate = new Double( LUXRATE); // nine percent.
                 category = new String( LUXNAME );
            } else {
                 taxRate = new Double(NECESS_RATE); // one percent.
                 category = new String( NECESS_NAME );
            } 
            int tax = (int) Math.round(( price * taxRate ));
            int total = price + tax;
            String output = new String(  category + " item;  price: " + price + "  tax: " + tax + "  = Total: " + total + "\n" );
            System.out.println( output );
         } else { 
         }       // if count < args.len else..


      } // for int count...



   } // public static void main...


} // class Q3Tax





















