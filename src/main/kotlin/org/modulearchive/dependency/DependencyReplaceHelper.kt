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

package org.modulearchive.dependency

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.modulearchive.IInfoCenter
import org.modulearchive.config.PropertyInfoHelper
import org.modulearchive.extension.ProjectManageHelper
import org.modulearchive.extension.ProjectManageHelper.buildAARGraph
import org.modulearchive.log.ModuleArchiveLogger


class DependencyReplaceHelper constructor(
    val infoCenter: IInfoCenter,
) {

    fun replaceDependency() {
        replaceDependency(infoCenter.getTargetProject())
    }

    private fun replaceDependency(replaceProject: Project) {
//        println("开始处理依赖替换 ${replaceProject.name}")
        replaceProject.configurations.all { configuration ->
//            println("开始处理依赖替换 ${replaceProject.name} ${configuration.name}")
            configuration.dependencies.all { dependency ->
                handleReplaceDependency(configuration, dependency, replaceProject)
            }
        }


    }


    private fun handleReplaceDependency(
        configuration: Configuration,
        dependency: Dependency,
        replaceProject: Project
    ) {
        val moduleArchiveExtension = infoCenter.getModuleArchiveExtension()

        val managerProList = moduleArchiveExtension.getProjectConfig().map {
            it.obtainProject(infoCenter.getTargetProject())
        }
        if (dependency !is ProjectDependency) {
            return
        }

        val dependencyProject = dependency.dependencyProject
        if (dependencyProject === replaceProject) {
            return
        }

        val manager = moduleArchiveExtension.getProjectConfig()
            .findByName(dependencyProject.path)


        if (manager != null && manager.enable && managerProList.contains(dependencyProject)) {
            ModuleArchiveLogger.logLifecycle("Handle dependency：${replaceProject.name}:${dependency.name}  ")

            val aarFile =
                ProjectManageHelper.obtainProjectAARFile(infoCenter, manager)



            //todo 这里暂时以文件存在判断是否缓存成功
            val propertyInfoHelper = infoCenter.getPropertyInfoHelper()

            if (propertyInfoHelper.currentAarHit(manager)) {
                configuration.dependencies.remove(dependency)

                val aarConfig = infoCenter.getTargetProject().rootProject.files(
                    aarFile
                )
                configuration.dependencies.add(replaceProject.dependencies.create(aarConfig))
            } else {
                //不存在文件构建
                buildAARGraph(infoCenter, manager)
            }


        }
        replaceDependency(dependencyProject)
    }
}