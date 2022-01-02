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

package org.modulearchive

import org.gradle.api.Project
import org.modulearchive.config.PropertyInfoHelper
import org.modulearchive.dependency.DependencyReplaceHelper
import org.modulearchive.extension.ModuleArchiveExtension
import org.modulearchive.extension.ProjectManageWrapper
import org.modulearchive.plugin.ModuleArchivePlugin
import org.modulearchive.task.ModuleArchiveTask

interface IInfoCenter {

    fun getModuleArchivePlugin(): ModuleArchivePlugin
    fun getModuleArchiveExtension(): ModuleArchiveExtension
    fun getDependencyReplaceHelper(): DependencyReplaceHelper
    fun getModuleArchiveTask(): ModuleArchiveTask
    fun getTargetProject(): Project
    fun getPropertyInfoHelper(): PropertyInfoHelper
    fun getManagerList(): List<ProjectManageWrapper>
}