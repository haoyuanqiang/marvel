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

package org.marvel.auth.service;

import org.marvel.auth.model.MarvelResponse;
import org.marvel.auth.model.Permission;
import org.marvel.auth.model.Role;
import org.marvel.auth.service.impl.FeignBasisServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "marvel-admin", fallback = FeignBasisServiceImpl.class)
public interface FeignBasisService {

    @PostMapping("/auth/user/validation")
    MarvelResponse<Boolean> validateUser(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password);

    @GetMapping("/auth/user/roles")
    MarvelResponse<List<Role>> getUserRoleIds(@RequestParam(value = "username") String username);

    @GetMapping("/auth/permissions")
    MarvelResponse<List<Permission>> getPermissions(@RequestParam(value = "roleIds") List<String> roleIds);
}
