package com.forgestorm.spigotcore.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventorySize {

    ROWS_1(9),
    ROWS_2(18),
    ROWS_3(27),
    ROWS_4(36),
    ROWS_5(45),
    ROWS_6(54);

    private final int size;
}
