
/*  
  This is a simple Java program to answer Comcast’s Question 6
  Call this file “Q6.java"
 */  

import java.util.ArrayList;

class Q6  {

   static Boolean verbose = new Boolean( false );

   // Your program begins with a call to main().
   public static void main(String args[])  {


   ArrayList<Integer> aStrip = new ArrayList<>(); // exploit autoboxing in SE 7 & later...
   ArrayList<ArrayList<Integer>> variations = new ArrayList<ArrayList<Integer>>(); // ok
   ArrayList<Integer> yield = new ArrayList<>();

//      System.out.println("args.length " + args.length );

      String sArgsZipContent = "Please enter strip values such as '10 9 7 12' separated by whitespace";
      if (0 < args.length )  {
          sArgsZipContent = args[0];
      }
//       System.out.println("Hello " + sArgsZipContent );


      // come to think of it, I could just have used the values in args, but this makes them ints at least...
      Boolean junk = false;

      for (int cnt = 0; cnt < args.length; cnt++) {
          junk = aStrip.add( new Integer( args[ cnt ] )); // since Integer is what we told it...
      }

  //    if ( aStrip.size() > 0 ) {
 //         System.out.println("aStrip " + aStrip.toString() );
//      }


   /*
I model different mining schemes with ArrayLists of integers. The same size as the ArrayList of patch yield values.
In the variations ArrayLists, a 1 means mine it, a 0 means don't. Step through the aStrip and variations ArrayLists in parallel.
multiply the value in aStrip by the value in variations, then add the result to the sum for a given mining scheme. 

And here's a simple illustration. A strip of 4 patches, each of which has a different assay or yield of valuable dilithium...: 
..0th.1st.2nd.3rd
| 10 | 9 | 7 | 12 |  That's a total of 38 units, but only two of the patches can be recovered at a time.

The question is, what sequence of patches should be mined for the highest yeild? Can't mine adjacent patches,
that's dangerous, so have to skip one between each pair mined. Or skip 2. For a 4 patch minimal set,
possible solutions are 0,,2, ,1,,3 or 0,,3. (meaning, for these solutions,  use the 0th and 2nd patches, skip the 1st and 3rd; 
OR, use the 1st and 3rd patches, skip the 0th and 2nd; OR, use the 0th and 3rd patches, skip the 1st and 2nd.)

So possible values are 10 + 7 = 17;  9 + 12 = 21;  or 10 + 12 = 22;
By inspection, 10 + 12 is what we want, its biggest, and requires no adjacent mining.
Its also not something that 1010 or 0101 will get, so its clear even starting with alternating 
mine - skip - mine - skip isn't robust.

 For a 5 patch set, 0,,2,,4; ,1,,3,; 0,,3,; ,1,,,4; Each added patch increases the possible variations. For short strips,
adding one patch adds one more solution, 

all positions are based on 0-based counting:

..0th.1st.2nd.3rd..4th.5th.
| 10 | 9 | 7 | 12 | 4 | 1 |  6 patches have 5, unique, saturated, solutions:

1: 0,1,0,1,0,1
2: 1,0,1,0,1,0
3: 1,0,0,1,0,1 . double-0s start at 1 (zero-based)
4: 0,1,0,0,1,0 . double-0s start at 2
5: 1,0,1,0,0,1 . double-0s start at 3

But when the strip is long enough to contain TWO 1001 sequences (ie 1001001), 
adding the (0 based) 6th patch at the right hand end adds more than one possible solution. 

See note #2 at the bottom for more detail.

*/

   prepareVariations( aStrip.size(), variations );
   System.out.println();
   System.out.println("variations " + variations.toString() );

   Integer largestValue = new Integer( 0 );
   Integer largestScheme = new Integer( -1 );

      for( int scheme = 0; scheme < variations.size(); scheme++ ) {  // for each pontential mining strip....
          int sum = 0;
          for( int cnt = 0; cnt < aStrip.size(); cnt++ ) {  // for all mineable patches
              if (verbose ) {
                 System.out.print("cnt: " + cnt + "  aStrip.get( cnt ): " + aStrip.get( cnt) );
                 System.out.print("  scheme: " + scheme + "  variations.get(scheme).get(cnt): " +  variations.get(scheme).get(cnt).toString() );
              } // if verbose...

              sum = sum + aStrip.get( cnt ) * variations.get(scheme).get(cnt);

              if (verbose ) {
                 System.out.println("  sum: " + sum );
              } // if verbose...
          } // for int cnt 
          if (sum > largestValue ) {
             largestValue = new Integer( sum );
             largestScheme = new Integer( scheme );
          } // if sum...
          yield.add( new Integer(sum) );
       } // for int scheme

       System.out.print("\nlargestValue: " + largestValue.toString() + " = " );

       boolean didPrint = false;
       for (int cnt = 0; cnt < variations.get(largestScheme).size(); cnt ++ ) {
          if ( 1 == variations.get(largestScheme).get(cnt) )  {
             if (didPrint) { 
                 System.out.print( " + " );
             } else {
                 didPrint = true; // set for next time...
             } // if didprint else...
             System.out.print( aStrip.get(cnt) + " " );
             
          } // if 1 == variations.get...
       } // for int cnt= 0...
       
       System.out.print("\n" );
       System.out.println( "largestScheme: " + largestScheme.toString() + "   " +  variations.get(largestScheme).toString() + "\n" );
       System.out.print("  aStrip: " + aStrip.toString() );
       System.out.println("  Yields: " + yield.toString() );
   } // main...


/* 
 
 */
    
