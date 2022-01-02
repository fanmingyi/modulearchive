/*
 * Copyright (C) 2021 fmy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.modulearchive.extension

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Optional
import java.io.File

open class ModuleArchiveExtension {
    /**
     * 是否启用插件
     */
    var pluginEnable: Boolean = false

    /**
     * 是否打印日志
     */
    var logEnable: Boolean = false

    /***
     * 如果当前的task任务名称满足就启动依赖替换
     */
    var detectLauncherRegex: String = ""


    /**
     * 得到存储lib的目录
     */
    var storeLibsDir: File = File("")

    /**
     * 管理子module
     */

    var projectConfig: NamedDomainObjectContainer<ProjectManage>

    constructor(project: Project) {
        projectConfig = project.container(ProjectManage::class.java)
    }


    fun subModuleConfig(action: Action<NamedDomainObjectContainer<ProjectManage>>) {
        action.execute(projectConfig)
    }

}