package uk.gov.nhs.sts.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.nhs.sts.model.dto.SkillDTO;
import uk.gov.nhs.sts.service.PeopleManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/skills")
@Api(value = "/api/v1/skills", tags = {"Skills API"}, protocols = "HTTP")
public class SkillController {

  private final PeopleManagementService service;

  @GetMapping(value = "/{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
  @ApiOperation(value = "Gets a skill's details",
      notes = "This endpoint fetches a skill's details based on the name.",
      response = SkillDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Skill details retrieved"),
      @ApiResponse(code = 404, message = "Skill not found"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<SkillDTO> getSkill(@ApiParam(value = "Name of the skill to be created",
      required = true) @PathVariable("name") final String name) {
    final SkillDTO skill = this.service.fetchSkillByName(name);
    if (skill != null) {
      return new ResponseEntity<>(skill, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping
  @ApiOperation(value = "Gets all the skill details",
      notes = "This endpoint fetches all the skill details.", response = List.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Skills retrieved"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<List<SkillDTO>> getSkills() {
    final List<SkillDTO> skills = this.service.fetchSkills();
    return new ResponseEntity<>(skills, HttpStatus.OK);
  }

  @ApiOperation(value = "Creates a new skill", notes = "This endpoint creates a new skill",
      response = SkillDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 201, message = "Skill created"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 409, message = "Skill with provided name already exists"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<SkillDTO> createSkill(@Valid @RequestBody final SkillDTO skillDto,
      final BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (this.service.fetchSkillByName(skillDto.getName()) != null) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    this.service.createOrUpdateSkill(skillDto, false);

    return new ResponseEntity<>(skillDto, HttpStatus.CREATED);
  }

  @PutMapping("/{name}")
  @ApiOperation(value = "Updates a skill's details",
      notes = "This endpoint updates the details of a skill.", response = SkillDTO.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Skill updated successfully"),
      @ApiResponse(code = 404, message = "Skill not found"),
      @ApiResponse(code = 400, message = "Bad request"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<SkillDTO> updateSkill(
      @ApiParam(value = "Name of the skill to be updated",
          required = true) @PathVariable("name") final String name,
      @Valid @RequestBody final SkillDTO skillDto, final BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (this.service.fetchSkillByName(name) == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    this.service.createOrUpdateSkill(skillDto, true);

    return new ResponseEntity<>(skillDto, HttpStatus.OK);
  }

  @DeleteMapping("/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiOperation(value = "Deletes a skill",
      notes = "This endpoint deletes a skill and it's association(s) with any person.",
      response = Void.class)
  @ApiResponses(value = {@ApiResponse(code = 204, message = "Skill deleted"),
      @ApiResponse(code = 404, message = "Skill not found"),
      @ApiResponse(code = 500, message = "Error while processing the request")})
  public ResponseEntity<Void> deletePerson(@ApiParam(value = "Name of the skill to be deleted",
      required = true) @PathVariable("name") final String name) {

    final SkillDTO skill = this.service.fetchSkillByName(name);
    if (skill != null) {
      this.service.deleteSkill(skill);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
