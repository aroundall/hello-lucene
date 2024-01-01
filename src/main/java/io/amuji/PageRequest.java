package io.amuji;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    private int page = 0;
    private int size = 5;

    public int getStart() {
        return page * size;
    }

    public int getEnd() {
        return getStart() + this.size;
    }
}