    public static void prepareVariations( int stripLength, ArrayList<ArrayList<Integer>> returnVariations  ) {

        for( int start = 0; start < 2; start++ ) { 
            ArrayList<Integer> aVariation = new ArrayList<Integer>(); 
            for (int cnt = start; cnt < stripLength+start; cnt++ ) {

               int factor = ( cnt & 1 );  // that's a bitwise &...
               aVariation.add( new Integer( factor)); 
            } // for int cnt...

            //  System.out.println("aVariation " + aVariation.toString() );

            ArrayList<Integer> referenceVariation = new ArrayList<Integer>(aVariation); // make a deep copy

            returnVariations.add( aVariation );

/* slightly tricky here: I could use recursion to construct variations but I'm going to try iteration to clairfy the algorithm first.
 * We want to find 01010101010... and switch to 010010101..., Or 1010101010... and switch to 100101010101. So our insertion point
 * candidates start with 3 (0101 is 0..3) or 2 (101 is 0..2) for the even and odd based sequences. Start = 0 is Even, so if I use 3 - start,
 * I get the 3, 2 sequence I want on successive 'start' values; 
 */
/*   Next, the insertion point has to be at least one less than the length of the arraylist we start with. 
 * Imagine I had a 3 place pair of lists, 010 and 101. 3 is beyond the end of 010, but 2 is the end of 101. 
 * However, there's no gain in changing 101 to 100. It will always sum to less than 101. So the threshold of 
 * utility is when an insertion point candidate is at (size -1) -1), ie, we have 0101 and 1010. Location 3 is 
 * size - 1 for 0101 but 0100 is not worth the candle. Location 2 is (size -1)-1) for 1010 and changing 1010 
 * to 1001 may produce a worthwhile result.
 */

 /* in any event, its 5:41am, 13 hours after I was supposed to have finished, so I'm releasing this as the first version. It a more robust and openended piece of code to construct exhaustive variations. But for shorter lists, this works and works well.a */


            for( int inPoint = (3 - start); (inPoint < (stripLength - 1 )); inPoint += 2 ) {
               ArrayList<Integer> anotherVariation = new ArrayList<Integer>(referenceVariation); // deep copy, if inPoint, start, stripLength agree
   if (verbose) System.out.println("anotherVariation 1 " + anotherVariation.toString() );
               anotherVariation.add(inPoint, new Integer( 0 )); // insert at zero-based index, shift any existing content to right
   if (verbose) System.out.println("anotherVariation 2 " + anotherVariation.toString() );
               anotherVariation.trimToSize(); // so getting the size coresponds to actual content...
               int oneBasedSize = anotherVariation.size(); 
               anotherVariation.remove( oneBasedSize - 1 );               
   if (verbose) System.out.println("anotherVariation 3 " + anotherVariation.toString() );

               returnVariations.add( anotherVariation );

/* This is good for one double-zero insertion, but more than one is required for long strips. */
            } // for int inPoint...

            if (verbose) System.out.println("returnVariations " + returnVariations.toString() );

        } // for start... 

        if (verbose) System.out.println("returnVariations " + returnVariations.toString() );

    } // void prepareVariations...


} // class Q6

