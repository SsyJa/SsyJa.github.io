from numpy.random import rand
import numpy as np

import matplotlib.pyplot as plt

"""seed()函数知识点，随机数生成器 原来每次运行代码时设置相同的seed，
则每次生成的随机数也相同，如果不设置seed，则每次生成的随机数都会不一样。"""
def seed_():
    # 第一次不使用seed()
    a = rand(5)
    print("第一次不使用seed():", a)
    print("=======================================================================")
    # 第二次不使用seed()
    a = rand(5)
    print("第二次不使用seed():", a)
    print("=======================================================================")
    # 第一次使用seed()
    np.random.seed(2)
    a = rand(5)
    print("第一次使用seed():", a)
    print("=======================================================================")
    # 第二次使用seed()
    np.random.seed(2)
    a = rand(5)
    print("第二次使用seed():", a)
    print("=======================================================================")

"""NumPy 是一个 Python 的第三方库，代表 “Numeric Python”，主要用于数学/科学计算;
它是一个由多维数组对象和用于处理数组的例程集合组成的库。
    使用 Numpy 我们可以轻松进行如下等计算：
    1.数组的算数和逻辑运算。
    2.傅立叶变换和用于图形操作的例程。
    3.与线性代数有关的操作。 NumPy 拥有线性代数和随机数生成的内置函数。
"""
def numpy_():
    list = [1, 2, 3, 4]
    oneArray = np.array(list)

    print(list)  # [1, 2, 3, 4]  这是python的列表对象
    print(oneArray)  # [1 2 3 4]     这是一个一维数组

    twoArray = np.array([[1, 2], [3, 4], [5, 6]])

    print(twoArray)  # [[1 2] [3 4] [5 6]]  这是一个二维数组
    print(twoArray.ndim)

    X = np.array([[0, 0, 1, 1], [0, 1, 0, 1]])
    print(X)
    m = X.shape[1]
    print(m)

"""
矩阵（ndarray）的shape属性可以获取矩阵的形状（例如二维数组的行列），获取的结果是一个元组
"""
def shap_():
    x = np.array([[1, 2, 5], [2, 3, 5], [3, 4, 5], [2, 3, 6]])
    print(x)
    # 输出数组的行和列数
    print(x.shape)  # 结果： (4, 3)
    # 只输出行数
    print(x.shape[0])  # 结果： 4
    # 只输出列数
    print(x.shape[1])  # 结果： 3

"""np.random.randn()函数:通过本函数可以返回一个或一组服从标准正态分布的随机样本值。
    语法：
    np.random.randn(d0,d1,d2……dn)
    1)当函数括号内没有参数时，则返回一个浮点数； 
    2）当函数括号内有一个参数时，则返回秩为1的数组，不能表示向量和矩阵； 
    3）当函数括号内有两个及以上参数时，则返回对应维度的数组，能表示向量或矩阵； 
    4）np.random.standard_normal（）函数与np.random.randn()类似，但是np.random.standard_normal（）的输入参数为元组（tuple）. 
    5)np.random.randn()的输入通常为整数，但是如果为浮点数，则会自动直接截断转换为整数。
    
    标准正态分布是以0为均数、以1为标准差的正态分布，记为N（0，1）。对应的正态分布曲线如下所示:
    
    在神经网络构建中，权重参数W通常采用该函数进行初始化，当然需要注意的是，
    通常会在生成的矩阵后面乘以小数，比如0.01，目的是为了提高梯度下降算法的收敛速度。 
    W = np.random.randn(2,2)*0.01
"""
def randn_():
    a = np.random.randn(1)
    print(a)

    b = np.random.randn(3,3)
    print(b)

"""返回来一个给定形状和类型的用0填充的数组；
    zeros(shape, dtype=float, order=‘C’)
    shape:形状
    dtype:数据类型，可选参数，默认numpy.float64
    order:可选参数，c代表与c语言类似，行优先；F代表列优先
"""
def zeros_():
    print(np.zeros((2, 5)))
    print(np.zeros((2, 5), dtype=np.int))

"""dot()返回的是两个数组的点积(dot product)
    1.如果处理的是一维数组，则得到的是两数组的內积
    2.如果是二维数组（矩阵）之间的运算，则得到的是矩阵积（mastrix product）
"""
def dot_():
    #处理的是一维数组
    d = np.arange(0, 9)
    print(d)
    e = d[::-1]
    print(e)
    r = np.dot(d,e)
    print(r)
    print("==========================")
    #二维数组（矩阵）之间的运算
    a = np.arange(1, 5).reshape(2, 2)
    print(a)
    b = np.arange(5, 9).reshape(2, 2)
    print(b)
    print("==========================")
    r = np.dot(a, b)
    print(r)

"""
双曲正切函数（tanh） 用python 画一个tanh的图
linspace的第一个参数表示起始点，第二个参数表示终止点，第三个参数表示数列的个数。

双曲正切函数（tanh）与tf.sigmoid非常接近，且与后者具有类似的优缺点。
tf.sigmoid和tf.tanh的主要区别在于后者的值域为[-1.0，1.0]。
"""
def tanh_():
    x = np.linspace(-100, 100, 1000)
    y = np.tanh(x)

    plt.plot(x, y, label="label", color="red", linewidth=2)
    plt.xlabel("abscissa")
    plt.ylabel("ordinate")
    plt.title("tanh Example")
    plt.show()

