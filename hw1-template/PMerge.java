//UT-EID=dmb4377
//UT-EID=ji4399


import java.util.*;
import java.util.concurrent.*;


public class PMerge extends RecursiveAction{
	int[] A;
	int[] B;
	int[] C;
	int numThreads;
	int aBegin;
	int aEnd;
	int bBegin;
	int bEnd;
	
	public PMerge(int[] A, int[] B, int[] C, int aBegin, int aEnd, int bBegin, int bEnd) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.aBegin = aBegin;
		this.aEnd = aEnd;
		this.bBegin = bBegin;
		this.bEnd = bEnd;
	}
	
	public static void parallelMerge(int[] A, int[] B, int[]C, int numThreads){
	  
		ForkJoinPool pool = new ForkJoinPool(numThreads);
		PMerge t1 = new PMerge(A, B, C, 0, A.length - 1, 0, B.length - 1);
		pool.invoke(t1);
		pool.shutdown();
	}
  
	@Override
	protected void compute() {
		int size = aEnd - aBegin + bEnd - bBegin + 2;
		if (aEnd - aBegin <= 1) {
			merge();
			return;
		}
		int[] median = findMedian(0, size / 2);
		PMerge t1 = new PMerge(A, B, C, aBegin, aBegin + median[0] - 1, bBegin, bBegin + median[1] - 1);
		PMerge t2 = new PMerge(A, B, C, aBegin + median[0], aEnd, bBegin + median[1], bEnd);
		invokeAll(t1, t2);
	}	

	void merge() {
		//sequential merge for small array chunks
		while(aBegin <= aEnd && bBegin <= bEnd) {
			if (A[aBegin] < B[bBegin]) {
				C[C.length - (aBegin + bBegin) - 1] = A[aBegin];
				++aBegin;
			}
			else {
				C[C.length - (aBegin + bBegin) - 1] = B[bBegin];
				++bBegin;
			}
		}
		for (int i = aBegin; i <= aEnd; i++)
			C[C.length - (i + bBegin) - 1] = A[i];
		for (int i = bBegin; i <= bEnd; i++)
			C[C.length - (i + aBegin) - 1] = B[i];
	}

	int[] findMedian(int low, int high) {
		int size = aEnd - aBegin + bEnd - bBegin + 2;
		if (aEnd < aBegin) return new int[]{0, (size / 2)};
		if (bEnd < bBegin) return new int[]{(size / 2), 0};
	
		int aElements = ((aEnd - aBegin + 1) / 2);
		if (aElements < 1){ 
			return new int[]{0, size / 2};
		}
		return new int[]{aElements, binarySearch(B, bBegin, bEnd, A[aBegin + aElements - 1]) - bBegin + 1};
	}

	int binarySearch(int[] arr, int first, int last, int value) {
		//returns the last index in arr that is less than value
		int i = (first + last) / 2;
		if (arr[i] <= value) {
			if (i >= last)
				return i;
			if (arr[i + 1] > value)
				return i;
			else
				return binarySearch(arr, i + 1, last, value);
		}
		else {
			if (i <= first)
				return i - 1;
			else 
				return binarySearch(arr, first, i - 1, value);
		}
	}
}
