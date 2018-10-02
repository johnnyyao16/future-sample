## 简单的Future实现
在 Java 中广泛使用了 Future，可以实现异步调用。本例 Futuer 的简单实现中我们利用了 Java 的等待通知机制，所谓等待通知机制，就是某个线程A调用了对象 Obj 的 wait() 方法，另一个线程 B 调用对象 Obj 的 notify() 或者 notifyAll() 方法。 线程 A 接收到线程 B 的通知，从 wait 状态中返回，继续执行后续操作。两个线程通过对象 Obj 来进行通信。
### 等待-通知经典范式
* wait线程：

  1. 获取对象的锁

  2. 条件不满足，调用对象wait()方法，让出线程

  3. 等待另外线程通知，如果满足条件，继续余下操作执行。

```
lock(object){
	while(condition){
		object.wait();
	}
	doOthers();
}
```

* notify线程：

  1. 获取对象的锁。
  2. 修改条件。
  3. 调用对象的notify()或者notifyAll()方法通知等待的线程。
  4. 释放锁

```
lock(object){
	change(condition);
	objcet.notify();
}
```

### 具体实现

#### FutureData

定义 isCompleted 标记和数据域 data

```
private boolean isCompleted = false;

private String data;
```

定义 get() 方法：如果未完成，即 isCompleted 为 false，则调用 wait 来等待；如果已完成，即 isCompleted 为 true，则返回数据域

```
public synchronized String getData() throws InterruptedException {
    while (true) {
	      if (isCompleted) {
		        logger.info("Set data is completed, return data!");
		        return data;
		    } else {
			      logger.info("Set data is not completed, wait!");
			      wait();
		    }
    }
}
```

定义 set() 方法：如果已完成，即 isCompleted 为 true，则直接返回；否则模拟10s 的延迟，把数据放入数据域，并且将 isCompleted 置为 true，最后唤醒其他线程

```
public synchronized void setData(String data) throws InterruptedException {
    if (isCompleted) {
        return;
    }
    // 模拟10s的延迟
    TimeUnit.SECONDS.sleep(10);
    this.data = data;
    isCompleted = true;
    notifyAll();
}
```

#### FutureDataService

实例化一个 FutureData 对象，起一个线程去设置数据，然后直接返回 FutureData实例

```
public FutureData getFutureData() {
    FutureData futureData = new FutureData();
    new Thread(() -> {
        try {
            futureData.setData("Future data is ready!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
    return futureData;
}
```

#### Main

对于一些比较费时的操作，可以先返回 FutureData 对象，然后做一些不依赖于费时操作结果的事。等到真的需要用到费时操作结果的时候，再去 FutureData 里以阻塞的方式获取结果

1. 实例化 futureData1，然后有10s 的时间可以做其他与获取数据无关的操作
2. 过3s 后实例化 futureData2
3. 做完其他与获取数据无关的操作后，获取 futureData1的结果
4. 获取 futureData2的结果，此时需要等待3s

```
public static void main(String args[]) throws InterruptedException {
	FutureDataService service = new FutureDataService();
	FutureData futureData1 = service.getFutureData();
	logger.info("Total 10s to get ready for future data");
	logger.info("We can do something here");
	TimeUnit.SECONDS.sleep(3);
	FutureData futureData2 = service.getFutureData();
	String result1 = futureData1.getData();
	logger.info("Future data 1 is ready, get result {}", result1);
	String result2 = futureData2.getData();
	logger.info("Future data 2 is ready, get result {}", result2);
}
```

