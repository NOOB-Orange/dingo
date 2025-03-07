/*
 * Copyright 2021 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dingodb.calcite.operation;

import org.apache.calcite.avatica.AvaticaConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class KillQuery implements DdlOperation {
    private String threadId;
    private Map<String, Connection> connectionMap;

    public KillQuery(String threadId) {
        this.threadId = threadId;
    }

    public void init(Object connectionMap) {
        this.connectionMap = (Map<String, Connection>) connectionMap;
    }

    @Override
    public void execute() {
        Connection connection = connectionMap.get(threadId);
        AvaticaConnection avaticaConnection = (AvaticaConnection) connection;
        if (avaticaConnection.statementMap != null) {
            avaticaConnection.statementMap.forEach((k, v) -> {
                try {
                    v.cancel();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
