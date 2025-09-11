package com.autoecole.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import com.autoecole.dto.response.CandidateListDTO;
import com.autoecole.model.Candidate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CandidateMapper {

    /**
     * Convert a Page<Candidate> to Page<CandidateListDTO>
     * This removes all the unnecessary data (relations, null fields, etc.)
     */
    public Page<CandidateListDTO> toListDTOPage(Page<Candidate> candidatePage) {
        List<CandidateListDTO> dtoList = candidatePage.getContent()
                .stream()
                .map(CandidateListDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, candidatePage.getPageable(), candidatePage.getTotalElements());
    }

    /**
     * Convert a single Candidate to CandidateListDTO
     */
    public CandidateListDTO toListDTO(Candidate candidate) {
        return CandidateListDTO.fromEntity(candidate);
    }

    /**
     * Convert a List<Candidate> to List<CandidateListDTO>
     */
    public List<CandidateListDTO> toListDTOList(List<Candidate> candidates) {
        return candidates.stream()
                .map(CandidateListDTO::fromEntity)
                .collect(Collectors.toList());
    }
}