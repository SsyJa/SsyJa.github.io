#! user/bin/python3.8
# *- coding:utf-8 -*-

import cv2
import pandas as pd


# 创建sift特征提取器
sift = cv2.xfeatures2d.SIFT_create()

# 存放数据集的路径
path = "corel/"

# title = [i for i in range(128)]
all_sift = []
belong = []


for i in range(10):
    # 获取文件夹路径
    path = path + str(i) + '/'
    for j in range(100):
        # 拼接图片的路径
        k = i * 100 + j
        img_path = path + str(k) + '.jpg'
        # 读入图片
        orig_img = cv2.imread(img_path)
        # 调用sift.detectAndCompute()方法提取特征
        # 第二个参数:mask(掩码)，屏蔽不需要提取特征点的区域,设置为None表示检测所有
        keypoint, vec = sift.detectAndCompute(orig_img, None)
        # print(vec.shape)   # (n,128）

        # 把提取的特征加入all_sift中
        vec = list(vec)
        all_sift += vec
        # 添加每一个特征点所属图片的序号
        belong += len(vec) * [k]

        print(k)  # 图片编号
        print(len(vec))  # 本次提取的特征点数
        print(len(all_sift))  # 当前总特征点数
        print("\n")

    path = "corel/"

# 总特征点数：695661
# 将特征向量输出到文件中
df = pd.DataFrame(all_sift)
df.insert(loc=0, column='belong', value=belong)
# df.shape->(695661, 129)
out_path = '_sift_.csv'
df.to_csv(out_path, sep=',', index=False, header=True)