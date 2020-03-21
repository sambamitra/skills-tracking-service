package uk.gov.nhs.sts.model.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
@Entity
@Table(name = "person_skill")
@IdClass(PersonSkillKey.class)
public class PersonSkill {

  @Id
  @ManyToOne
  @JoinColumn(name = "person_id")
  private Person person;

  @Id
  @ManyToOne
  @JoinColumn(name = "skill_id")
  private Skill skill;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private SkillLevel level;

}