"""
在Python中，实现对应元素相乘，有2种方式，一个是np.multiply()，
另外一个是*,这种方式要求两个个矩阵的的形状shape相同。见如下Python代码："""
def multipy_():
    # 2-D array: 2 x 3
    a = np.array([[1, 2, 3], [4, 5, 6]])
    b = np.array([[7, 8, 9], [4, 7, 1]])
    print(a)
    print(b)
    # 对应元素相乘 element-wise product
    c = a * b
    print('element wise product: %s' % (c))

    # 对应元素相乘 element-wise product
    d = np.multiply(a, b)
    print('element wise product: %s' % (d))

"""numpy.squeeze(a,axis = None)
 1）a表示输入的数组；
 2）axis用于指定需要删除的维度，但是指定的维度必须为单维度，否则将会报错；
 3）axis的取值可为None 或 int 或 tuple of ints, 可选。若axis为空，则删除所有单维度的条目；
 4）返回值：数组
 5) 不会修改原数组；
 作用：从数组的形状中删除单维度条目，即把shape中为1的维度去掉
 
 场景：在机器学习和深度学习中，通常算法的结果是可以表示向量的数组（即包含两对或以上的方括号形式[[]]），
 （见后面的示例）。我们可以利用squeeze（）函数将表示向量的数组转换为秩为1的数组，
 这样利用matplotlib库函数画图时，就可以正常的显示结果了。
 """
def squeeze_():
    #例一
    a = np.arange(10).reshape(1, 10)
    print(a)
    a = np.arange(10).reshape(1, 10)
    b = np.squeeze(a)
    print(b)
    print("==============================")
    #例二
    c = np.arange(10).reshape(1, 2, 5)
    print(c)
    d = np.squeeze(c)
    print(d)

"""
1.sum不传参的时候，是所有元素的总和。
2.sum()输入参数带有axis时，将按照指定axis进行对应求和
    这个axis的取值就是这个精确定位某个元素需要经过多少数组的长度
    如果一个数组精确到某个元素需要a[n0][n1][n2][...][n]，则axis的取值就是n。
"""
def sum_():
    x = np.arange(9).reshape(3,3)
    print(x)
    print(np.sum(x))
    print("==================================")
    print(np.sum(x,axis=1))#在第一个轴展开方向上求和 行
    print(np.sum(x,axis=0)) #在第一个轴展开方向上求和 列
    print("==================================")
    a = np.array([[[1, 2, 3, 2], [1, 2, 3, 1], [2, 3, 4, 1]], [[1, 0, 2, 0], [2, 1, 2, 0], [2, 1, 1, 1]]])
    print(a)
    print("==================================")
    b = a.sum(axis=0)
    print(b)
    print("==================================")
    c = a.sum(axis=1)
    print(c)
    #print(a.sum(axis=(0,1)))
    print("==================================")
    d = a.sum(axis=2)
    print(d)


"""
any（）方法是查看两矩阵是否有一个对应元素相等。
事实上，all（）操作就是对两个矩阵的比对结果再做一次与运算，而any则是做一次或运算
"""
def sum_any():
    a = np.array([1,2,3])
    b = np.array([1,2,1])
    print(a==b)
    print((a==b).all())
    c = a.copy()
    print((a==c).all())
    print((a==b).any())
    d = np.array([0,0,0])
    print((a==d).any())

if __name__ == '__main__':
    #seed_()
    #numpy_()
    #shap_()
    #randn_()
    #zeros_()
    #dot_()
    #tanh_()
    multipy_()
    #squeeze_()
    #sum_()
    #sum_any()

"""
选择学习率的初始值只是问题的一部分。另一个需要优化的是学习计划（learning schedule）：
如何在训练过程中改变学习率。传统的观点是，随着时间推移学习率要越来越低，
而且有许多方法进行设置：例如损失函数停止改善时逐步进行学习率退火、指数学习率衰退、余弦退火等。

只在训练之前选择一次学习率是不够的。训练过程中，最优学习率会随着时间推移而下降。
你可以定期重新运行相同的学习率搜索程序，以便在训练的稍后时间查找学习率。

学习率 learning_rate:表示了每次参数更新的幅度大小。
如果学习率过大，会导致待优化的参数在最小值附近波动，不收敛；
学习率过小，会导致待优化的参数收敛缓慢。
"""

"""
这里包含11个Python神经网络编程的一些函数必备知识；
可以帮助读者很快地看懂Python代码：
至于一些重要原理的话，我会写在其他的文章中；
"""

"""
python国内镜像
https://pypi.python.org/simple 国外
http://pypi.douban.com/simple/ 豆瓣
http://mirrors.aliyun.com/pypi/simple/ 阿里
http://pypi.hustunique.com/simple/ 华中理工大学
http://pypi.sdutlinux.org/simple/ 山东理工大学
http://pypi.mirrors.ustc.edu.cn/simple/ 中国科学技术大学
https://pypi.tuna.tsinghua.edu.cn/simple 清华
"""
————————————————
版权声明：本文为CSDN博主「yue200403」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/yue200403/article/details/106795837