/* discussion, thoughts about design */

/*
Note #2: 

To carry on from the note in the middle of the code: 

 For a 5 patch set, 0,,2,,4; ,1,,3,; 0,,3,; ,1,,,4; Each added patch increases the possible variations. For short strips,
adding one patch adds one more solution, but when the strip is long enough to contain TWO 1001 sequences (ie 1001001), 
adding the (0 based) 6th patch at the right hand end adds more than one possible solution:

all positions are based on 0-based counting:

..0th.1st.2nd.3rd..4th.5th.
| 10 | 9 | 7 | 12 | 4 | 1 |  6 patches have 5, unique, saturated, solutions:

1: 0,1,0,1,0,1
2: 1,0,1,0,1,0
3: 1,0,0,1,0,1 . double-0s start at 1 (zero-based)
4: 0,1,0,0,1,0 . double-0s start at 2
5: 1,0,1,0,0,1 . double-0s start at 3

Add one more patch:
..0th.1st.2nd.3rd..4th.5th.6th.
| 10 | 9 | 7 | 12 | 4 | 1 | 5 |   7 patches have more than 6 unique, saturated, solutions:

1: 0,1,0,1,0,1,0
2: 1,0,1,0,1,0,1
3: 1,0,0,1,0,1,0 . double-0s start at 1st (zero based)
4: 0,1,0,0,1,0,1 . double-0s start at 2nd
5: 1,0,1,0,0,1,0 . double-0s start at 3rd
6: 0,1,0,1,0,0,1 . double-0s start at 4th
7: 1,0,0,1,0,0,1 . double double-0s start at 1st

Add another one more patch, get 2 more solutions:
..0th.1st.2nd.3rd..4th.5th.6th.7th.
| 10 | 9 | 7 | 12 | 4 | 1 | 5 | 2 |   8 patches have more than 7 unique, saturated, solutions:

1: 0,1,0,1,0,1,0,1
2: 1,0,1,0,1,0,1,0
3: 1,0,0,1,0,1,0,1 . double-0s start at 1st (zero based)
4: 0,1,0,0,1,0,1,0 . double-0s start at 2nd
5: 1,0,1,0,0,1,0,1 . double-0s start at 3rd
6: 0,1,0,1,0,0,1,0 . double-0s start at 4th
7: 1,0,1,0,1,0,0,1 . double-0s start at 5th
8: 1,0,0,1,0,0,1,0 . doublle double-0s start at 1st
9: 0,1,0,0,1,0,0,1 . doublle double-0s start at 2th

Add 2 more patches:
..0th.1st.2nd.3rd..4th.5th.6th..7th..8th..9th.
| 10 | 9 | 7 | 12 | 4 | 1 | 5 | 17 | 30 | 18 |   10 patches have more than 9 unique, saturated, solutions:

 1: 0,1,0,1,0,1,0,1,0,1
 2: 1,0,1,0,1,0,1,0,1,0
 3: 1,0,0,1,0,1,0,1,0,1 . double-0s start at 1st ( zero based )
 4: 0,1,0,0,1,0,1,0,1,0 . double-0s start at 2nd
 5: 1,0,1,0,0,1,0,1,0,1 . double-0s start at 3rd
 6: 0,1,0,1,0,0,1,0,1,0 . double-0s start at 4th
 7: 1,0,1,0,1,0,0,1,0,1 . double-0s start at 5th
 8: 0,1,0,1,0,1,0,0,1,0 . double-0s start at 6th
 9: 1,0,1,0,1,0,1,0,0,1 . double-0s start at 7th
10: 1,0,0,1,0,0,1,0,1,0 . 1 spaced double double 0s start at zero based 1st
11: 0,1,0,0,1,0,0,1,0,1 . 1 spaced double double 0s start at zero based 2nd
12: 1,0,1,0,0,1,0,0,1,0 . 1 spaced double double 0s start at zero based 3rd
13: 0,1,0,1,0,0,1,0,0,1 . 1 spaced double double 0s start at zero based 4th
14: 1,0,0,1,0,1,0,0,1,0 . 3 spaced double double 0s start at zero based 1st
14: 0,1,0,0,1,0,1,0,0,1 . 3 spaced double double 0s start at zero based 2nd
15:,1,0,0,1,0,0,1,0,0,1 . triple double 0s start at zero based 1st

Add 1 more patch:
..0th.1st.2nd.3rd..4th.5th.6th..7th..8th..9th.10th.
| 10 | 9 | 7 | 12 | 4 | 1 | 5 | 17 | 30 | 18 | 22 |    11 patches have more than 9 unique, saturated, solutions:

 1: 0,1,0,1,0,1,0,1,0,1,0
 2: 1,0,1,0,1,0,1,0,1,0,1
 3: 1,0,0,1,0,1,0,1,0,1,0 . double-0s start at 1st ( zero based )
 4: 0,1,0,0,1,0,1,0,1,0,1 . double-0s start at 2nd
 5: 1,0,1,0,0,1,0,1,0,1,0 . double-0s start at 3rd
 6: 0,1,0,1,0,0,1,0,1,0,1 . double-0s start at 4th
 7: 1,0,1,0,1,0,0,1,0,1,0 . double-0s start at 5th
 8: 0,1,0,1,0,1,0,0,1,0,1 . double-0s start at 6th
 9: 1,0,1,0,1,0,1,0,0,1,0 . double-0s start at 7th
10: 0,1,0,1,0,1,0,1,0,0,1 . double-0s start at 8th
11: 1,0,0,1,0,0,1,0,1,0,1 . 1 spaced double double 0s start at zero based 1st
12: 0,1,0,0,1,0,0,1,0,1,0 . 1 spaced double double 0s start at zero based 2nd
13: 1,0,1,0,0,1,0,0,1,0,1 . 1 spaced double double 0s start at zero based 3rd
14: 0,1,0,1,0,0,1,0,0,1,0 . 1 spaced double double 0s start at zero based 4th
15: 1,0,1,0,1,0,0,1,0,0,1 . 1 spaced double double 0s start at zero based 5th
16: 1,0,0,1,0,1,0,0,1,0,1 . 3 spaced double double 0s start at zero based 1st
17: 0,1,0,0,1,0,1,0,0,1,0 . 3 spaced double double 0s start at zero based 2nd
18: 1,0,1,0,0,1,0,1,0,0,1 . 3 spaced double double 0s start at zero based 3rd
19: 1,0,0,1,0,1,0,1,0,0,1 . 5 spaced double double 0s start at zero based 1st
20:,1,0,0,1,0,0,1,0,0,1,0 . triple double 0s start at zero based 1st
21: 0,1,0,0,1,0,0,1,0,0,1 . triple double 0s start at zero based 2nd


num of columns: . 04, 05, 06, 07, 08, 09, 10, 11, 12, 13, 
solutions: ie f(x):
0xAAA,0x555 . . :  2,  2,  2,  2,  2,  2,  2,  2,  2,  2, 
double-0s . . . :  1,  2,  3,  4,  5,  6,  7,  8,  9, 10,         blob size 3 - 001
double double 0s:  0,  0,  0,  1,  2,  3,  4,  5,  6,  7, (1 space) blob size 6 - 001001
double double 0s:  0,  0,  0,  0,  0,  1,  2,  3,  4,  5, (3 space) blob size 8 - 00101001
double double 0s:  0,  0,  0,  0,  0,  0,  0,  1,  2,  3, (5 space) blob size 10- 0010101001
double double 0s:  0,  0,  0,  0,  0,  0,  0,  0,  0,  1, (7 space) blob size 12- 001010101001
triple double 0s:  0,  0,  0,  0,  0,  0,  1,  2,  3,  4, (1,1 space) blob size 9 - 001001001
triple double 0s:  0,  0,  0,  0,  0,  0,  0,  0,  1,  2, (1,3 space) blob size 11 - 00100101001
triple double 0s:  0,  0,  0,  0,  0,  0,  0,  0,  1,  2, (3,1 space) blob size 11 - 00101001001
triple double 0s:  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, (3,3 space) blob size 13 - 0010100101001
quadruple 00s:     0,  0,  0,  0,  0,  0,  0,  0,  0,  1, (1 space)   blob size 12 -  001001001001

sum: . . . . . .   3,  4,  5,  7,  9, 12, 16, 21, 28, 37, 
delta: . . . . .   1,  1,  1,  2,  2,  3,  4,  5,  7,  9

What makes this unsettling is that triple double 0s can have asymetric spacings-, as can quadruple, blah blah blah. The solution space is expanding remarkably fast.

bits,     values,     00, never          interesting/
                      11 or 111  delta  total values:
4           16        3          1      1.875 e-1
5           32        4          1      1.25 e-1
6           64        5          1      7.8125 e-2
7          128        7          2      5.46875 e-2  
8          256        9          2      3.515625 e-2
9          512       12          3      2.34375 e-2 
10        1024       16          4      1.5625 e-2
11        2048       21          5      1.025390625 e-2
12        4096       28          7      6.8359375 e-3
13        8192       37          9      4.5166015625 e-3



I model different mining schemes with ArrayLists of integers. The same size as the ArrayList of patch yield values.
In the variations ArrayLists, a 1 means mine it, a 0 means don't. Step through the aStrip and variations ArrayLists in parallel.
multiply the value in aStrip by the value in variations, then add the result to the sum for a given mining scheme. 

On the other hand, I don't like how the problem has started blowing up in complexity. Not one bit. 





*/
/* 
   Note #1: Most important. Most possible solutions are forbidden or unsaturated:
   So rather than eliminating unsuitable patterns, concentrate on genderating suitable ones.

   expressing Mining patterns as 1 0 1 0 0 1 sets of 0 and 1, where 1 means mine, and 0 means skip:
  
   pick an optimum set out of aStrip by the rule of having to skip one or more elements after each taken, 
   so if we had 10,9,7,12 we could take 10,,7, totaling 17, or ,,9,,12 totalling 21. But 10,,,12 is 22, biggest.

   If it were 10,9,7,12,8, then 10,,7,,8 would be bigger, 25, even bigger than 10,,,12,.
   but 10,9,7,12,8,4 would have 10,,,12,4, 26, even bigger than 10,,7,,8,. or ,9,,12,,4.

   There's a VERY sparse B tree that goes from
   101010 to 010101 ( 0x2A and 0x15 )
and includes [1 0 0 1 0 1 ], [1 0 1 0 0 1], [0 1 0 0 1 0]
Anything with a "1 1" is forbidden, 
Anything which starts or ends with "0 0" is unsaturated- might as well start "1 0" or "0 1",  end "0 1" or "1 0".
Anything which contains "0 0 0" is unsaturated. "0 0 0" is legal but "0 1 0" will always be larger

Of 32 possible values, 00000 to 111111:
00000 - not forbidden but meaningless
00001 - not saturated: (same as 10101 or 01001)
00010 - not saturated: (same as 01010 or 10010)
00011 - forbidden!
00100 - not saturated, same as 10101, 10100, 00101)
00101 - not saturated, same as 10101
00110 - forbidden!
00111 - forbidden!
01000 - not saturated: (same as 01010 or 01001)
01001 - Saturated 1
01010 - Saturated 2
01011 - forbidden!
01100 - forbidden!
01101 - forbidden!
01110 - forbidden!
01111 - forbidden!
10000 - not saturated: sameas 10101 or 10010
10001 - not saturated: (same as 10101)
10010 - Saturated 3
10011 - forbidden!
10100 - not saturated, same as 10101)
10101 - Saturated 4
10110 - forbidden!
10111 - forbidden!
11000 - forbidden!
11001 - forbidden!
11010 - forbidden!
11011 - forbidden!
11100 - forbidden!
11101 - forbidden!
11110 - forbidden!
11111 - forbidden!

19 are forbidden, 1 meaningless, 8 are not saturated, 4 are saturated. 32 total.
Saturated: 10101; 01010; 10010; and 01001

Adding another bit gives:

40 are forbidden, 1 meaningless, 18 are not saturated, 5 are saturated. 64 total.

Saturated:  010010, 010101, 100101, 101001, 101010

so start with 101010 and 010101 for the length of the data supplied.
Scan, and for every *010 substitue *001 and store the variation.

000000 - not forbidden but meaningless
000001 - not saturated: (same as 010101 or 001001, etc)
000010 - not saturated: (same as 001010 or 010010, etc)
000011 - forbidden!
000100 - not saturated, same as 010101, 010100, 000101)
000101 - not saturated, same as 010101
000110 - forbidden!
000111 - forbidden!
001000 - not saturated: (same as 001010 or 001001)
001001 - not Saturated! Same as 101001
001010 - not Saturated! Same as 101010
001011 - forbidden!
001100 - forbidden!
001101 - forbidden!
001110 - forbidden!
001111 - forbidden!
010000 - not saturated: sameas 010101 or 010010
010001 - not saturated: (same as 010101)
010010 - Saturated 1
010011 - forbidden!
000100 - not saturated, same as 010101)
010101 - Saturated 2
010110 - forbidden!
010111 - forbidden!
011000 - forbidden!
011001 - forbidden!
011010 - forbidden!
011011 - forbidden!
011100 - forbidden!
011101 - forbidden!
011110 - forbidden!
011111 - forbidden!
100000 - not saturated same as 101010, etc. 
100001 - not saturated: (same as 10101 or 01001)
100010 - not saturated: (same as 01010 or 10010)
100011 - forbidden!
100100 - not saturated, same as 10101, 10100, 00101)
100101 - Saturated 3
100110 - forbidden!
100111 - forbidden!
101000 - not saturated: (same as 01010 or 01001)
101001 - Saturated 4
101010 - Saturated 5
101011 - forbidden!
101100 - forbidden!
101101 - forbidden!
101110 - forbidden!
101111 - forbidden!
110000 - not saturated: sameas 10101 or 10010
110001 - not saturated: (same as 10101)
110010 - forbidden!
110011 - forbidden!
110100 - not saturated, same as 10101)
110101 - forbidden!
110110 - forbidden!
110111 - forbidden!
111000 - forbidden!
111001 - forbidden!
111010 - forbidden!
111011 - forbidden!
111100 - forbidden!
111101 - forbidden!
111110 - forbidden!
111111 - forbidden!

*/




















