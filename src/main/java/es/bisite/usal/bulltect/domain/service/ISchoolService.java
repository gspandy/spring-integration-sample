package es.bisite.usal.bulltect.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.bisite.usal.bulltect.web.dto.request.AddSchoolDTO;
import es.bisite.usal.bulltect.web.dto.response.SchoolDTO;

public interface ISchoolService {
	Page<SchoolDTO> findPaginated(Integer page, Integer size);
    Page<SchoolDTO> findPaginated(Pageable pageable);
    Page<SchoolDTO> findByNamePaginated(String name, Pageable pageable);
    Iterable<String> getAllSchoolNames();
    SchoolDTO getSchoolById(String id);
    SchoolDTO save(AddSchoolDTO addSchoolDTO);
    SchoolDTO delete(String id);
}
