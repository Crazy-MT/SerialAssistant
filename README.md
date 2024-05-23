# SerialAssistant

#### 介绍
一款串口调试助手，从 https://gitee.com/liang-fu/serial-assistant 精简来的，感谢 liang-fu 贡献的开源项目。

本项目包含了一个解析以下协议的示例代码，解决数据包解析和验证，并且保证数据包完整。

```
取重

1、主动/被动模式的数据格式相同。
2、上位机指令（HEX）： 1b 01 02
3、数据格式：（总共 24 字节）

01 02  000.000kg 000.000kg sta  X   03 04
数据头  净重       皮重      状态 校验 数据尾

SHead1        SOH(01H)   1 字节，标题开始
SHead2        STX(02H)   1 字节，正文开始
Weight 1      XXX.XXX    7 字节，净重。
Weight Units  U1U0       2 字节，重量单位。如“kg”
Weight2       XXX.XXX    7 字节，皮重。
Weight Units  U1U0       2 字节，重量单位。如“kg”
Status        STA        1 字节，状态
Check Sum     BCC        1 字节，使用 BCC 算法，除 SOH STX ETX EOT 及本字节外所有字符的 BCC 校验。
Tail1         ETX(03H)   1 字节，标题结束
Tail2         EOT(04H)   1 字节，传输结束

重量格式（净重/皮重），例如：
123.456kg
23.456kg
12.3456kg
0.012kg
-12.345kg
-1.234kg
-0.0001kg
（前面无数据则用空格填充。如果小数点后面有四位，则为精确到 0.1g）

状态：
bit7：1 重量溢出；0 重量正常
bit6：1 开机后未归零（开机时秤盘上有重物）；0 开机后已归零
bit5：1 当前在去皮模式；0 当前不是    去皮模式
bit4：1 当前重量为 0；0 当前重量不为 0 
bit3：1 重量稳定；0 重量不稳定 
bit2~bit0  0
```    