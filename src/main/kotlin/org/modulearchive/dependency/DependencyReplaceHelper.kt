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
import org.modulearchive.extension.ProjectManageHelper
import org.modulearchive.extension.ProjectManageWrapper
import org.modulearchive.log.ModuleArchiveLogger


class DependencyReplaceHelper constructor(
    private val infoCenter: IInfoCenter
) {

    fun replaceDependency() {

        //替换依赖
        replaceDependency(infoCenter.getTargetProject())
        //使用aar依赖不会打包到apk所以这里需要进一步的处理
//        addDependencyToTarget()

    }

    /**
     * 添加依赖到启动模块，防止没有打包进apk的情况
     */
    private fun addDependencyToTarget(manager: ProjectManageWrapper) {
        val implementation = infoCenter.getTargetProject().configurations.getByName("implementation")
        val api = infoCenter.getTargetProject().configurations.getByName("api")
        implementation.dependencies.add(manager.obtainAARDependency())
        api.dependencies.add(manager.obtainAARDependency())
    }

    private fun replaceDependency(replaceProject: Project) {
        replaceProject.configurations.all { configuration ->
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

        val managerList = infoCenter.getManagerList()

        if (dependency !is ProjectDependency) {
            return
        }

        //依赖对应的project
        val dependencyProject = dependency.dependencyProject

        //防止自己引用自己
        if (dependencyProject === replaceProject) {
            return
        }


        val manager = managerList.firstOrNull { it.originData.name == dependencyProject.path }


        if (manager != null && manager.originData.enable) {
            //标记这个对象被引用了
            manager.flagHasOut = true

//            if (replaceProject != infoCenter.getTargetProject()) {
//                addDependencyToTarget(manager)
//            }

            ModuleArchiveLogger.logLifecycle("Handle dependency：${replaceProject.name}:${dependency.name}  ")

            if (manager.cacheValid) {
                //缓存命中

                ModuleArchiveLogger.logLifecycle("${replaceProject.name} 依赖 ${manager.obtainName()} 缓存命中")

                //添加依赖路径
                replaceProject.repositories.flatDir { flatDirectoryArtifactRepository ->
                    flatDirectoryArtifactRepository.dir(moduleArchiveExtension.storeLibsDir)
                }
                //移除原始的project依赖
                configuration.dependencies.remove(dependency)
                //添加aar依赖
                configuration.dependencies.add(manager.obtainAARDependency())


            } else {
                ModuleArchiveLogger.logLifecycle("${replaceProject.name} 依赖 ${manager.obtainName()} 没有命中缓存")
                //不存在文件进行构建
                ProjectManageHelper.buildAARGraph(infoCenter, manager)
            }
        }

        //当前进行替换replaceProject是否在管理的范围
        val replaceProInManager = managerList.firstOrNull { it.obtainProject() == replaceProject }

        //记录依赖关系
        if (manager != null && replaceProInManager != null) {
            replaceProInManager.dependencyManagerList.add(manager)
        }

        //替换自工程的依赖
        replaceDependency(dependencyProject)
    }
}