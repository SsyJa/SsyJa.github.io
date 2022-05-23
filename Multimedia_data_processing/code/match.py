#! user/bin/python3.8
# *- coding:utf-8 -*-

import numpy as np
import pandas as pd
import cv2
import joblib
import random
import matplotlib.pyplot as plt


# 余弦相似度
def cos(vector1, vector2):
    return np.dot(vector1, vector2) / (np.linalg.norm(vector1) * (np.linalg.norm(vector2)))
    # np.linalg.norm 用于求范数，向量的二范数即为每个元素的平方和再开平方


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

# 随机读入一张图片
path = "corel/"
i = random.randint(0, 9)
j = random.randint(0, 99)
k = i * 100 + j
img_path = path + str(i) + '/' + str(k) + '.jpg'
img = cv2.imread(img_path)

# img = cv2.imread("corel/3/300.jpg")

# 创建sift特征提取器
sift = cv2.xfeatures2d.SIFT_create()
# 提取该图片的特征
keypoint, vector = sift.detectAndCompute(img, None)
# print(vector.shape)  # (n, 128)

# 预测每一个特征向量的所在簇的编号
vector = vector.astype(float)
labels = model.predict(vector)
# print(labels.shape)

# 统计该图片的词频
input_count = np.zeros(1000)
for i in range(len(labels)):
    input_count[labels[i]] += 1

# 相似度比较: 余弦相似度
similar = []
for i in range(1000):
    similar.append(cos(input_count, count[i]))

# 选取与之最大相似度的10张图片的编号
img_no = np.argsort(similar)[-11:]


for idx, no in enumerate(img_no):
    print(no)  # 图片编号
    print(similar[no])  # 余弦相似度

    src_path = "corel/" + str(no//100) + '/' + str(no) + '.jpg'
    #  opencv的颜色通道顺序为[B,G,R]，而matplotlib颜色通道顺序为[R,G,B],所以需要调换一下通道位置
    img = cv2.imread(src_path)[:, :, (2, 1, 0)]
    plt.subplot(3, 5, idx+1)
    plt.xticks([])
    plt.yticks([])
    plt.imshow(img)

plt.show()
