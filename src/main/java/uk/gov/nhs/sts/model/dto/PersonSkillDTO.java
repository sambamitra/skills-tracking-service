package uk.gov.nhs.sts.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.nhs.sts.model.data.SkillLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "person_skill", description = "The skill of a person")
public class PersonSkillDTO {

  @ApiModelProperty(value = "The skill name")
  private String skillName;

  @ApiModelProperty(value = "The skill level")
  private SkillLevel skillLevel;
}
