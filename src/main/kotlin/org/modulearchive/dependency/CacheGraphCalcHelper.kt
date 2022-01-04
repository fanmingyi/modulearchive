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

import org.modulearchive.IInfoCenter
import org.modulearchive.extension.ProjectManageWrapper
import org.modulearchive.extension.convertWrapper
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

object CacheGraphCalcHelper {

    /**
     * 计算出缓存有效
     */
    fun calcCacheValid(info: IInfoCenter): List<ProjectManageWrapper> {

        val propertyInfoHelper = info.getPropertyInfoHelper()
        val projectManageList =
            info.getModuleArchiveExtension().projectConfig.toList().map { it.convertWrapper(info) }
        val countDownLatch = CountDownLatch(projectManageList.size)

        for (projectManage in projectManageList) {
            thread {
                projectManage.cacheValid = propertyInfoHelper.currentAarHit(projectManage)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return projectManageList
    }

    /**
     * 计算出依赖图
     */
//    fun calcDependenceCacheValid(info: IInfoCenter) {
//        val pro = info.getTargetProject()
//        val propertyInfoHelper = info.getPropertyInfoHelper()
//        val moduleList = info.getModuleArchiveExtension().getProjectConfig().toList()
//        for (projectManage in moduleList) {
//            projectManage.obtainProject(pro).configurations.all { configuration ->
//                configuration.dependencies.all {dependency ->
//                    if (dependency !is ProjectDependency) {
//                        return
//                    }
//                }
//            }
//        }
//    }


}