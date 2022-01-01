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
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Zip
import org.modulearchive.IInfoCenter
import org.modulearchive.extension.ProjectManage
import org.modulearchive.log.ModuleArchiveLogger
import java.io.File

public abstract class ModuleArchiveTask : DefaultTask() {

    @InputFiles
    @SkipWhenEmpty
    abstract fun getInputAARList(): ConfigurableFileCollection

    @OutputDirectory
    var outPutDirFile = File(".")

    @Internal
    var reNameMap = HashMap<String, ProjectManage>()

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
                    ModuleArchiveLogger.logLifecycle("Copy aar  from ${name} to ${projectManage.aarName}.")
                    infoCenter?.getPropertyInfoHelper()?.upProjectManager(projectManage)
                    return@rename projectManage.aarName
                }
                name
            }

        }

    }


    fun aarInput(taskProvider: TaskProvider<Zip>, projectManage: ProjectManage) {
        getInputAARList().from(taskProvider)
        val get = taskProvider.get()
        project.fileTree("")
        val archiveFileName = get.archiveFileName.get()
        reNameMap[archiveFileName] = projectManage
    }

    fun aarOutDir(dir: File) {
        outPutDirFile = dir
    }


}