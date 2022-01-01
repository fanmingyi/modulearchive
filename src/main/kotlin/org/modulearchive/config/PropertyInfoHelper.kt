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
package org.modulearchive.config

import org.apache.tools.ant.util.FileUtils
import org.modulearchive.IInfoCenter
import org.modulearchive.extension.ProjectManage
import org.modulearchive.extension.ProjectManageHelper
import org.modulearchive.log.ModuleArchiveLogger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class PropertyInfoHelper constructor(val info: IInfoCenter) {

    val props = Properties()

    var isInit = false

    val fileName = "ModuleArchive.properties"

    fun getPropertyInfo(): Properties {
        if (!isInit) {
            val storeLibsDir = info.getModuleArchiveExtension().getStoreLibsDir().get()
            val configFile = File(storeLibsDir, fileName)
            if (!configFile.exists()) {
                configFile.createNewFile()
            } else {
                props.load(FileReader(configFile))
            }
            if (configFile.exists()) {
                props.load(FileReader(configFile))
            }
        }
        isInit = true
        return props
    }

    /**
     * 是否缓存
     */
    fun currentAarHit(project: ProjectManage): Boolean {

        val aarFile =
            ProjectManageHelper.obtainProjectAARFile(info, project)

        if (!aarFile.exists()) {
            ModuleArchiveLogger.logLifecycle("${project.name} don't find cache aar.")
            return false
        }
        val propertyInfo = getPropertyInfo()
        val curAARProLastModified = ProjectManageHelper.curAARProLastModified(info, project)
        val property = propertyInfo.getProperty(project.aarName)
        if (curAARProLastModified.toString() == property) {
            ModuleArchiveLogger.logLifecycle("${project.name}  found cache aar.")
            return true
        }

        ModuleArchiveLogger.logLifecycle("${project.name} don't find cache aar.")
        return false
    }

    /**
     * 是否缓存
     */
    fun upProjectManager(project: ProjectManage) {
//        val aarFile =
//            ProjectManageHelper.obtainProjectAARFile(info, project)
//
//        if (!aarFile.exists()) {
//            ModuleArchiveLogger.logLifecycle("${project.name} don't find cache aar.")
//
//            return false
//        }
        val propertyInfo = getPropertyInfo()
        val curAARProLastModified = ProjectManageHelper.curAARProLastModified(info, project)
        propertyInfo.setProperty(project.aarName, curAARProLastModified.toString())

    }

    /**
     * 是否缓存
     */
    fun writeFile() {
        val propertyInfo = getPropertyInfo()
        val storeLibsDir = info.getModuleArchiveExtension().getStoreLibsDir().get()
        val configFile = File(storeLibsDir, fileName)
        propertyInfo.store(FileWriter(configFile),"用于存储缓存aar映射关系")
    }

}