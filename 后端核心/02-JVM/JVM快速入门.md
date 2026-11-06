不同操作系统上的JVM不同

通过将java文件编译成class字节码文件用于JVM进行执行，节省了运行时的时间

java到class的过程是编译，class放到JVM运行这个部分是解释，所以综合了编译和解释

![1792481029348](E:\library\learninghub\后端核心\02-JVM\assets\1792481029348.png)

其他高级语言也可以通过编译成字节码文件通过JVM实现跨平台

![1792481183140](E:\library\learninghub\后端核心\02-JVM\assets\1792481183140.png)

![1792481698762](E:\library\learninghub\后端核心\02-JVM\assets\1792481698762.png)

类加载子系统：将字节码文件从磁盘中读取出来放入内存

解释器执行字节码指令需要将字节码指令翻译成机器指令

JIT编译器将热点字节码指令之间对应到相应的机器指令（？不是特别清楚）

![1792482117243](E:\library\learninghub\后端核心\02-JVM\assets\1792482117243.png)

classPath

双亲委派避免类的重复加载



Young GC/Minor GC

Old GC/Major GC

Full GC

垃圾是指JVM中没有任何引用指向的对象

**垃圾回收的阶段**

STW

标记阶段
找到有哪些垃圾，引用计数法，可达性分析法

GC root

标记清除算法

复制算法

标记整理算法