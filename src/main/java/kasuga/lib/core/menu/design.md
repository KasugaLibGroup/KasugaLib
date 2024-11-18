# KasugaLib Menu System Design
## 简介
KasugaLib菜单系统是一个基于网络通信的GUI框架，用于在Minecraft中创建和管理复杂的用户界面交互。

## 已有实现
### AbstractContainerMenu

Minecraft原版的`AbstractContainerMenu`是一个抽象基类,用于实现物品栏(Inventory)相关的GUI界面。它提供了以下核心功能:

1. 槽位(Slot)管理
- 维护一个槽位列表,用于存储和管理GUI中的物品栏格子
- 提供槽位的添加、移除、查询等基础操作
- 支持槽位的拖拽、快速移动等交互操作

2. 数据同步
- 在服务端和客户端之间同步物品栏数据
- 通过ContainerData接口同步自定义数据
- 支持增量更新以优化网络传输

3. 交互处理
- 处理玩家的点击、拖拽等操作
- 支持快速合成(Quick Craft)功能
- 处理物品的分割和合并

4. 生命周期管理
- 处理容器打开和关闭事件
- 在关闭时正确处理剩余物品
- 维护容器的有效性检查

这个类为Minecraft中所有带物品栏的GUI提供了基础实现,如:
- 玩家背包界面
- 箱子界面
- 工作台界面
- 熔炉界面
等


## 缺点
- 原版`AbstractContainerMenu`的槽位管理只能通过`addSlot`方法添加，但是 KasugaLib 实现了一套基于 Javascript 的 GUI 系统，Slot 的位置由 Javascript 内的 renderer 决定，因此原版`AbstractContainerMenu`的槽位管理无法满足需求
    - KasugaLib 的 GUI 系统中，Slot 的 Identifier 和 Slot 的渲染设置是分离的, `addSlot()` 方法只负责添加槽位，槽位的渲染设置(X,Y)由具体渲染器决定

- 原版`AbstractContainerMenu`的网络通信是基于`ContainerData`的, 因此无法完全控制发送数据的时机和内容，对于需要较为复杂的数据控制的模块（如 `LKJ2000` ），原版`AbstractContainerMenu`无法满足需求, 而 KasugaLib 抽象了一个更通用的网络协议实现 `NetworkChannel`, 故需要基于新的 NetworkChannel 实现新的菜单系统

## 设计
我们对 GUI 系统抽象为多层，每一层实现不同的功能