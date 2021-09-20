/*
 * Licensed to the organization of MARVEL under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MARVEL licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.marvel.admin.runner;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.admin.common.MarvelSpringBootContext;
import org.marvel.admin.entity.*;
import org.marvel.admin.service.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component
@Slf4j
public class InitializeDataRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments arguments) {
        log.info("Start importing initial data...");
        try {
            int count = 0;
            ExcelReader excelReader = ExcelUtil.getReader(ResourceUtil.getStream("initial-data.xlsx"));
            List<String> sheetNames = excelReader.getSheetNames();
            for (String sheetName : sheetNames) {
                String invokeMethodName = StrUtil.format("import{}s", StrUtil.upperFirst(sheetName));
                Method method = ReflectUtil.getMethod(InitializeDataRunner.class, invokeMethodName, ExcelReader.class);
                if (ObjectUtil.isNotNull(method)) {
                    excelReader.setIgnoreEmptyRow(true);
                    excelReader.setSheet(sheetName);
                    Object result = ReflectUtil.invoke(this, method, excelReader);
                    if (result instanceof Integer) {
                        count += (Integer) result;
                    }
                }
            }
            log.info("Complete the initial data import, import a total of {} data", count);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private Integer importDepartments(ExcelReader reader) {
        int count = 0;
        try {
            List<DepartmentEntity> entities = reader.read(0, 1, DepartmentEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                DepartmentService departmentService = MarvelSpringBootContext.getBean(DepartmentService.class);
                for (DepartmentEntity entity : entities) {
                    if (!departmentService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        departmentService.save(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }

    private Integer importPositions(ExcelReader reader) {
        int count = 0;
        try {
            List<PositionEntity> entities = reader.read(0, 1, PositionEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                PositionService positionService = MarvelSpringBootContext.getBean(PositionService.class);
                for (PositionEntity entity : entities) {
                    if (!positionService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        positionService.save(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }

    private Integer importMenus(ExcelReader reader) {
        int count = 0;
        try {
            List<MenuEntity> entities = reader.read(0, 1, MenuEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                MenuService menuService = MarvelSpringBootContext.getBean(MenuService.class);
                for (MenuEntity entity : entities) {
                    if (!menuService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        menuService.save(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }

    private Integer importPermissions(ExcelReader reader) {
        int count = 0;
        try {
            List<PermissionEntity> entities = reader.read(0, 1, PermissionEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                PermissionService permissionService = MarvelSpringBootContext.getBean(PermissionService.class);
                for (PermissionEntity entity : entities) {
                    if (!permissionService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        permissionService.save(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }

    private Integer importRoles(ExcelReader reader) {
        int count = 0;
        try {
            List<RoleEntity> entities = reader.read(0, 1, RoleEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                RoleService roleService = MarvelSpringBootContext.getBean(RoleService.class);
                for (RoleEntity entity : entities) {
                    if (!roleService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        roleService.save(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }

    private Integer importUsers(ExcelReader reader) {
        int count = 0;
        try {
            List<UserEntity> entities = reader.read(0, 1, UserEntity.class);
            if (ObjectUtil.isNotNull(entities)) {
                UserService userService = MarvelSpringBootContext.getBean(UserService.class);
                for (UserEntity entity : entities) {
                    if (!userService.isExistent(entity.getId())) {
                        entity.setCreateTime(System.currentTimeMillis());
                        userService.saveUser(entity);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return count;
    }
}
