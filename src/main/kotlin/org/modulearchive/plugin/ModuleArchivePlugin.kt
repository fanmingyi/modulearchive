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

package org.modulearchive.plugin

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariantOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.modulearchive.IInfoCenter
import org.modulearchive.config.PropertyInfoHelper
import org.modulearchive.dependency.DependencyReplaceHelper
import org.modulearchive.extension.ModuleArchiveExtension
import org.modulearchive.extension.ProjectManageHelper
import org.modulearchive.log.ModuleArchiveLogger
import org.modulearchive.task.ModuleArchiveAARGraphHelper
import org.modulearchive.task.ModuleArchiveTask
import java.io.FileReader
import java.io.Reader
import java.util.*


public class ModuleArchivePlugin : Plugin<Project>, IInfoCenter {

    private lateinit var project: Project
    private lateinit var moduleArchiveTask: ModuleArchiveTask
    private lateinit var moduleArchiveExtension: ModuleArchiveExtension
    private lateinit var dependencyReplaceHelper: DependencyReplaceHelper
    private lateinit var propertyInfoHelper: PropertyInfoHelper

    override fun apply(project: Project) {
        this.project = project


        //构造配置
        this.moduleArchiveExtension = project.extensions.create<ModuleArchiveExtension>(
            "moduleArchive",
            ModuleArchiveExtension::class.java
        )

        val moduleArchiveTask: TaskProvider<ModuleArchiveTask> =
            project.tasks.register(
                "moduleArchiveTask",
                ModuleArchiveTask::class.java
            )

        this.moduleArchiveTask = moduleArchiveTask.get()

        project.tasks.getByName("preBuild").dependsOn(moduleArchiveTask)

        dependencyReplaceHelper =
            DependencyReplaceHelper(this)
        propertyInfoHelper = PropertyInfoHelper(this)
        moduleArchiveTask.get().infoCenter=this
        moduleArchiveTask.get().doLast {
            propertyInfoHelper.writeFile()
        }
        //是否开启日志
        project.afterEvaluate {

            //沒有啓用直接返回
            if (!moduleArchiveExtension.enable){
                return@afterEvaluate
            }

            ModuleArchiveLogger.enableLogging = moduleArchiveExtension.logEnable
            moduleArchiveTask.get().aarOutDir(moduleArchiveExtension.getStoreLibsDir().get())
            dependencyReplaceHelper.replaceDependency()
//            ModuleArchiveAARGraphHelper.buildGraph(this)
//
//            ProjectManageHelper.obtainAARDirInspection(
//                this,
//                moduleArchiveExtension.getProjectConfig().single()
//            )
        }
    }


    fun readConfig() {
        val properties = Properties()
        val file = project.rootProject.file("moduleArchiveConfig")
        if (file.exists()) {
            val reader = FileReader(file)
            properties.load(reader)
        }
    }

    override fun getModuleArchivePlugin(): ModuleArchivePlugin {
        return this
    }

    override fun getModuleArchiveExtension(): ModuleArchiveExtension {
        return moduleArchiveExtension
    }

    override fun getDependencyReplaceHelper(): DependencyReplaceHelper {
        return dependencyReplaceHelper
    }

    override fun getModuleArchiveTask(): ModuleArchiveTask {
        return moduleArchiveTask
    }

    override fun getTargetProject(): Project {
        return project
    }

    override fun getPropertyInfoHelper(): PropertyInfoHelper {
        return propertyInfoHelper

    }
}