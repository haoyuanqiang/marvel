package org.marvel.deevy.foundation.entity.association;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.marvel.deevy.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_ASSOCIATION_USER_GROUP_AND_USER", indexes = {
        @Index(name = "MA_IN_GU_USER_GROUP_ID", columnList = "USER_GROUP_ID"),
        @Index(name = "MA_IN_GU_USER_ID", columnList = "USER_ID"),
        @Index(name = "MA_IN_GU_VALID", columnList = "_VALID")
})
public class UserGroupAndUserAssociation extends BaseEntity {

    @Column(name = "USER_GROUP_ID")
    private String userGroupId;

    @Column(name = "USER_ID")
    private String userId;
}
