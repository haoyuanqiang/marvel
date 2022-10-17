package org.marvel.deevy.foundation.controller.authority;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.marvel.deevy.foundation.param.UserGroupParam;
import org.marvel.deevy.foundation.service.security.UserGroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/2/28 20:22
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/user-group")
public class UserGroupController {

    @Resource
    private UserGroupService userGroupService;


    /**
     * 获取岗位树
     *
     * @return 岗位树结构
     */
    @GetMapping("/tree")
    public List<UserGroupParam> getUserGroupTree() {
        List<UserGroup> entities = userGroupService.getAll();

        Map<Object, UserGroupParam> tmpMap = new LinkedHashMap<>(entities.size());

        for (UserGroup entity : entities) {
            UserGroupParam param = BeanUtil.copyProperties(entity, UserGroupParam.class);
            tmpMap.put(entity.getId(), param);
        }
        List<UserGroupParam> result = new ArrayList<>();
        for (UserGroup entity : entities) {
            UserGroupParam node = tmpMap.get(entity.getParentId());
            if (ObjectUtil.isNotNull(node) && CompareUtil.compare(entity.getId(), entity.getParentId()) != 0) {
                node.addChildNode(tmpMap.get(entity.getId()));
            } else {
                result.add(tmpMap.get(entity.getId()));
            }
        }
        return result;
    }

    /**
     * 获取岗位列表
     *
     * @return 全部岗位
     */
    @GetMapping("/list")
    public List<UserGroupParam> getUserGroupList() {
        List<UserGroup> entities = userGroupService.getAll();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                UserGroupParam.class
        ), true);
    }


    /**
     * 获取岗位信息
     *
     * @param userGroupId 岗位ID
     * @return 岗位信息
     */
    @GetMapping
    public UserGroupParam getUserGroup(@RequestParam("id") String userGroupId) {
        UserGroup entity = userGroupService.getById(userGroupId);
        return BeanUtil.copyProperties(entity, UserGroupParam.class);
    }

    /**
     * 新增岗位
     *
     * @param userGroup 岗位
     * @return 岗位ID
     */
    @PostMapping
    public String addUserGroup(@RequestBody UserGroupParam userGroup) {
        int validateStatus = userGroup.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (userGroupService.isDuplicate(userGroup.getCode(), null, null)) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (userGroupService.isDuplicate(null, userGroup.getName(), null)) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(userGroup.getParentId()) && !userGroupService.isExistent(userGroup.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        UserGroup entity = BeanUtil.copyProperties(userGroup, UserGroup.class, BaseEntity.Fields.createTime);
        entity.setId(null);
        userGroupService.save(entity);
        return entity.getId();
    }

    /**
     * 修改岗位
     *
     * @param userGroup 岗位
     */
    @PutMapping
    public void updateUserGroup(@RequestBody UserGroupParam userGroup) {
        int validateStatus = userGroup.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        UserGroup entity = userGroupService.getById(userGroup.getId());
        if (null == entity) {
            // nonexistent entity
            throw ExceptionFactory.create(1126);
        }
        if (userGroupService.isDuplicate(userGroup.getCode(), null, userGroup.getId())) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (userGroupService.isDuplicate(null, userGroup.getName(), userGroup.getId())) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(userGroup.getParentId()) && !userGroupService.isExistent(userGroup.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        BeanUtil.copyProperties(userGroup, entity, CopyOptions.create().ignoreNullValue().setIgnoreProperties(
                BaseEntity.Fields.createTime, BaseEntity.Fields.modifyTime));
        userGroupService.save(entity);
    }

    /**
     * 软删除岗位
     *
     * @param userGroupIds 岗位ID列表
     */
    @PutMapping("/invalidation")
    public int invalidateUserGroups(@RequestBody List<String> userGroupIds) {
        if (ObjectUtil.isNotNull(userGroupIds)) {
            return userGroupService.invalidate(userGroupIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个岗位
     *
     * @param userGroupIds 岗位ID
     */
    @DeleteMapping("/{ids}")
    public int deleteUserGroup(@PathVariable("ids") List<String> userGroupIds) {
        if (CollectionUtil.isNotEmpty(userGroupIds)) {
            return userGroupService.delete(userGroupIds);
        }
        return 0;
    }
}
