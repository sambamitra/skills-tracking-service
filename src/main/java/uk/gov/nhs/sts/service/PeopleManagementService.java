package uk.gov.nhs.sts.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.nhs.sts.model.data.Person;
import uk.gov.nhs.sts.model.data.PersonSkill;
import uk.gov.nhs.sts.model.data.Skill;
import uk.gov.nhs.sts.model.dto.PersonDTO;
import uk.gov.nhs.sts.model.dto.PersonSkillDTO;
import uk.gov.nhs.sts.model.dto.SkillDTO;
import uk.gov.nhs.sts.repository.PersonRepository;
import uk.gov.nhs.sts.repository.SkillRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeopleManagementService {

  private final PersonRepository personRepository;
  private final SkillRepository skillRepository;

  public PersonDTO fetchPersonByStaffNumber(final String staffNumber) {
    final Person person = getPerson(staffNumber);
    if (person != null && person.getPersonSkills() != null) {
      final List<PersonSkillDTO> personSkills = person.getPersonSkills().stream()
          .map(personSkill -> PersonSkillDTO.builder().skillName(personSkill.getSkill().getName())
              .skillLevel(personSkill.getLevel()).build())
          .collect(Collectors.toList());

      return PersonDTO.builder().name(person.getName()).personSkills(personSkills).build();
    }
    return null;
  }

  public SkillDTO fetchSkillByName(final String skillName) {
    final Skill skill = getSkill(skillName);
    if (skill != null) {
      return SkillDTO.builder().name(skill.getName()).build();
    }
    return null;
  }

  public List<PersonDTO> fetchPeople() {
    final List<Person> people = this.personRepository.findAll();
    final List<PersonDTO> personDtos = new ArrayList<>();

    for (Person person : people) {
      final List<PersonSkillDTO> personSkillDtos = person.getPersonSkills().stream()
          .map(personSkill -> PersonSkillDTO.builder().skillName(personSkill.getSkill().getName())
              .skillLevel(personSkill.getLevel()).build())
          .collect(Collectors.toList());

      final PersonDTO personDto = PersonDTO.builder().staffNumber(person.getStaffNumber())
          .name(person.getName()).personSkills(personSkillDtos).build();
      personDtos.add(personDto);
    }
    return personDtos;
  }

  public List<SkillDTO> fetchSkills() {
    final List<Skill> skills = this.skillRepository.findAll();
    return skills.stream().map(skill -> SkillDTO.builder().name(skill.getName()).build())
        .collect(Collectors.toList());
  }

  private Person getPerson(final String staffNumber) {
    return this.personRepository.findByStaffNumber(staffNumber);
  }

  private Skill getSkill(final String skillName) {
    return this.skillRepository.findByName(skillName);
  }

  @Transactional
  public void createOrUpdatePersonWithSkills(final PersonDTO personDto, final boolean update) {
    final Person person;
    if (update) {
      person = this.getPerson(personDto.getStaffNumber());
    } else {
      person = Person.builder().name(personDto.getName()).staffNumber(personDto.getStaffNumber())
          .build();
      this.personRepository.saveAndFlush(person);
    }

    if (personDto.getPersonSkills() != null && !personDto.getPersonSkills().isEmpty()) {
      final Set<PersonSkill> personSkills = new HashSet<>();
      for (PersonSkillDTO personSkillDto : personDto.getPersonSkills()) {
        final PersonSkill personSkill;
        final Skill skill = this.skillRepository.findByName(personSkillDto.getSkillName());
        if (skill != null) {
          log.info("Skill with name {} already exists, so creating the association with person",
              skill.getName());
          personSkill = PersonSkill.builder().person(person).skill(skill)
              .level(personSkillDto.getSkillLevel()).build();
        } else {
          log.info("Skill with name {} doesn't exist, so creating it first",
              personSkillDto.getSkillName());
          final Skill persistedSkill = Skill.builder().name(personSkillDto.getSkillName()).build();
          this.skillRepository.saveAndFlush(persistedSkill);
          log.info("Creating the association with person");
          personSkill = PersonSkill.builder().person(person).skill(persistedSkill)
              .level(personSkillDto.getSkillLevel()).build();
        }
        if (personSkill != null) {
          personSkills.add(personSkill);
        }
      }
      if (person.getPersonSkills() != null) {
        person.getPersonSkills().clear();
        person.getPersonSkills().addAll(personSkills);
      } else {
        person.setPersonSkills(personSkills);
      }

    }
    this.personRepository.saveAndFlush(person);
  }

  @Transactional
  public void createOrUpdateSkill(final SkillDTO skillDto, final boolean update) {
    final Skill skill;
    if (update) {
      skill = this.getSkill(skillDto.getName());
    } else {
      skill = Skill.builder().name(skillDto.getName()).build();
    }
    this.skillRepository.save(skill);
  }


  @Transactional
  public void deletePerson(final PersonDTO personDto) {
    final Person person = getPerson(personDto.getStaffNumber());
    this.personRepository.delete(person);
  }

  @Transactional
  public void deleteSkill(final SkillDTO skillDto) {
    final Skill skill = getSkill(skillDto.getName());
    this.skillRepository.delete(skill);
  }

}
