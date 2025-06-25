package com.springBoot.autoEcole.repository;

import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ApplicationFileDao extends CrudRepository<ApplicationFile, Long> {
    Long removeById(Long id);
    ApplicationFile findByCandidateAndCategory(Candidate candidate, Category category);
    List<ApplicationFile> findByCandidate(Candidate candidate);
}
