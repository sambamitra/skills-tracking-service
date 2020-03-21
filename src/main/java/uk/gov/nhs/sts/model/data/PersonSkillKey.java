package uk.gov.nhs.sts.model.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @Embeddable
public class PersonSkillKey implements Serializable {

  private static final long serialVersionUID = 1L;

  // @Column(name = "person_id")
  private Person person;

  // @Column(name = "skill_id")
  private Skill skill;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    PersonSkillKey key = (PersonSkillKey) obj;

    if (person != null ? !person.equals(key.getPerson()) : key.getPerson() != null) {
      return false;
    }
    return skill != null ? skill.equals(key.getSkill()) : key.getSkill() == null;
  }

  @Override
  public int hashCode() {
    int result = person != null ? person.hashCode() : 0;
    result = 31 * result + (skill != null ? skill.hashCode() : 0);
    return result;
  }


}
