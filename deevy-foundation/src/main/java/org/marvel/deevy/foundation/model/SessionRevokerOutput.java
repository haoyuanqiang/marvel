package org.marvel.deevy.foundation.model;

import lombok.Data;
import org.marvel.deevy.foundation.entity.security.Session;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/10/14 14:13
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Data
public class SessionRevokerOutput {

    private int count;


    private List<Session> revokedSession;
}
