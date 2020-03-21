package uk.gov.nhs.sts.model.dto;

import javax.validation.constraints.NotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "skill", description = "The skill model")
public class SkillDTO {

  @NotBlank(message = "Name should be present")
  @ApiModelProperty(value = "The skill name", required = true)
  private String name;

}
