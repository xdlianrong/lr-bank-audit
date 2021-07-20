# lr-bank-audit
银行审计演示系统（仅演示审计员页面）

## 系统简介

银行间的业务往来可以在不暴露银行本身存款总量的前提下进行审计。

审计员在系统中拥有最高的权限，可以直接查看各个银行的交易情况，以及存款总量等信息，同时根据这些信息，可以计算相应指标。

审计员要审计某银行的资金和，就会向银行发出审计请求，银行收到审计请求后，会把自己现有的资金和和致盲因子发给审计员，审计员利用账本上的Pedersen承诺的同态属性，计算相应指标。

## 目录说明

BankAudit下的五个idea工程文件，对应着audit和四个银行。

abstore.go是需要部署在fabric上的智能合约。

## 使用说明

### 环境准备

- hyperledger fabric 2.0.1

- docker

### 部署流程

在部署之前确保虚拟机中的网络正常。即输入``` ifconfig ```命令后，可以显示出ens33网卡的信息，ip地址在192.168.x.x网段之内，如下如所示。

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/image-20210630183041228.png)

#### 添加智能合约

将智能合约 ```abstore.go```复制到地址```/opt/golang/src/github.com/hyperledger/fabric-samples/chaincode/abstore/go```下。abstore.go文件已经预先存放在根目录下。

具体操作流程如下：

```
[root@localhost lxq]# cp abstore.go /opt/golang/src/github.com/hyperledger/fabric-samples/chaincode/abstore/go/
cp：是否覆盖"/opt/golang/src/github.com/hyperledger/fabric-samples/chaincode/abstore/go/abstore.go"？ yes
```

**注意，这里要输入yes，不可以用回车代替，否则不会被覆盖。**

#### 利用脚本启动Fabric

开启一个终端，进入root模式下，切换到部署脚本的路径下，执行脚本，用于启动fabric。具体的命令如下，顺次执行即可。

```
[root@localhost lxq]# cd /opt/golang/src/github.com/hyperledger/fabric-samples/first-network
[root@localhost first-network]# ./byfn.sh down
Stopping for channel 'mychannel' with CLI timeout of '10' seconds and CLI delay of '3' seconds
Continue? [Y/n] Y
proceeding ...
[root@localhost first-network]# ./byfn.sh generate
Generating certs and genesis block for channel 'mychannel' with CLI timeout of '10' seconds and CLI delay of '3' seconds
Continue? [Y/n] Y
proceeding ...
[root@localhost first-network]# ./byfn.sh up
Starting for channel 'mychannel' with CLI timeout of '10' seconds and CLI delay of '3' seconds
Continue? [Y/n] Y
proceeding ...

```

命令行出现如下画面，代表成功。如果启动失败，重新执行顺次执行部署脚本的三条命令即可。

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/image-20210630175148123.png)

#### 验证Fabric是否启动（非必须操作）

为验证fabric已经成功启动，可以进入到docker中查看。

具体执行命令如下：

```
[root@localhost first-network]# docker exec -it cli bash
bash-5.0# peer channel list
2021-06-30 09:53:11.417 UTC [channelCmd] InitCmdFactory -> INFO 001 Endorser and orderer connections initialized
Channels peers has joined: 
mychannel
bash-5.0# peer channel getinfo -c mychannel
2021-06-30 09:53:19.370 UTC [channelCmd] InitCmdFactory -> INFO 001 Endorser and orderer connections initialized
Blockchain info: {"height":7,"currentBlockHash":"M2cE3hfeAhEuRjfMVkTFiTO1v1ZUhZjd9rlRAZgEGXk=","previousBlockHash":"gV1Kv1zeI9tMQu3nytqlIPukRQtB7k878wEu0xIc7i4="}
bash-5.0# exit
exit
[root@localhost first-network]# 

```

由以上指令可以看出，在docker中已经存在对应的容器，当前链的高度为7，这是fabric的初始高度。

#### 复制config证书

由于每次虚拟机启动后，fabric需要重新启动，因此需要新的证书，需要将证书加入到java后端代码中。

```
[root@localhost first-network]# tar cvf crypto-config.tar ./crypto-config
[root@localhost first-network]# mv crypto-config.tar /home/lxq/
```

执行上述两条命令之后，可以在根目录下看到一个名为```crypto-config.tar```的压缩包，将压缩包从虚拟机中导出到实体机中。

#### 修改java代码

首先检查后端代码中的```src/main/resources/connection.json```文件的ip地址是否与虚拟机的IP地址一致，如果不一致需要将json文件中的所有IP地址都进行替换，ip地址后的端口号不要更改。

将crypto-config解压，解压后在crypto-config文件夹下应该有以下两个文件。

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/20210630205938.png)

将crypto-config文件夹放在```src/main/resources```目录下，替换原有的crypto-config。

替换完成后，启动相应的后端代码即可。

### 使用流程

#### 初始化

监管者首先启动后台代码，然后打开index.html文件，即可进入前端页面。首先，在确保银行全部启动，且没有转账时，点击右上角的初始化系统，对系统进行初始化。完成初始化后，银行可以通过命令行进行转账操作，四个银行互相转钱即可。

#### 审计

转账之后，进入审计模式，如下图所示。

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/image-20210630204206581.png)

可以选择审计对象和审计的内容，选择完之后点击开始审计即可，右下角的清空过程可以对侧边的零知识证明过程进行清空。如下图所示，是审计的结果。

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/20210630204940.png)

#### 区块链浏览器

链上状态是区块链浏览器

![](https://img-for-md-1306026404.cos.ap-beijing.myqcloud.com/img/20210630205032.png)

区块链浏览器中，点击刷新按钮可以刷新，点击加号可以看详情。

#### 安全退出

银行通过命令行进行退出，监管者点击右上角的安全退出即可。

