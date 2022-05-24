# 图像特征提取与检索

### 任务

​	使用颜色直方图或者bof算法来提取图像特征，在corel数据集(10*100张图片)上实现以图搜图，即输入数据集中某一张图，在剩下的999张图里搜索最邻近的10张图。尽量避免调库，请按照实验原理自行编写代码，调库酌情减分。

## BOF算法

​	 Bag-of-Features模型仿照文本检索领域的Bag-of-Words方法，把**每幅图像**描述为一个**局部区域/关键点(Patches/Key Points)**特征的无序集合。

​	使用某种聚类算法(如K-means)将局部特征进行聚类，每个聚类中心被看作是词典中的一个视觉词汇(Visual Word)，相当于文本检索中的词，视觉词汇由聚类中心对应特征形成的码字(code word)来表示（可看当为一种特征量化过程，可理解为：码字表示聚类中心的特征矢量，如该类的平均矢量等）。

​	所有视觉词汇形成一个视觉词典(Visual Vocabulary)，对应一个码书(code book)（可理解为：码书是所有聚类中心特征矢量的集合），即码字的集合，词典中所含词的个数反映了词典的大小。

### 算法步骤

**一、构建视觉词典（Visual World）**

1. 特征提取

​	利用SIFT算法提取图像的特征点；

2. 特征聚类

​	由于特征点的数目过多，利用Kmeans聚类算法将特征点分为K类，**每一类称为一个码字（code word）**；

3. 构建词典

​	所有类（词）构成一个词典；

**二、量化（Feature Pooling）**

​	对于每个输入图像的特征，根据视觉词典进行量化，量化的过程是将该特征映射到距离其最接近的视觉单词，并实现计数。

1. 特征提取

​	利用SIFT算法提取图像的特征点；

2. 查找对应码字
3. 构建图像特征词表(原始BOF特征)

​	将字典内每个词出现频率构成这个训练图像特征向量（显然是K维向量）（初步的无权BOF（直方图向量））

4. 引入TF-IDF

​	通过tf-idf对频数表加上权重，生成最终的bof。

**三、检索匹配的图像**

​	计算输入图像的BOF直方图, 在数据库中查找 k 个最近邻的图像;


### TF-IDF

​	**TF-IDF(Term Frequency-Inverse Document Frequency, 词频-逆文件频率)**是一种用于资讯检索与资讯探勘的常用加权技术。TF-IDF是一种统计方法，用以评估一字词对于一个文件集或一个语料库中的其中一份文件的重要程度。字词的重要性随着它在文件中出现的次数成正比增加，但同时会随着它在语料库中出现的频率成反比下降。

**TF的主要思想**:如果某个关键词在一篇文章中出现的频率高，说明该词语能够表征文章的内容，该关键词在其它文章中很少出现，则认为此词语具有很好的类别区分度，对分类有很大的贡献。

**IDF的主要思想**:如果文件数据库中包含词语A的文件越少，则IDF越大，则说明词语A具有很好的类别区分能力。

#### **TF**

​	**TF(Term Frequency, 词频)**表示词条在文本中出现的频率，这个数字通常会被归一化(一般是词频除以文章总词数), 以防止它偏向长的文件（同一个词语在长文件里可能会比短文件有更高的词频，而不管该词语重要与否）。

#### **IDF**

​	表示关键词的普遍程度。如果包含词条 i 的文档越少， IDF越大，则说明该词条具有很好的类别区分能力。某一特定词语的IDF，可以由总文件数目除以包含该词语之文件的数目，再将得到的商取对数得到

### SIFT

```python
"""
SIFT: 尺度不变特征提取
  步骤：
  1.DOG尺度空间的建立
    多分辨率金字塔
    差分金字塔
  2.尺度空间中提取关键点 
    与周围的26个点进行比较->局部关键点
    对离散的局部极值点进行线性拟合->找到真正的极值点
  3.生成特征描述子--128维
  4.特征点匹配
"""
```

## 分析

### **SIFT特征提取**

```python
# 创建sift特征提取器
sift = cv.xfeatures2d.SIFT_create()
```

循环提取10个文件夹内各100张图片的SIFT特征

```python
for i in range(10):
    # 获取文件夹路径
    path = path + str(i) + '/'
    for j in range(100):
        # 拼接图片的路径
        k = i * 100 + j
        img_path = path + str(k) + '.jpg'
        # 读入图片
        orig_img = cv.imread(img_path)
        # 调用sift.detectAndCompute()方法提取特征
        # 第二个参数:mask(掩码)，屏蔽不需要提取特征点的区域,设置为None表示检测所有
        keypoint, vec = sift.detectAndCompute(orig_img, None)
        # print(vec.shape)   # (n,128）

        # 把提取的特征加入all_sift中
        vec = list(vec)
        all_sift += vec
        # 添加每一个特征点的标签
        belong += len(vec) * [k]

        print(k)  # 图片编号
        print(len(vec))  # 本次提取的特征点数
        print(len(all_sift))  # 当前总特征点数
        print("--------------------------------")

    path = "corel/"
```

