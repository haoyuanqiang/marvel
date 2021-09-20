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

package org.marvel.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.marvel.auth.model.MarvelResponse;
import org.marvel.auth.model.Permission;
import org.marvel.auth.model.Role;
import org.marvel.auth.service.FeignBasisService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@Slf4j
public class FeignBasisServiceImpl implements FeignBasisService {

    @Override
    public MarvelResponse<Boolean> validateUser(@RequestParam String username, @RequestParam String password) {
        log.error("Exception occurred when call {}.[{}]", "validateUser", username);
        return null;
    }

    @Override
    public MarvelResponse<List<Role>> getUserRoleIds(@RequestParam String username) {
        log.error("Exception occurred when call {}.[{}]", "getUserRoleIds", username);
        return null;
    }

    @Override
    public MarvelResponse<List<Permission>> getPermissions(@RequestParam List<String> roleIds) {
        log.error("Exception occurred when call {}.[{}]", "getUserRoleIds", roleIds);
        return null;
    }
}
