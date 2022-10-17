package org.marvel.deevy.foundation.controller.organization;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.foundation.entity.organization.Position;
import org.marvel.deevy.foundation.param.PositionParam;
import org.marvel.deevy.foundation.service.organization.PositionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/1/2 13:11
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/position")
public class PositionController {

    @Resource
    private PositionService positionService;

    /**
     * 获取岗位树
     *
     * @return 岗位树结构
     */
    @GetMapping("/tree")
    public List<PositionParam> getPositionTree() {
        List<Position> entities = positionService.getAll();

        Map<Object, PositionParam> tmpMap = new LinkedHashMap<>(entities.size());

        for (Position entity : entities) {
            PositionParam param = BeanUtil.copyProperties(entity, PositionParam.class);
            tmpMap.put(entity.getId(), param);
        }
        List<PositionParam> result = new ArrayList<>();
        for (Position entity : entities) {
            PositionParam node = tmpMap.get(entity.getParentId());
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
    public List<PositionParam> getPositionList() {
        List<Position> entities = positionService.getAll();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                PositionParam.class
        ), true);
    }


    /**
     * 获取岗位信息
     *
     * @param positionId 岗位ID
     * @return 岗位信息
     */
    @GetMapping
    public PositionParam getPosition(@RequestParam("id") String positionId) {
        Position entity = positionService.getById(positionId);
        return BeanUtil.copyProperties(entity, PositionParam.class);
    }

    /**
     * 新增岗位
     *
     * @param position 岗位
     * @return 岗位ID
     */
    @PostMapping
    public String addPosition(@RequestBody PositionParam position) {
        int validateStatus = position.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (positionService.isDuplicate(position.getCode(), null, null)) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (positionService.isDuplicate(null, position.getName(), null)) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(position.getParentId()) && !positionService.isExistent(position.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        Position entity = BeanUtil.copyProperties(position, Position.class, BaseEntity.Fields.createTime);
        entity.setId(null);
        positionService.save(entity);
        return entity.getId();
    }

    /**
     * 修改岗位
     *
     * @param position 岗位
     */
    @PutMapping
    public void updatePosition(@RequestBody PositionParam position) {
        int validateStatus = position.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        Position entity = positionService.getById(position.getId());
        if (null == entity) {
            // nonexistent entity
            throw ExceptionFactory.create(1126);
        }
        if (positionService.isDuplicate(position.getCode(), null, position.getId())) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (positionService.isDuplicate(null, position.getName(), position.getId())) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(position.getParentId()) && !positionService.isExistent(position.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        BeanUtil.copyProperties(position, entity, CopyOptions.create().ignoreNullValue().setIgnoreProperties(
                BaseEntity.Fields.createTime, BaseEntity.Fields.modifyTime));
        positionService.save(entity);
    }

    /**
     * 软删除岗位
     *
     * @param positionIds 岗位ID列表
     */
    @PutMapping("/invalidation")
    public int invalidatePositions(@RequestBody List<String> positionIds) {
        if (ObjectUtil.isNotNull(positionIds)) {
            return positionService.invalidate(positionIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个岗位
     *
     * @param positionIds 岗位ID
     */
    @DeleteMapping("/{ids}")
    public int deletePosition(@PathVariable("ids") List<String> positionIds) {
        if (CollectionUtil.isNotEmpty(positionIds)) {
            return positionService.delete(positionIds);
        }
        return 0;
    }
}
