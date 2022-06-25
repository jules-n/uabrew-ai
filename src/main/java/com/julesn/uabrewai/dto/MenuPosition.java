package com.julesn.uabrewai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuPosition {
    @Nullable
    private List<Characteristic> characteristics;
    @Nullable
    private List<Component> components;
    private Integer price;
    private String name;
    private Integer amount;
}
