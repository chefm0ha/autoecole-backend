package com.autoecole.repository;

import com.autoecole.model.ApplicationFile;
import com.autoecole.model.Candidate;
import com.autoecole.model.Category;
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
