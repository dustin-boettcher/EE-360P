import java.io.DataOutput;
import java.io.DataInput;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import java.io.IOException;

public class Tuple implements WritableComparable{

	private Text word1;
        private Text word2;
	private IntWritable count;

	Tuple(){
		set(new Text(), new Text(), new IntWritable());
	}

	Tuple(Text v, Text w, IntWritable c){
		set(v,w,c);
	}

	@Override
	public void write(DataOutput out) throws IOException{
		word1.write(out);
                word2.write(out);
		count.write(out);
	}

	public void set(Text t, Text w, IntWritable c){
		this.word1=t;
                this.word2=w;
		this.count=c;
	}

	@Override
	public void readFields(DataInput input) throws IOException {
		word1.readFields(input);
                word2.readFields(input);
		count.readFields(input);
	}
	
	@Override
	public int compareTo(Object o){
		Tuple that=(Tuple) o;
		return this.word1.compareTo(that.getWord1()) && this.word2.compareTo(that.getWord2());
	}

	
	public int hashCode(){
		return word1.hashCode()*10000+word2.hashCode()*100+count.hashCode();
	}

	public String toString(){
		return word1+" " + word2+" "+count;
	}

	public Text getWord1(){
		return this.word1;
	}

        public Text getWord2(){
                return this.word2;
        }

	public IntWritable getCount(){
		return this.count;
	}

}