from pyspark import SparkConf, SparkContext


def map_func1(x):
    s = x.split(",")
    return s[1], int(s[4]) #name score


def map_func2(x):
    if 90 <= x <= 100:
        return "90~100", 1
    if 80 <= x <= 89:
        return "80~89", 1
    if 70 <= x <= 79:
        return "70~79", 1
    if 60 <= x <= 69:
        return "60~69", 1
    if x < 60:
        return "<60", 1

# 配置信息
conf = SparkConf().setMaster("local").setAppName("avgcount")
sc = SparkContext(conf=conf)

# 输入数据集
textData = sc.textFile("/grades.txt") # RDD创建算子
# splitData = textData.flatMap(lambda line: line.split(","))
lines = textData.filter(lambda line: "必修" in line).map(lambda x: map_func1(x)) # RDD转换算子：filter

avgData = lines.mapValues(lambda x: (x, 1)).reduceByKey(lambda x, y: (x[0] + y[0], x[1] + y[1])).mapValues(
    lambda x: int(x[0] / x[1]))  #mapValues仅对k-v对的value进行操作

avgData.saveAsTextFile("result1")

fData = avgData.map(lambda x: map_func2(x[1])).reduceByKey(lambda x, y: (x + y))

fData.saveAsTextFile("result2")
