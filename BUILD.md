# 打包说明

本插件使用 Gradle 6.7.1 与 IntelliJ 插件 0.6.5，**构建时需使用 JDK 8 或 JDK 11**（Gradle 6.7 不支持在 JDK 21 下运行）。项目已配置使用本机 JDK 11（见 `gradle.properties`）。

## 打包命令

在项目根目录执行：

```bash
.\gradlew.bat build
```

## 构建产物

| 产物 | 路径 | 说明 |
|------|------|------|
| **插件 ZIP（推荐安装用）** | `build\distributions\touch-fish-2.3.2.zip` | 在 IDE 中通过「从磁盘安装插件」选择此文件 |
| **插件 JAR** | `build\libs\touch-fish-2.3.2.jar` | 插件主 JAR，也可单独使用 |

## 仅生成 JAR

若只需要 JAR、不打包完整 ZIP：

```bash
.\gradlew.bat jar
```

## 使用其他 JDK 版本构建

若本机 JDK 11 路径不同，可编辑 `gradle.properties` 中的 `org.gradle.java.home`（或设置环境变量 `JAVA_HOME` 为 JDK 8/11 的安装目录）。
