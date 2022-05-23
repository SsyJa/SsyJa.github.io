#! user/bin/python3.8
# *- coding:utf-8 -*-

import joblib
import pandas as pd
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
import time

# 读入
data_csv = '_sift_.csv'
df = pd.read_csv(data_csv)
# 获取编号
belong = df['belong']  # <class 'pandas.core.series.Series'>
belong = pd.DataFrame(belong)  # <class 'pandas.core.frame.DataFrame'>
# 获取特征向量
df = df.drop(columns='belong')

# 使用PCA降维
# pca = PCA(n_components=10)
# df_pca = pca.fit_transform(df)

start = time.perf_counter()
kmeans = KMeans(n_clusters=1000, random_state=10).fit(df)  # random_state: Determines random number generation for centroid initialization.
end = time.perf_counter()
print(f"kmeans运行时间：{end-start}")

# 保存训练模型
joblib.dump(kmeans, 'model_1000.pickle')

# 获取聚类标签
labels = kmeans.labels_
labels = labels.tolist()
belong.insert(loc=0, column='class', value=labels)

# 输出并保存到csv文件中
# 该文件第1列表示该特征所属的簇
# 该文件第2列表示该特征对应的图片编号
out_name = 'CW_1000.csv'
belong.to_csv(out_name, sep=',', index=False, header=True)
