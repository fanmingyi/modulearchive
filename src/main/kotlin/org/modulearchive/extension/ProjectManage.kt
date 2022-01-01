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

import org.gradle.api.Project
import org.gradle.api.file.FileTree

open class ProjectManage constructor(val name: String) {

    /**
     * 使用debug包
     */
    var useDebug: Boolean = true

    /**
     * 是否启用
     */
    var enable: Boolean = true

    /**
     * 配置aar
     */
    var aarName: String = "_${name}.aar"

    /**
     * 风味组合名 如AAXX
     */
    var flavorName: String = ""


    var filesChangeListener: FileTree? = null


    fun obtainProject(project: Project): Project {
        return project.project("$name")
    }

}