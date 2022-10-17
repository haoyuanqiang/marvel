package org.marvel.deevy.foundation.service.organization;

import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.organization.Position;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public interface PositionService extends BaseService<Position> {


    boolean isDuplicate(String code, String name, String ignorantId);
}
