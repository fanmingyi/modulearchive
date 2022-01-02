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

package org.modulearchive.task

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Zip
import org.modulearchive.IInfoCenter
import org.modulearchive.extension.ProjectManageWrapper
import org.modulearchive.log.ModuleArchiveLogger
import java.io.File

public abstract class ModuleArchiveTask : DefaultTask() {

    @InputFiles
    @SkipWhenEmpty
    abstract fun getInputAARList(): ConfigurableFileCollection

    @OutputDirectory
    var outPutDirFile = File(".")

    @Internal
    var reNameMap = HashMap<String, ProjectManageWrapper>()

    @Internal
    var infoCenter: IInfoCenter? = null

    @TaskAction
    fun perform() {
        project.copy { it ->
            it.from(getInputAARList())
            it.into(outPutDirFile)
            it.rename { name ->
                val projectManage = reNameMap[name]

                if (projectManage != null) {
                    ModuleArchiveLogger.logLifecycle("Copy aar  from $name to ${projectManage.originData.aarName}.")
                    infoCenter?.getPropertyInfoHelper()?.upProjectManager(projectManage)
                    return@rename projectManage.originData.aarName
                }
                name
            }

        }

    }


    fun aarInput(
        taskProvider: TaskProvider<Zip>,
        projectManage: ProjectManageWrapper,
        packageLibraryProvider: TaskProvider<Task>
    ) {
        getInputAARList().from(taskProvider)
        //不知道为什么有时候单纯依靠上面的输入输出关联有时候无法成功触发编译aar
        //因此加入下面的代码
        dependsOn(packageLibraryProvider)
        val zip = taskProvider.get()
        val archiveFileName = zip.archiveFileName.get()
        reNameMap[archiveFileName] = projectManage
    }

    fun aarOutDir(dir: File) {
        outPutDirFile = dir
    }


}