# ModuleArchive

一个可以提升Android编译效率的Gradle小插件

在多module工程下，大多数情况我们若干子module是不会变动的
但是在当前gradle版本偶尔会触发联动编译进而影响编译效率。

本插件会自动缓存编译后的子模块生产物(xxx.aar)，且会自动替换依赖为aar依赖。
如果你改动了子module本插件通用会自动感知进行编译再次进行缓存。

在[Tiya](https://play.google.com/store/apps/details?id=com.huanliao.tiya&hl=en_US&gl=US)项目中增量构建效益对比图
![1641143695(1)](https://user-images.githubusercontent.com/22413240/147883749-3f2e2c15-66bf-4ca7-9e67-8cbb565de731.png)


# 使用指南
在您的app的build.gradle添加如下配置
```groovy
//启用插件
apply plugin: org.modulearchive.plugin.ModuleArchivePlugin
//插件配置
moduleArchive {
    //可选参数.是否打印log 默认为false
    logEnable = true
    //可选参数.是否启用插件 默认为false
    pluginEnable = true
    //必选参数.存储插件临时配置目录
    storeLibsDir = project.rootProject.file("libs")
    //下面配置哪些模块可以被编译成aar缓存
    subModuleConfig {
        //image-picker是一个aar模块，那么他会自动在构建后缓存
        //从而提高效率，在您修改这个模块后会自动进行构建
        register(":image-picker") {
            //可选参数.是否使用debug版本
            useDebug = true
            //可选参数.是否启用这个模块配置 
            enable = true
            //可选参数. 缓存的aar命中
            aarName = "image-picker-debug.aar"
            //可选参数.构建变体 如没有可不写
            flavorName = "tiya"
        }
        //另一个aar模块，其最简约配置
        register(":floatwindow") {
      
        }



    }
}
```

# 谁在使用

| TIYA        | 
| --------   | 
|![tiyaICON(1)](https://play-lh.googleusercontent.com/RwuBOgoBX1OmmR5W14AyBDp9pNgnh1eJD2UmJzhVSZOpZYG1xI_y1aihbE4aP3dURwc=s360-rw)        |


