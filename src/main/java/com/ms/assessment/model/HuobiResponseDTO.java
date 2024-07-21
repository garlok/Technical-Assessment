package com.ms.assessment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HuobiResponseDTO {

    public List<HuobiDTO> data;
}
