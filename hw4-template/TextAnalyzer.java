import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.HashSet;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Tuple> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        
        private Text neighbor = new Text();
        private Tuple tuple = new Tuple();

        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of your mapper function
            String line = value.toString().toLowerCase();
            line = line.replaceAll("[^A-Za-z]", " ");

            StringTokenizer tokenizer = new StringTokenizer(line);
            HashSet<String> duplicates = new HashSet<>();
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken());
                if (!duplicates.contains(word.toString())) {
                    duplicates.add(word.toString());
                    StringTokenizer tokenizer_neighbors = new StringTokenizer(line);
                    while (tokenizer_neighbors.hasMoreTokens()) {
                        neighbor.set(tokenizer_neighbors.nextToken());
                        if (!word.equals(neighbor)) {
                            tuple.set(neighbor, one);
                            context.write(word, tuple);
                        } 
                    }
                } 
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<Text, Tuple, Text, Tuple> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
        	LinkedHashMap<String, Integer> set = new LinkedHashMap<String, Integer>();
            for (Tuple t: tuples) {
            	String neighbor = t.getValue().toString();
            	if (set.containsKey(neighbor)) {
            		set.put(neighbor, set.get(neighbor) + t.getCount().get());
            	}
            	else {
            		set.put(neighbor, t.getCount().get());
            	}
            	
            }
            
            for (Map.Entry<String, Integer> mapElement : set.entrySet()) {
            	String value = mapElement.getKey();
            	Integer count = mapElement.getValue();
            	context.write(key, new Tuple(new Text(value), new IntWritable(count)));
            }
            
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Tuple, Text, Text> {
        private final static Text emptyText = new Text("");

        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
        	
        	LinkedHashMap<String, Integer> set = new LinkedHashMap<String, Integer>();
            for (Tuple t: queryTuples) {
            	String neighbor = t.getValue().toString();
            	if (set.containsKey(neighbor)) {
            		set.put(neighbor, set.get(neighbor) + t.getCount().get());
            	}
            	else {
            		set.put(neighbor, t.getCount().get());
            	}
            	
            }
            
            // Implementation of you reducer function
            for (Map.Entry<String, Integer> mapElement : set.entrySet()) {
            	String neighbor = mapElement.getKey();
            	Integer count = mapElement.getValue();
            	Text value = new Text();
            	String weight = count.toString();
            	value.set(" " + neighbor + " " + weight);
            	context.write(key, value);
            }

            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        Job job = new Job(conf, "dmb4377_ji4399"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        
        // set local combiner class
        job.setCombinerClass(TextCombiner.class);
        // set reducer class        
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Tuple.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    // You may define sub-classes here. Example:
    // public static class MyClass {
    //
    // }
}
