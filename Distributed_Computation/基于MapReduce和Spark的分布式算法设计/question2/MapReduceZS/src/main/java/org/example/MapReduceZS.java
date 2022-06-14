package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;


public class MapReduceZS {
    //自定义的mapper，继承org.apache.hadoop.mapreduce.Mapper
    public static class MyMapper extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>{//输入的key，value；输出的key，value类型
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {
            //分割数据，左列为child，右列是parent
            String child = value.toString().split(",")[0];
            String parent = value.toString().split(",")[1];

            //产生正序与逆序的key-value对，同时写入context
            context.write(new Text(child), new Text("-" + parent));
            context.write(new Text(parent), new Text("+" + child));
            //Tim,Andy ->（key,value）: (Tim,-Andy)  (Andy,+Tim)
            //Andy, Joseph -> (key,value): (Andy,-Joseph)  (Joseph,+Andy)
        }
    }

    public static class MyReducer extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text k2, Iterable<Text> v2s,
                              Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            ArrayList<Text> grandparent = new ArrayList<>();
            ArrayList<Text> grandchild = new ArrayList<>();

            for (Text v2:v2s){

                String s = v2.toString();

                if (s.startsWith("-")){//startsWith()方法用来判断当前字符串是否是以另外一个给定的子字符串“开头”的，根据判断结果返回 true 或 false。
                    grandparent.add(new Text(s.substring(1))); //截取字符串，从序1开始（包含1）
                } else {
                    grandchild.add(new Text(s.substring(1)));
                }
            }
            //对于key代表的人物，至少有一个子代和一个父辈才会被写入
            for (int i = 0; i < grandchild.size(); i++ ){
                for (int j = 0; j < grandparent.size(); j++){
                    context.write(grandchild.get(i), grandparent.get(j));
                }
            }
        }
    }

    //客户端代码，写完交给ResourceManager框架去执行
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, MapReduceZS.class.getSimpleName());
        //打成jar执行
        job.setJarByClass(MapReduceZS.class);

        //数据在哪里？
        FileInputFormat.setInputPaths(job, args[0]);
        //使用哪个mapper处理输入的数据？
        job.setMapperClass(MyMapper.class);
        //map输出的数据类型是什么？
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        //使用哪个reducer处理输入的数据？
        job.setReducerClass(MyReducer.class);
        //reduce输出的数据类型是什么？
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //数据输出到哪里？
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //交给yarn去执行，直到执行结束才退出本程序
        job.waitForCompletion(true);
    }
}
