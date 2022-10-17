package org.marvel.deevy.gateway.model;

import lombok.Data;

/**
 * @author haoyuanqiang
 * @date 2022/9/9 16:54
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Data
public class User {

    private String id;


    private String name;


    private String departmentId;


    private String positionId;


    private Long logoutDuration;

}
