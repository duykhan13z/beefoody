package com.example.foodordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchFoodRequest {
    @NotBlank(message = "Keyword must not be empty")
    private String keyword;

    private String sortBy;
}
