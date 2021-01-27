//UT-EID=dmb4377


import java.util.*;
import java.util.concurrent.*;

public class PSort extends RecursiveAction{
  int[] A; 
  int begin; 
  int end;
 
  public PSort(int[] A, int begin, int end) {
	  this.A = A; this.begin = begin; this.end = end;
  }
  
  public static void parallelSort(int[] A, int begin, int end){
	  final ForkJoinPool forkJoinPool = 
			  new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
			forkJoinPool.invoke(new PSort(A, begin, end));
  }
  
  void insertSort(int[] A, int begin, int end) {
	  for (int i = begin + 1; i < end; i++) {
		  int j = i - 1;
		  int val = A[i];
		  while (j >= 0 && val < A[j]) {
			  A[j + 1] = A[j];
			  A[j] = val;
			  --j;
		  }
	  }
  }
  
  void swap(int[] A, int a, int b) {
	  int temp = A[a];
	  A[a] = A[b];
	  A[b] = temp;
  }

  @Override
  protected void compute() { 
	//insertion sort for small arrays
	if (end - begin < 17){	
		insertSort(A, begin, end);
		return;
	}

	//take median of begin, middle, end as pivot
	if (A[begin] > A[end - 1])
		swap(A, begin, end - 1);
	if (A[(begin + end) / 2] < A[begin])
		swap(A, begin, (begin + end) / 2);
	if (A[(begin + end) / 2] > A[end - 1])
		swap(A, end - 1, (begin + end) / 2);
	swap(A, end - 2, (begin + end) / 2);
	int pivot = A[end - 2];
	
	int high = end - 3; //index to swap on right side
	while(A[high] > pivot)
		--high;
	
	for (int i = begin + 1; i <= high; i++){
		if (A[i] > pivot) {
			swap(A, i, high);
			while(A[high] > pivot)
			--high;
		}
	}
	swap(A, high + 1, end - 2); //pivot to correct index
	PSort t1 = new PSort(A, begin, high + 1);
	PSort t2 = new PSort(A, high + 2, end);

	invokeAll(t1, t2);
  }
}
