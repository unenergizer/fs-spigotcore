package com.forgestorm.spigotcore.features.required.database.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SqlSearchData {
    private final String tableName;
    private final String columnName;
    private final Object setData;
}
