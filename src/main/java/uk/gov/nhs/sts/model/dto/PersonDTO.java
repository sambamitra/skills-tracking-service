package uk.gov.nhs.sts.model.dto;

import java.util.List;
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
@ApiModel(value = "person_with_skills",
    description = "This holds data about a person with a list of skills they have")
public class PersonDTO {

  @NotBlank(message = "Name should be present")
  @ApiModelProperty(value = "The person name", required = true)
  private String name;

  @NotBlank(message = "Staff number should be present")
  @ApiModelProperty(value = "The staff number", required = true)
  private String staffNumber;

  @ApiModelProperty(value = "The list of skills for the person")
  private List<PersonSkillDTO> personSkills;

}
