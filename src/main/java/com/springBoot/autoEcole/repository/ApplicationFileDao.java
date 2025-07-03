package com.springBoot.autoEcole.repository;

import com.springBoot.autoEcole.model.ApplicationFile;
import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ApplicationFileDao extends CrudRepository<ApplicationFile, Long> {
    Long removeById(Long id);
    ApplicationFile findByCandidateAndCategory(Candidate candidate, Category category);
    List<ApplicationFile> findByCandidate(Candidate candidate);

    @Modifying
    @Query(value = "CALL cancel_application_file(:p_application_file_id)", nativeQuery = true)
    void cancelApplicationFile(@Param("p_application_file_id") Long applicationFileId);

    @Modifying
    @Query(value = "CALL save_application_file_with_validation(:p_candidate_cin, :p_category_code, :p_total_amount, :p_initial_amount, @p_application_file_id)", nativeQuery = true)
    void saveApplicationFileWithValidation(
            @Param("p_candidate_cin") String candidateCin,
            @Param("p_category_code") String categoryCode,
            @Param("p_total_amount") Integer totalAmount,
            @Param("p_initial_amount") Integer initialAmount
    );

    @Query(value = "SELECT @p_application_file_id", nativeQuery = true)
    Long getLastApplicationFileId();
}