​	图片的每一个特征点由128维的特征向量来描述；

将提取的特征保存到csv文件中

```python
df = pd.DataFrame(all_sift)
df.insert(loc=0, column='belong', value=belong)
out_path = '_sift_.csv'
df.to_csv(out_path, sep=',', index=False, header=True)
```

### Kmeans聚类建立视觉词典

读取sift特征

```python
# 读入
data_csv = '_sift_.csv'
df = pd.read_csv(data_csv)
# 获取编号
belong = df['belong']  # <class 'pandas.core.series.Series'>
belong = pd.DataFrame(belong)  # <class 'pandas.core.frame.DataFrame'>
# 获取特征向量
df = df.drop(columns='belong')
```

进行kmeans聚类

```python
start = time.perf_counter()
kmeans = KMeans(n_clusters=500, random_state=10).fit(df)
end = time.perf_counter()
print(f"kmeans运行时间：{end-start}")
```

保存视觉词典

```python
# 获取聚类标签
labels = kmeans.labels_
labels = labels.tolist()
belong.insert(loc=0, column='class', value=labels)

# 输出并保存到csv文件中
# 该文件第1列表示该特征所属的簇
# 该文件第2列表示该特征对应的图片编号
outputpath = 'out_1000.csv'
belong.to_csv(outputpath, sep=',', index=False, header=True)
```

​	对于Kmeas中簇类`n_clusters`的选择是很关键的，聚类簇数过小可能导致欠拟合，聚类簇数过多可能导致过拟合。

​	`n_clusters=100`时的效果不尽人意，`n_clusters=500` `n_clusters=1000` 时效果有显著的提升，但是训练聚类模型的时间也是成倍提升。

`n_clusters=2000`时的效果也不是很理想。

​	尝试使用pcv降维后再进行特征聚类，会大大减少训练时间，但是效果也会受到影响。





### 根据视觉词典进行图片匹配

量化视觉词典

```python
# 获取模型
model = joblib.load('./1000/model_1000.pickle')

# 读入-每个特征点聚类结果和所属图片编号
df = pd.read_csv('./1000/CW_1000.csv')

#  DataFrame => numpy.ndarray
classf = np.array(df)
# picture*clusters: 1000*1000
# 统计所有的词频
count = np.zeros((1000, 1000))

for i in range(len(classf)):
    count[classf[i][1]][classf[i][0]] += 1
```

读取一张图片并搜索匹配与之最接近的10张

```python
img = cv2.imread("corel/8/826.jpg")

# 创建sift特征提取器
sift = cv2.xfeatures2d.SIFT_create()
# 提取该图片的特征
keypoint, vector = sift.detectAndCompute(img, None)
# print(vector.shape)  # (454, 128)

# 预测每一个特征向量的所在簇的编号
vector = vector.astype(float)
labels = model.predict(vector)
# print(labels.shape)

# 统计该图片的词频
input_count = np.zeros(500)
for i in range(len(labels)):
    input_count[labels[i]] += 1

# 相似度比较: 余弦相似度
similar = []
for i in range(1000):
    similar.append(cos(input_count, count[i]))

# 选取与之最大相似度的10张图片的编号
img_no = np.argsort(similar)[-10:]


for idx, no in enumerate(img_no):
    print(no)  # 图片编号
    print(similar[no])  # 余弦相似度

    src_path = "corel/" + str(no//100) + '/' + str(no) + '.jpg'
    img = cv2.imread(src_path)
    plt.subplot(4, 3, idx+1)
    plt.xticks([])
    plt.yticks([])
    plt.imshow(img)

plt.show()
```

## 结果

第三行为待匹配图片，前两行为最邻近的10张图片

**n_clusters = 100**



![image-20220523132245707](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231322860.png)

 ![image-20220523132310439](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231323520.png)

**n_clusters = 500**





![image-20220523132358706](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231323788.png)

![image-20220523132415616](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231324704.png)

![image-20220523132459396](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231324479.png)

**n_clusters = 1000**



![image-20220522182424284](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205221824347.png)

![image-20220522182500682](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205221825743.png)

![image-20220522182547074](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205221825158.png)

**n_clusters = 2000**

![image-20220523132607313](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231326398.png)

当聚类簇数为2000时会导致过拟合，任意读入一张图片进行匹配最高相似度仅能达到0.2左右（每张图片由两行信息，第一行是图片序号，第二行是它与待匹配图片的余弦相似度）

![image-20220523132709901](https://cdn.jsdelivr.net/gh/mistletoe1222/img/imgs/202205231327962.png)

## PS

scipy 1.8.0+版本会导致未知原因的错误，建议降至1.7.0使用；



