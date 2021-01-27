//UT-EID=dmb4377
//UT-EID=ji4399


import java.util.*;
import java.util.concurrent.*;


public class PMerge{
  public static void parallelMerge(int[] A, int[] B, int[]C, int numThreads){
    // TODO: Implement your parallel merge function
	int size;
	if (A.length > B.length) size = A.length / numThreads;
	else size = B.length / numThreads;
	final ForkJoinPool forkJoinPool = 
			  new ForkJoinPool(numThreads);
	int maxA = A.length - 1; int maxB = B.length - 1; int maxC = C.length - 1;
	for (int i = 0; i < numThreads; i++) {
		int x; int y;
		if (A.length > B.length) {
			x = A.length - size;
		    y = binarySearch(B, A[A.length - size]);
		    merge(x, y, maxA, maxB, A, B, C, maxC);
		    maxA = x - 1; maxB = y - 1;
		    maxC -= (maxB- y) + (maxA - x);
		}
		
		    
		
		
	}
  }
  
  static int binarySearch(int[] A, int x) {
	  int min = 0; int max = A.length - 1;
	  int middle = (min + max) / 2;
	  while (min < max) {
		  if (A[middle] < x) {
			  min = middle + 1;
			  middle = (min + max) / 2;
		  }
		  else {
			  max = middle - 1;
			  middle = (min + max) / 2;
		  }
	  }
	  return middle;
  }
  
  static void merge(int minX, int minY, int maxX, int maxY, int[] A, int[] B, int[] C, int c) {
	  int x = maxX;
	  int y = maxY;
	  for (int i = 0; i < (maxY - minY) + (maxX - minX); i++) {
		  if (A[x] > B[y]){
			  C[c] = A[x];
			  --x;
			  --c;
		  }
		  else{
			  C[c] = B[y];
			  --y;
			  --c;
		  }
	  }
  }
  
}

