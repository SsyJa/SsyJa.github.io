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
import java.util.Arrays;
import java.util.List;

public class MapReduceStuAvg {
    //自定义的mapper，继承org.apache.hadoop.mapreduce.Mapper
    public static class MyMapper extends org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text> {//输入的key，value；输出的key，value类型
        Text KeyOut = new Text();
        Text ValueOut = new Text();

        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {

            String line = value.toString();
            String[] splited = line.split(",");
            //通过判断课程类型，来过滤掉选修的数据
            if (splited[3].equals("必修")) {
                KeyOut.set(splited[1]); //name
                ValueOut.set(splited[4]); //score
                context.write(KeyOut, ValueOut);
            }
        }
    }

    public static class MyReducer extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text> {
        Text ValueOut = new Text();
        List<Integer> scoreList = new ArrayList<>();

        @Override
        protected void reduce(Text k2, Iterable<Text> v2s,
                              Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            scoreList.clear();
            for (Text v2 : v2s) {
                scoreList.add(Integer.valueOf(v2.toString()));
            }

            int sum_score = 0;
            for (int score : scoreList) {
                sum_score += score;
            }

            double avg_score = sum_score * 1D / scoreList.size();

            ValueOut.set(String.valueOf(avg_score));
            context.write(k2, ValueOut);
        }
    }

    //客户端代码，写完交给ResourceManager框架去执行
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, org.example.MapReduceStuAvg.class.getSimpleName());
        //打成jar执行
        job.setJarByClass(org.example.MapReduceStuAvg.class);

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