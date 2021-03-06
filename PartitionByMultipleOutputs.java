
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.json.JSONException;
import org.json.JSONObject;

public class PartitionByMultipleOutputs extends Configured implements Tool
{	
        public static class MultipleOutputsMapper extends Mapper<LongWritable, Text, Text, Text> 
	{			
		public void map(LongWritable mkey, Text mvalue, Context context) throws IOException, InterruptedException
                {
              		    try
                      {
	               JSONObject jsonObj = new JSONObject(mvalue.toString());
		               //parse the input data with JSONObject	
	               String articles = (String)jsonObj.get("articles");
//	               String state = (String)jsonObj.get("state");
//	               String city = (String)jsonObj.get("city");
//	               String street = (String)jsonObj.get("street");
//	               String zip = (String)jsonObj.get("zip");
//	               
	               StringBuilder key = new StringBuilder();
	               key.append(articles);
//	               key.append("/");
//	               key.append(state);
//	               key.append("/");
//	               key.append(city);
//	               key.append("/");
//	               key.append(street);
//	               key.append("/");
//	               key.append(zip);
	               //emitting directory structure as key and input record as value.
	               context.write(new Text(key.toString()), mvalue);	          			    
                       }
                       catch (Exception e) {
				e.printStackTrace();
			}
		     }
		}
				
	public static class MultipleOutputsReducer extends Reducer<Text, Text ,NullWritable, Text> 
	{			
		private MultipleOutputs<NullWritable,Text> multipleOutputs;
		public void setup(Context context) throws IOException, InterruptedException
		{
			multipleOutputs = new MultipleOutputs<NullWritable, Text>(context);
		}
		public void reduce(Text rkey, Iterable<Text> rvalue, Context context) throws IOException, InterruptedException
		{							
	           for(Text value : rvalue) {				              		             		               
              	      multipleOutputs.write(NullWritable.get(), value, rkey.toString());		          		           }		           
		}											
		public void cleanup(Context context) throws IOException, InterruptedException
		{
		      multipleOutputs.close();
		}		
	}	
		
	public int run(String[] args) throws Exception 
	{
//		Configuration conf = new Configuration();
		Configuration conf = getConf();
		
		JobConf job = new JobConf(conf, PartitionByMultipleOutputs.class);
		
		Path in = new Path(args[0]);
        Path out = new Path(args[1]);
        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);
        
        job.setJobName("PartitionByMultipleOutputs");
        job.setMapperClass(MultipleOutputsMapper.class);
        job.setReducerClass(MultipleOutputsReducer.class);
        
        job.setInputFormat(KeyValueTextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.set("key.value.separator.in.input.line", ",");
        
        JobClient.runJob(job);
        
        return 0;
		//		Job job = new Job(conf, "PartitionByMultipleOutputs");		
//		job.setJarByClass(PartitionByMultipleOutputs.class);
//		
//		job.setMapperClass(MultipleOutputsMapper.class);
//		job.setReducerClass(MultipleOutputsReducer.class);
//
//		job.setMapOutputKeyClass(Text.class);
//		job.setMapOutputValueClass(Text.class);
//
//		job.setOutputKeyClass(NullWritable.class);
//		job.setOutputValueClass(Text.class);
//		job.setOutputFormatClass(TextOutputFormat.class);  
//		  
		job.getConfiguration().set("mapred.child.java.opts","-Xmx2048m");
		job.getConfiguration().setInt("mapreduce.map.memory.mb",2048);
		job.getConfiguration().setInt("mapreduce.reduce.memory.mb",2048);
		job.getConfiguration().set("mapreduce.map.java.opts","-Xmx2048m");
		job.getConfiguration().set("mapreduce.reduce.java.opts","-Xmx2048m");
		job.getConfiguration().setBoolean("mapreduce.reduce.speculative", true);

//		job.setNumReduceTasks(2);
//		FileInputFormat.addInputPath(job, new Path(inputpath));
//		FileOutputFormat.setOutputPath(job, new Path(outputpath));
//				
		if(fs.exists(new Path(outputpath)))
		{
			fs.delete(new Path(outputpath), true);
		}
		
		job.waitForCompletion(true);			
		return 0;	   
	}
	public static void main(String[] args) throws Exception 
	{
		int exitCode = ToolRunner.run(new PartitionByMultipleOutputs(), args);
		    System.exit(exitCode);
        }
}
